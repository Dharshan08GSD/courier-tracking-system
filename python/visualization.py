"""
visualization.py
Courier and Parcel Tracking Management System - Visualization Module

Generates six analytical charts and saves them as PNG files at 300 DPI
in the visualizations/ subdirectory.
"""

import os
import sys

try:
    from pandas_compat import pd, sns
except ImportError:
    import pandas as pd
    import seaborn as sns

import matplotlib
matplotlib.use("Agg")  # Non-interactive backend for file-only output
import matplotlib.pyplot as plt

from data_retrieval import (
    fetch_all_parcels,
    fetch_all_assignments,
    fetch_delivery_personnel,
)
from data_preprocessing import preprocess_data

# Output directory for charts
VIS_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "visualizations")
os.makedirs(VIS_DIR, exist_ok=True)

# Style and palette
try:
    plt.style.use("seaborn-v0_8-whitegrid")
except OSError:
    try:
        plt.style.use("seaborn-whitegrid")
    except OSError:
        plt.style.use("ggplot")

PALETTE = sns.color_palette("Set2")
SERVICE_PALETTE = {"Standard": "#4c72b0", "Express": "#dd8452", "Overnight": "#55a868"}


def save_chart(fig, filename):
    """Save a figure to the visualizations directory."""
    path = os.path.join(VIS_DIR, filename)
    fig.tight_layout()
    fig.savefig(path, dpi=300, bbox_inches="tight")
    plt.close(fig)
    print(f"  [SAVED] {path}")


# ------------------------------------------------------------------ #
# Chart 1: Parcel Status Distribution (Pie Chart)
# ------------------------------------------------------------------ #
def chart_parcel_status_distribution(parcels):
    """Pie chart showing distribution of parcel statuses."""
    if parcels.empty or "status" not in parcels.columns:
        print("  [SKIP] parcel_status_distribution — no data")
        return

    status_counts = parcels["status"].value_counts()

    fig, ax = plt.subplots(figsize=(8, 6))
    wedges, texts, autotexts = ax.pie(
        status_counts.values,
        labels=status_counts.index,
        autopct="%1.1f%%",
        startangle=140,
        colors=PALETTE[: len(status_counts)],
        pctdistance=0.85,
        wedgeprops=dict(edgecolor="white", linewidth=1.5),
    )
    for t in autotexts:
        t.set_fontsize(10)
        t.set_fontweight("bold")

    ax.set_title("Parcel Status Distribution", fontsize=14, fontweight="bold", pad=20)
    ax.legend(
        status_counts.index,
        title="Status",
        loc="center left",
        bbox_to_anchor=(1, 0.5),
    )

    save_chart(fig, "parcel_status_distribution.png")


# ------------------------------------------------------------------ #
# Chart 2: Average Delivery Charges by Service Type (Bar Chart)
# ------------------------------------------------------------------ #
def chart_delivery_charges_by_service(parcels):
    """Bar chart of average delivery charges by service type."""
    if parcels.empty or "service_type" not in parcels.columns:
        print("  [SKIP] delivery_charges_by_service — no data")
        return

    avg_charges = parcels.groupby("service_type")["delivery_charge"].mean()
    # Ensure consistent order
    order = ["Standard", "Express", "Overnight"]
    avg_charges = avg_charges.reindex(order).dropna()

    fig, ax = plt.subplots(figsize=(8, 6))
    bars = ax.bar(
        avg_charges.index,
        avg_charges.values,
        color=[SERVICE_PALETTE.get(s, "#999999") for s in avg_charges.index],
        edgecolor="white",
        linewidth=1.2,
        width=0.5,
    )

    # Add value labels on bars
    for bar in bars:
        height = bar.get_height()
        ax.annotate(
            f"₹{height:,.0f}",
            xy=(bar.get_x() + bar.get_width() / 2, height),
            xytext=(0, 6),
            textcoords="offset points",
            ha="center",
            fontsize=11,
            fontweight="bold",
        )

    ax.set_title(
        "Average Delivery Charges by Service Type", fontsize=14, fontweight="bold"
    )
    ax.set_xlabel("Service Type", fontsize=12)
    ax.set_ylabel("Average Charge (₹)", fontsize=12)
    ax.set_ylim(0, avg_charges.max() * 1.25)

    save_chart(fig, "delivery_charges_by_service.png")


