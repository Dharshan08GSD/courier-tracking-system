"""
data_preprocessing.py
Courier and Parcel Tracking Management System - Data Preprocessing Module

Cleans, transforms, and merges the raw data retrieved from the database.
Produces derived columns (delivery_duration, is_delayed) and exports a
cleaned CSV for downstream analysis.
"""

import os
import sys

try:
    from pandas_compat import pd
except ImportError:
    import pandas as pd

from data_retrieval import (
    fetch_all_parcels,
    fetch_all_customers,
    fetch_all_tracking_records,
    fetch_all_delivery_attempts,
    fetch_all_assignments,
    fetch_delivery_personnel,
)

# Output path for the cleaned CSV
OUTPUT_DIR = os.path.dirname(os.path.abspath(__file__))
CLEANED_CSV_PATH = os.path.join(OUTPUT_DIR, "cleaned_data.csv")


def convert_date_columns(df, columns):
    """Convert specified columns to datetime, coercing errors to NaT."""
    for col in columns:
        if col in df.columns:
            df[col] = pd.to_datetime(df[col], errors="coerce")
    return df


def handle_missing_values(df):
    """Handle missing values with sensible defaults."""
    # Fill missing text columns with 'Unknown'
    text_cols = df.select_dtypes(include=["object"]).columns
    for col in text_cols:
        df[col] = df[col].fillna("Unknown")

    # Fill missing numeric columns with 0
    num_cols = df.select_dtypes(include=["number"]).columns
    for col in num_cols:
        df[col] = df[col].fillna(0)

    return df


def compute_delivery_duration(merged_df):
    """
    Compute delivery_duration in days.

    For delivered parcels, duration = delivered_date - booking_date.
    The delivered_date is taken from the latest shipment_tracking entry
    with status 'Delivered' for that parcel.
    """
    merged_df["delivery_duration"] = None

    if "delivered_date" in merged_df.columns and "booking_date" in merged_df.columns:
        mask = merged_df["delivered_date"].notna() & merged_df["booking_date"].notna()
        merged_df.loc[mask, "delivery_duration"] = (
            merged_df.loc[mask, "delivered_date"] - merged_df.loc[mask, "booking_date"]
        ).dt.days

    return merged_df


def compute_is_delayed(merged_df):
    """
    Flag parcels as delayed based on service type thresholds:
      - Express   : delayed if duration > 3 days
      - Standard  : delayed if duration > 7 days
      - Overnight : delayed if duration > 2 days
    """
    thresholds = {"Express": 3, "Standard": 7, "Overnight": 2}

    merged_df["is_delayed"] = False

    for svc, max_days in thresholds.items():
        mask = (
            (merged_df["service_type"] == svc)
            & (merged_df["delivery_duration"].notna())
            & (merged_df["delivery_duration"] > max_days)
        )
        merged_df.loc[mask, "is_delayed"] = True

    return merged_df


def preprocess_data():
    """
    Main preprocessing pipeline:
    1. Fetch all tables
    2. Convert date columns
    3. Build a delivered-date lookup from shipment_tracking
    4. Merge parcels with sender/receiver customer info and delivered date
    5. Compute derived columns
    6. Handle missing values
    7. Export cleaned CSV
    """
    print("\n[INFO] Fetching data from database...")
    parcels = fetch_all_parcels()
    customers = fetch_all_customers()
    tracking = fetch_all_tracking_records()
    attempts = fetch_all_delivery_attempts()
    assignments = fetch_all_assignments()
    personnel = fetch_delivery_personnel()

    if parcels.empty:
        print("[WARNING] No parcel data found. Cleaned CSV will be empty.")
        pd.DataFrame().to_csv(CLEANED_CSV_PATH, index=False)
        return pd.DataFrame()

    # --- Convert date columns ---
    parcels = convert_date_columns(parcels, ["booking_date"])
    tracking = convert_date_columns(tracking, ["update_time"])
    attempts = convert_date_columns(attempts, ["attempt_date"])
    assignments = convert_date_columns(assignments, ["assigned_date"])

    # --- Build delivered-date lookup ---
    delivered_dates = pd.DataFrame()
    if not tracking.empty:
        delivered_tracking = tracking[tracking["status"] == "Delivered"]
        if not delivered_tracking.empty:
            delivered_dates = (
                delivered_tracking.sort_values("update_time")
                .groupby("parcel_id")
                .last()
                .reset_index()[["parcel_id", "update_time"]]
                .rename(columns={"update_time": "delivered_date"})
            )

    # --- Merge parcels with customers (sender) ---
    merged = parcels.copy()
    if not customers.empty:
        merged = merged.merge(
            customers.rename(
                columns={
                    "customer_id": "sender_id",
                    "name": "sender_name",
                    "contact_number": "sender_contact",
                    "address": "sender_address",
                }
            ),
            on="sender_id",
            how="left",
        )
        # Merge with receiver info
        merged = merged.merge(
            customers.rename(
                columns={
                    "customer_id": "receiver_id",
                    "name": "receiver_name",
                    "contact_number": "receiver_contact",
                    "address": "receiver_address",
                }
            ),
            on="receiver_id",
            how="left",
        )

    # --- Merge delivered date ---
    if not delivered_dates.empty:
        merged = merged.merge(delivered_dates, on="parcel_id", how="left")
    else:
        merged["delivered_date"] = pd.NaT

    # --- Compute derived columns ---
    merged = compute_delivery_duration(merged)
    merged = compute_is_delayed(merged)

    # --- Handle missing values ---
    merged = handle_missing_values(merged)

    # --- Export cleaned data ---
    merged.to_csv(CLEANED_CSV_PATH, index=False)
    print(f"[INFO] Cleaned data exported to: {CLEANED_CSV_PATH}")

    return merged


def print_preprocessing_summary(df):
    """Print a summary of the preprocessed data."""
    print("\n" + "=" * 60)
    print("  Data Preprocessing Summary")
    print("=" * 60)
    print(f"  Total records          : {len(df)}")
    print(f"  Columns                : {len(df.columns)}")
    print(f"  Column names           : {list(df.columns)}")

    if "is_delayed" in df.columns:
        delayed_count = df["is_delayed"].sum()
        print(f"  Delayed deliveries     : {delayed_count}")

    if "delivery_duration" in df.columns:
        valid = df["delivery_duration"].dropna()
        if len(valid) > 0:
            print(f"  Avg delivery duration  : {valid.mean():.1f} days")
            print(f"  Max delivery duration  : {valid.max():.0f} days")

    if "service_type" in df.columns:
        print("\n  Parcels by service type:")
        for svc, cnt in df["service_type"].value_counts().items():
            print(f"    {svc:20s}: {cnt}")

    if "status" in df.columns:
        print("\n  Parcels by status:")
        for status, cnt in df["status"].value_counts().items():
            print(f"    {status:20s}: {cnt}")

    print()


if __name__ == "__main__":
    print("=" * 60)
    print("  Courier & Parcel Tracking - Data Preprocessing Module")
    print("=" * 60)

    try:
        cleaned_df = preprocess_data()
        if not cleaned_df.empty:
            print_preprocessing_summary(cleaned_df)
        print("[INFO] Preprocessing completed successfully.")
    except Exception as e:
        print(f"[ERROR] Preprocessing failed: {e}")
        sys.exit(1)
