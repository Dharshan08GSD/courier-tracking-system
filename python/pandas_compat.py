"""
pandas_compat.py
Graceful compatibility layer for pandas and seaborn.
If real pandas and seaborn are installed and functional, they are used directly.
If an environment blocks C-extension DLLs (such as Windows Application Control policy),
this provides a pure-Python DataFrame and palette fallback so that data retrieval,
preprocessing, CSV export, and chart generation run without interruption.
"""

import csv
import os
from collections import Counter
from datetime import datetime

USE_REAL_PANDAS = False

try:
    import pandas as real_pd
    import seaborn as real_sns
    # Verify pandas is truly functional (doesn't fail on DLL load)
    _test_df = real_pd.DataFrame({"a": [1]})
    USE_REAL_PANDAS = True
except Exception:
    USE_REAL_PANDAS = False


if USE_REAL_PANDAS:
    pd = real_pd
    sns = real_sns
    NaT = real_pd.NaT
else:
    NaT = None

    class DateAccessor:
        def __init__(self, series):
            self.series = series

        @property
        def days(self):
            vals = []
            for v in self.series.values:
                if v is None:
                    vals.append(None)
                elif hasattr(v, 'days'):
                    vals.append(v.days)
                elif isinstance(v, (int, float)):
                    vals.append(int(v))
                else:
                    vals.append(None)
            return Series(vals, index=self.series.index)

        def to_period(self, freq="M"):
            vals = []
            for v in self.series.values:
                if isinstance(v, datetime):
                    vals.append(v.strftime("%Y-%m"))
                elif isinstance(v, str) and len(v) >= 7:
                    vals.append(v[:7])
                else:
                    vals.append(None)
            return Series(vals, index=self.series.index)

        def to_timestamp(self):
            vals = []
            for v in self.series.values:
                if isinstance(v, str):
                    try:
                        vals.append(datetime.strptime(v, "%Y-%m"))
                    except Exception:
                        vals.append(v)
                elif isinstance(v, datetime):
                    vals.append(v)
                else:
                    vals.append(v)
            return Series(vals, index=self.series.index)

    class Series:
        def __init__(self, values, index=None, name=None):
            self.values = list(values)
            self.index = list(index) if index is not None else list(range(len(self.values)))
            self.name = name

        def __len__(self):
            return len(self.values)

        def __getitem__(self, item):
            if isinstance(item, int):
                return self.values[item]
            if isinstance(item, list) and all(isinstance(x, bool) for x in item):
                new_vals = [v for v, m in zip(self.values, item) if m]
                new_idx = [i for i, m in zip(self.index, item) if m]
                return Series(new_vals, index=new_idx)
            if item in self.index:
                idx = self.index.index(item)
                return self.values[idx]
            raise KeyError(item)

        def __sub__(self, other):
            res = []
            for a, b in zip(self.values, other.values):
                if a is not None and b is not None:
                    if isinstance(a, str):
                        try:
                            a = datetime.fromisoformat(a)
                        except Exception:
                            pass
                    if isinstance(b, str):
                        try:
                            b = datetime.fromisoformat(b)
                        except Exception:
                            pass
                    if hasattr(a, '__sub__'):
                        res.append(a - b)
                    else:
                        res.append(None)
                else:
                    res.append(None)
            return Series(res, index=self.index)

        def __gt__(self, other):
            return Series([v > other if v is not None else False for v in self.values], index=self.index)

        def __eq__(self, other):
            return Series([v == other for v in self.values], index=self.index)

        @property
        def dt(self):
            return DateAccessor(self)

        def __and__(self, other):
            other_vals = other.values if isinstance(other, Series) else other
            return Series([bool(a and b) for a, b in zip(self.values, other_vals)], index=self.index)

        def __or__(self, other):
            other_vals = other.values if isinstance(other, Series) else other
            return Series([bool(a or b) for a, b in zip(self.values, other_vals)], index=self.index)

        def notna(self):
            return Series([v is not None for v in self.values], index=self.index)

        def dropna(self):
            filtered = [(i, v) for i, v in zip(self.index, self.values) if v is not None]
            return Series([v for _, v in filtered], index=[i for i, _ in filtered])

        def value_counts(self):
            counts = Counter(v for v in self.values if v is not None)
            sorted_items = sorted(counts.items(), key=lambda x: x[1], reverse=True)
            return Series([v for _, v in sorted_items], index=[k for k, _ in sorted_items])

        def mean(self):
            nums = [v for v in self.values if isinstance(v, (int, float))]
            return sum(nums) / len(nums) if nums else 0.0

        def max(self):
            nums = [v for v in self.values if isinstance(v, (int, float))]
            return max(nums) if nums else 0

        def sum(self):
            nums = [v for v in self.values if isinstance(v, (int, float, bool))]
            return sum(nums)

        def items(self):
            return zip(self.index, self.values)

        def reindex(self, new_index):
            mapping = dict(zip(self.index, self.values))
            new_vals = [mapping.get(k, None) for k in new_index]
            return Series(new_vals, index=new_index)

        def astype(self, typ):
            return Series([typ(v) if v is not None else "" for v in self.values], index=self.index)

        def __add__(self, other):
            if isinstance(other, str):
                return Series([str(v) + other if v is not None else other for v in self.values], index=self.index)
            if isinstance(other, Series):
                return Series([a + b for a, b in zip(self.values, other.values)], index=self.index)
            return Series([v + other for v in self.values], index=self.index)

        def __radd__(self, other):
            if isinstance(other, str):
                return Series([other + str(v) if v is not None else other for v in self.values], index=self.index)
            return Series([other + v for v in self.values], index=self.index)

        def fillna(self, val):
            if isinstance(val, Series):
                return Series([b if a is None else a for a, b in zip(self.values, val.values)], index=self.index)
            return Series([val if v is None else v for v in self.values], index=self.index)

    class LocIndexer:
        def __init__(self, df):
            self.df = df

        def __setitem__(self, key, value):
            mask, col = key
            if col not in self.df._columns:
                self.df._columns.append(col)
                self.df._data[col] = [None] * len(self.df)
            col_data = self.df._data[col]
            if isinstance(value, Series):
                val_list = value.values
                val_idx = 0
                for i, m in enumerate(mask):
                    if m and val_idx < len(val_list):
                        col_data[i] = val_list[val_idx]
                        val_idx += 1
            elif isinstance(value, (list, tuple)):
                val_idx = 0
                for i, m in enumerate(mask):
                    if m and val_idx < len(value):
                        col_data[i] = value[val_idx]
                        val_idx += 1
            else:
                for i, m in enumerate(mask):
                    if m:
                        col_data[i] = value

        def __getitem__(self, key):
            mask, col = key
            vals = [self.df._data[col][i] for i, m in enumerate(mask) if m]
            return Series(vals)

    class DataFrame:
        def __init__(self, data=None, columns=None):
            self._columns = list(columns) if columns else []
            self._data = {}
            if isinstance(data, dict):
                self._columns = list(data.keys())
                self._data = {k: list(v) for k, v in data.items()}
            elif isinstance(data, list):
                if data and isinstance(data[0], dict):
                    self._columns = list(data[0].keys())
                    self._data = {col: [row.get(col) for row in data] for col in self._columns}
                elif data and isinstance(data[0], (list, tuple)):
                    if not self._columns:
                        self._columns = [f"col_{i}" for i in range(len(data[0]))]
                    self._data = {col: [row[i] for row in data] for i, col in enumerate(self._columns)}
                else:
                    self._data = {col: [] for col in self._columns}
            else:
                self._data = {col: [] for col in self._columns}

        @property
        def loc(self):
            return LocIndexer(self)

        @property
        def columns(self):
            return list(self._columns)

        @property
        def empty(self):
            return len(self) == 0

        def __len__(self):
            if not self._data:
                return 0
            first_col = next(iter(self._data.values()))
            return len(first_col)

        def __getitem__(self, item):
            if isinstance(item, str):
                if item in self._data:
                    return Series(self._data[item], name=item)
                raise KeyError(item)
            elif isinstance(item, list) and item and isinstance(item[0], str):
                new_data = {col: list(self._data[col]) for col in item if col in self._data}
                return DataFrame(new_data, columns=item)
            elif isinstance(item, (list, Series)) and len(item) == len(self):
                mask = item.values if isinstance(item, Series) else item
                filtered_data = {
                    col: [v for i, v in enumerate(self._data[col]) if mask[i]]
                    for col in self._columns
                }
                return DataFrame(filtered_data, columns=self._columns)
            raise KeyError(item)

        def __setitem__(self, col, val):
            if col not in self._columns:
                self._columns.append(col)
            n = len(self)
            if isinstance(val, Series):
                self._data[col] = list(val.values)
            elif isinstance(val, (list, tuple)):
                self._data[col] = list(val)
            else:
                self._data[col] = [val] * n

        def copy(self):
            new_data = {k: list(v) for k, v in self._data.items()}
            return DataFrame(new_data, columns=list(self._columns))

        def select_dtypes(self, include=None):
            include = include or []
            selected = []
            for col in self._columns:
                vals = [v for v in self._data[col] if v is not None]
                if not vals:
                    continue
                first = vals[0]
                if "number" in include and isinstance(first, (int, float)):
                    selected.append(col)
                elif "object" in include and isinstance(first, str):
                    selected.append(col)
            return self[selected]

        def fillna(self, val):
            new_df = self.copy()
            for col in new_df._columns:
                new_df._data[col] = [val if v is None else v for v in new_df._data[col]]
            return new_df

        def dropna(self, subset=None):
            subset = subset or self._columns
            rows_to_keep = []
            for i in range(len(self)):
                if all(self._data[col][i] is not None for col in subset if col in self._data):
                    rows_to_keep.append(True)
                else:
                    rows_to_keep.append(False)
            return self[rows_to_keep]

        def rename(self, columns=None):
            columns = columns or {}
            new_cols = [columns.get(c, c) for c in self._columns]
            new_data = {columns.get(c, c): list(self._data[c]) for c in self._columns}
            return DataFrame(new_data, columns=new_cols)

        def merge(self, other, on=None, how="inner"):
            if on not in self._columns or on not in other._columns:
                return self.copy()
            other_rows = []
            for i in range(len(other)):
                other_rows.append({col: other._data[col][i] for col in other._columns})
            
            other_lookup = {}
            for row in other_rows:
                k = row.get(on)
                if k not in other_lookup:
                    other_lookup[k] = row

            new_cols = list(self._columns)
            for c in other._columns:
                if c != on and c not in new_cols:
                    new_cols.append(c)

            merged_rows = []
            for i in range(len(self)):
                row = {c: self._data[c][i] for c in self._columns}
                k = row.get(on)
                match = other_lookup.get(k, {})
                for c in other._columns:
                    if c != on:
                        row[c] = match.get(c, None)
                merged_rows.append(row)

            return DataFrame(merged_rows, columns=new_cols)

        def sort_values(self, by, ascending=True):
            if by not in self._columns:
                return self.copy()
            rows = []
            for i in range(len(self)):
                rows.append({c: self._data[c][i] for c in self._columns})
            rows.sort(key=lambda r: (r.get(by) is None, r.get(by)), reverse=not ascending)
            return DataFrame(rows, columns=self._columns)

        def groupby(self, by):
            return GroupBy(self, by)

        def pivot_table(self, index=None, columns=None, values=None, fill_value=0):
            row_keys = []
            col_keys = []
            matrix = {}
            for i in range(len(self)):
                r = self._data[index][i]
                c = self._data[columns][i]
                v = self._data[values][i] if values else 1
                if r not in row_keys:
                    row_keys.append(r)
                if c not in col_keys:
                    col_keys.append(c)
                matrix[(r, c)] = v
            return SimplePivotTable(row_keys, col_keys, values, matrix)

        def to_csv(self, path, index=False):
            os.makedirs(os.path.dirname(os.path.abspath(path)), exist_ok=True)
            with open(path, "w", newline="", encoding="utf-8") as f:
                writer = csv.writer(f)
                writer.writerow(self._columns)
                for i in range(len(self)):
                    writer.writerow([self._data[c][i] for c in self._columns])

    class GroupBy:
        def __init__(self, df, by):
            self.df = df
            self.by = [by] if isinstance(by, str) else list(by)

        def __getitem__(self, col):
            return SeriesGroupBy(self.df, self.by, col)

        def size(self):
            counts = {}
            for i in range(len(self.df)):
                key = tuple(self.df._data[b][i] for b in self.by) if len(self.by) > 1 else self.df._data[self.by[0]][i]
                counts[key] = counts.get(key, 0) + 1
            rows = []
            for k, count in counts.items():
                row = {}
                if len(self.by) > 1:
                    for b, val in zip(self.by, k):
                        row[b] = val
                else:
                    row[self.by[0]] = k
                row["size"] = count
                rows.append(row)
            return GroupByResult(rows, self.by)

        def last(self):
            groups = {}
            for i in range(len(self.df)):
                key = tuple(self.df._data[b][i] for b in self.by) if len(self.by) > 1 else self.df._data[self.by[0]][i]
                groups[key] = {c: self.df._data[c][i] for c in self.df._columns}
            return GroupByResult(list(groups.values()), self.by)

    class GroupByResult:
        def __init__(self, rows, by):
            self.rows = rows
            self.by = by

        def reset_index(self, name=None):
            if name and self.rows and "size" in self.rows[0]:
                for r in self.rows:
                    r[name] = r.pop("size")
            return DataFrame(self.rows)

    class SeriesGroupBy:
        def __init__(self, df, by, col):
            self.df = df
            self.by = by
            self.col = col

        def mean(self):
            groups = {}
            for i in range(len(self.df)):
                key = tuple(self.df._data[b][i] for b in self.by) if len(self.by) > 1 else self.df._data[self.by[0]][i]
                val = self.df._data[self.col][i]
                if isinstance(val, (int, float)):
                    if key not in groups:
                        groups[key] = []
                    groups[key].append(val)
            means = {k: sum(v) / len(v) for k, v in groups.items()}
            return Series(list(means.values()), index=list(means.keys()))

    class SimplePivotTable:
        def __init__(self, rows, cols, values, matrix):
            self.index = list(rows)
            self.columns = list(cols)
            self._matrix = matrix

        def __getitem__(self, col):
            return Series([self._matrix.get((r, col), 0) for r in self.index], index=self.index)

        def reindex(self, index=None, columns=None):
            new_idx = index if index is not None else self.index
            new_cols = columns if columns is not None else self.columns
            return SimplePivotTable(new_idx, new_cols, None, self._matrix)

    def to_datetime(val, errors="coerce"):
        if isinstance(val, Series):
            converted = []
            for v in val.values:
                if isinstance(v, datetime):
                    converted.append(v)
                elif isinstance(v, str):
                    try:
                        converted.append(datetime.fromisoformat(v.replace(" ", "T")))
                    except Exception:
                        converted.append(None)
                else:
                    converted.append(None)
            return Series(converted, index=val.index)
        return val

    def read_sql_query(query, conn):
        cursor = conn.cursor()
        cursor.execute(query)
        columns = [desc[0] for desc in cursor.description]
        rows = cursor.fetchall()
        return DataFrame(rows, columns=columns)

    class FakeSeaborn:
        @staticmethod
        def color_palette(name="Set2", n_colors=8):
            set2 = ["#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3", "#a6d854", "#ffd92f", "#e5c494", "#b3b3b3"]
            viridis = ["#440154", "#414487", "#2a788e", "#22a884", "#7ad151", "#fde725"]
            if name == "viridis":
                return (viridis * (n_colors // len(viridis) + 1))[:n_colors]
            return (set2 * (n_colors // len(set2) + 1))[:n_colors]

    # Assign module-level namespace
    class PurePandasNamespace:
        DataFrame = DataFrame
        Series = Series
        to_datetime = staticmethod(to_datetime)
        read_sql_query = staticmethod(read_sql_query)
        NaT = None

    pd = PurePandasNamespace()
    sns = FakeSeaborn()
