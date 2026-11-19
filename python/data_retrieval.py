"""
data_retrieval.py
Courier and Parcel Tracking Management System - Data Retrieval Module

Connects to the SQLite database and provides functions to fetch data
as pandas DataFrames, along with summary statistics.
"""

import sqlite3
import sys
import os

try:
    from pandas_compat import pd
except ImportError:
    import pandas as pd

# Path to the SQLite database
DB_PATH = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                       "database", "courier_tracking.db")


def get_connection():
    """Establish and return a connection to the SQLite database."""
    if not os.path.exists(DB_PATH):
        print(f"[ERROR] Database not found at: {DB_PATH}")
        print("Please ensure the database file exists before running this module.")
        sys.exit(1)
    try:
        conn = sqlite3.connect(DB_PATH)
        return conn
    except sqlite3.Error as e:
        print(f"[ERROR] Failed to connect to database: {e}")
        sys.exit(1)


def fetch_all_parcels():
    """Fetch all records from the parcels table."""
    try:
        conn = get_connection()
        df = pd.read_sql_query("SELECT * FROM parcels", conn)
        conn.close()
        return df
    except Exception as e:
        print(f"[ERROR] Failed to fetch parcels: {e}")
        return pd.DataFrame()


def fetch_all_customers():
    """Fetch all records from the customers table."""
    try:
        conn = get_connection()
        df = pd.read_sql_query("SELECT * FROM customers", conn)
        conn.close()
        return df
    except Exception as e:
        print(f"[ERROR] Failed to fetch customers: {e}")
        return pd.DataFrame()


def fetch_all_tracking_records():
    """Fetch all records from the shipment_tracking table."""
    try:
        conn = get_connection()
        df = pd.read_sql_query("SELECT * FROM shipment_tracking", conn)
        conn.close()
        return df
    except Exception as e:
        print(f"[ERROR] Failed to fetch tracking records: {e}")
        return pd.DataFrame()


def fetch_all_delivery_attempts():
    """Fetch all records from the delivery_attempts table."""
    try:
        conn = get_connection()
        df = pd.read_sql_query("SELECT * FROM delivery_attempts", conn)
        conn.close()
        return df
    except Exception as e:
        print(f"[ERROR] Failed to fetch delivery attempts: {e}")
        return pd.DataFrame()


def fetch_all_assignments():
    """Fetch all records from the delivery_assignments table."""
    try:
        conn = get_connection()
        df = pd.read_sql_query("SELECT * FROM delivery_assignments", conn)
        conn.close()
        return df
    except Exception as e:
        print(f"[ERROR] Failed to fetch assignments: {e}")
        return pd.DataFrame()


def fetch_delivery_personnel():
    """Fetch all records from the delivery_personnel table."""
    try:
        conn = get_connection()
        df = pd.read_sql_query("SELECT * FROM delivery_personnel", conn)
        conn.close()
        return df
    except Exception as e:
        print(f"[ERROR] Failed to fetch delivery personnel: {e}")
        return pd.DataFrame()


def get_summary_statistics():
    """
    Return a dictionary with summary statistics:
    - total_parcels: Total number of parcels
    - delivered_count: Number of parcels with status 'Delivered'
    - pending_count: Number of parcels that are not yet delivered or returned
      (Booked, In Transit, Out for Delivery)
    - returned_count: Number of parcels with status 'Returned'
    """
    try:
        conn = get_connection()
        cursor = conn.cursor()

        cursor.execute("SELECT COUNT(*) FROM parcels")
        total_parcels = cursor.fetchone()[0]

        cursor.execute("SELECT COUNT(*) FROM parcels WHERE status = 'Delivered'")
        delivered_count = cursor.fetchone()[0]

        cursor.execute(
            "SELECT COUNT(*) FROM parcels WHERE status IN ('Booked', 'In Transit', 'Out for Delivery')"
        )
        pending_count = cursor.fetchone()[0]

        cursor.execute("SELECT COUNT(*) FROM parcels WHERE status = 'Returned'")
        returned_count = cursor.fetchone()[0]

        conn.close()

        return {
            "total_parcels": total_parcels,
            "delivered_count": delivered_count,
            "pending_count": pending_count,
            "returned_count": returned_count,
        }
    except Exception as e:
        print(f"[ERROR] Failed to get summary statistics: {e}")
        return {
            "total_parcels": 0,
            "delivered_count": 0,
            "pending_count": 0,
            "returned_count": 0,
        }


if __name__ == "__main__":
    print("=" * 60)
    print("  Courier & Parcel Tracking - Data Retrieval Module")
    print("=" * 60)
    print(f"\nDatabase path: {DB_PATH}")

    # Print summary statistics
    stats = get_summary_statistics()
    print("\n--- Summary Statistics ---")
    print(f"  Total Parcels   : {stats['total_parcels']}")
    print(f"  Delivered        : {stats['delivered_count']}")
    print(f"  Pending          : {stats['pending_count']}")
    print(f"  Returned         : {stats['returned_count']}")

    # Print table record counts
    print("\n--- Table Record Counts ---")
    tables = {
        "Customers": fetch_all_customers,
        "Parcels": fetch_all_parcels,
        "Tracking Records": fetch_all_tracking_records,
        "Delivery Attempts": fetch_all_delivery_attempts,
        "Assignments": fetch_all_assignments,
        "Delivery Personnel": fetch_delivery_personnel,
    }
    for name, func in tables.items():
        df = func()
        print(f"  {name:25s}: {len(df)} records")

    print("\n[INFO] Data retrieval completed successfully.")