# ------------------------------------------------------------------ #
# Chart 3: Shipment Volume Trend (Line Chart)
# ------------------------------------------------------------------ #
def chart_shipment_volume_trend(parcels):
    """Line chart of parcels booked over time grouped by month."""
    if parcels.empty or "booking_date" not in parcels.columns:
        print("  [SKIP] shipment_volume_trend — no data")
        return

    df = parcels.copy()
    df["booking_date"] = pd.to_datetime(df["booking_date"], errors="coerce")
    df = df.dropna(subset=["booking_date"])

    if df.empty:
        print("  [SKIP] shipment_volume_trend — no valid dates")
        return

    df["month"] = df["booking_date"].dt.to_period("M")
    monthly = df.groupby("month").size().reset_index(name="count")
    monthly["month_dt"] = monthly["month"].dt.to_timestamp()
    monthly = monthly.sort_values("month_dt")

    fig, ax = plt.subplots(figsize=(10, 6))
    ax.plot(
        monthly["month_dt"],
        monthly["count"],
        marker="o",
        linewidth=2.5,
        color="#4c72b0",
        markersize=8,
        markerfacecolor="white",
        markeredgewidth=2,
        markeredgecolor="#4c72b0",
    )
    ax.fill_between(
        monthly["month_dt"], monthly["count"], alpha=0.15, color="#4c72b0"
    )

    ax.set_title("Shipment Volume Trend (Monthly)", fontsize=14, fontweight="bold")
    ax.set_xlabel("Month", fontsize=12)
    ax.set_ylabel("Number of Parcels", fontsize=12)
    plt.xticks(rotation=45)
    ax.yaxis.set_major_locator(plt.MaxNLocator(integer=True))

    save_chart(fig, "shipment_volume_trend.png")


# ------------------------------------------------------------------ #
# Chart 4: Weight vs Charge Scatter Plot
# ------------------------------------------------------------------ #
def chart_weight_vs_charge(parcels):
    """Scatter plot of weight vs delivery charge, colored by service type."""
    if parcels.empty or "weight" not in parcels.columns:
        print("  [SKIP] weight_vs_charge — no data")
        return

    fig, ax = plt.subplots(figsize=(9, 6))

    for svc in ["Standard", "Express", "Overnight"]:
        subset = parcels[parcels["service_type"] == svc]
        if not subset.empty:
            ax.scatter(
                subset["weight"],
                subset["delivery_charge"],
                label=svc,
                color=SERVICE_PALETTE.get(svc, "#999999"),
                alpha=0.7,
                edgecolors="white",
                linewidth=0.5,
                s=80,
            )

    ax.set_title(
        "Parcel Weight vs Delivery Charge", fontsize=14, fontweight="bold"
    )
    ax.set_xlabel("Weight (kg)", fontsize=12)
    ax.set_ylabel("Delivery Charge (₹)", fontsize=12)
    ax.legend(title="Service Type", fontsize=10)

    save_chart(fig, "weight_vs_charge.png")


# ------------------------------------------------------------------ #
# Chart 5: Personnel Workload (Horizontal Bar Chart)
# ------------------------------------------------------------------ #
def chart_personnel_workload(assignments, personnel):
    """Horizontal bar chart of assignment count per delivery person."""
    if assignments.empty:
        print("  [SKIP] personnel_workload — no assignment data")
        return

    workload = assignments.groupby("personnel_id").size().reset_index(name="assignments")

    if not personnel.empty:
        workload = workload.merge(
            personnel[["personnel_id", "name"]], on="personnel_id", how="left"
        )
        workload["label"] = workload["name"].fillna(
            "ID " + workload["personnel_id"].astype(str)
        )
    else:
        workload["label"] = "ID " + workload["personnel_id"].astype(str)

    workload = workload.sort_values("assignments", ascending=True)

    fig, ax = plt.subplots(figsize=(9, max(4, len(workload) * 0.6)))
    bars = ax.barh(
        workload["label"],
        workload["assignments"],
        color=sns.color_palette("viridis", len(workload)),
        edgecolor="white",
        linewidth=1.0,
    )

    for bar in bars:
        width = bar.get_width()
        ax.text(
            width + 0.2,
            bar.get_y() + bar.get_height() / 2,
            f"{int(width)}",
            va="center",
            fontsize=10,
            fontweight="bold",
        )

    ax.set_title("Delivery Personnel Workload", fontsize=14, fontweight="bold")
    ax.set_xlabel("Number of Assignments", fontsize=12)
    ax.set_ylabel("Personnel", fontsize=12)
    ax.xaxis.set_major_locator(plt.MaxNLocator(integer=True))

    save_chart(fig, "personnel_workload.png")


# ------------------------------------------------------------------ #
# Chart 6: Distance Category Analysis (Grouped Bar Chart)
# ------------------------------------------------------------------ #
def chart_distance_category_analysis(parcels):
    """Grouped bar chart: parcel count by distance category and service type."""
    if parcels.empty or "distance_category" not in parcels.columns:
        print("  [SKIP] distance_category_analysis — no data")
        return

    ct = (
        parcels.groupby(["distance_category", "service_type"])
        .size()
        .reset_index(name="count")
    )

    dist_order = ["Local", "Regional", "National", "International"]
    svc_order = ["Standard", "Express", "Overnight"]

    pivot = ct.pivot_table(
        index="distance_category", columns="service_type", values="count", fill_value=0
    )
    # Reindex to consistent order (drop missing categories)
    pivot = pivot.reindex(index=[d for d in dist_order if d in pivot.index])
    pivot = pivot.reindex(columns=[s for s in svc_order if s in pivot.columns])

    fig, ax = plt.subplots(figsize=(10, 6))
    x = range(len(pivot.index))
    n_services = len(pivot.columns)
    bar_width = 0.25

    for i, svc in enumerate(pivot.columns):
        offsets = [pos + i * bar_width for pos in x]
        bars = ax.bar(
            offsets,
            pivot[svc],
            width=bar_width,
            label=svc,
            color=SERVICE_PALETTE.get(svc, "#999999"),
            edgecolor="white",
            linewidth=1.0,
        )
        for bar in bars:
            h = bar.get_height()
            if h > 0:
                ax.text(
                    bar.get_x() + bar.get_width() / 2,
                    h + 0.2,
                    str(int(h)),
                    ha="center",
                    va="bottom",
                    fontsize=9,
                    fontweight="bold",
                )

    ax.set_xticks([pos + bar_width * (n_services - 1) / 2 for pos in x])
    ax.set_xticklabels(pivot.index, fontsize=11)
    ax.set_title(
        "Parcels by Distance Category & Service Type",
        fontsize=14,
        fontweight="bold",
    )
    ax.set_xlabel("Distance Category", fontsize=12)
    ax.set_ylabel("Number of Parcels", fontsize=12)
    ax.legend(title="Service Type", fontsize=10)
    ax.yaxis.set_major_locator(plt.MaxNLocator(integer=True))

    save_chart(fig, "distance_category_analysis.png")


# ------------------------------------------------------------------ #
# Main
# ------------------------------------------------------------------ #
def generate_all_charts():
    """Generate all six charts."""
    print("\n" + "=" * 60)
    print("  Courier & Parcel Tracking - Visualization Module")
    print("=" * 60)

    print("\n[INFO] Loading and preprocessing data...")
    try:
        parcels = fetch_all_parcels()
        assignments = fetch_all_assignments()
        personnel = fetch_delivery_personnel()
    except Exception as e:
        print(f"[ERROR] Failed to load data: {e}")
        return

    if parcels.empty:
        print("[WARNING] No parcel data available. No charts generated.")
        return

    # Convert dates for the trend chart
    parcels["booking_date"] = pd.to_datetime(parcels["booking_date"], errors="coerce")

    print(f"\n[INFO] Generating charts in: {VIS_DIR}\n")

    chart_parcel_status_distribution(parcels)
    chart_delivery_charges_by_service(parcels)
    chart_shipment_volume_trend(parcels)
    chart_weight_vs_charge(parcels)
    chart_personnel_workload(assignments, personnel)
    chart_distance_category_analysis(parcels)

    print(f"\n[INFO] All charts saved to: {VIS_DIR}")


if __name__ == "__main__":
    try:
        generate_all_charts()
        print("[INFO] Visualization completed successfully.")
    except Exception as e:
        print(f"[ERROR] Visualization failed: {e}")
        sys.exit(1)
