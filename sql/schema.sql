-- ============================================================
-- Courier and Parcel Tracking Management System — Schema DDL
-- Database: SQLite
-- ============================================================

-- Enable foreign key enforcement
PRAGMA foreign_keys = ON;

-- ============================================================
-- Drop tables in reverse dependency order to satisfy FK constraints
-- ============================================================
DROP TABLE IF EXISTS delivery_attempts;
DROP TABLE IF EXISTS delivery_assignments;
DROP TABLE IF EXISTS shipment_tracking;
DROP TABLE IF EXISTS parcels;
DROP TABLE IF EXISTS delivery_personnel;
DROP TABLE IF EXISTS customers;

-- ============================================================
-- 1. customers
--    Stores sender and receiver information.
-- ============================================================
CREATE TABLE customers (
    customer_id    INTEGER PRIMARY KEY AUTOINCREMENT,
    name           TEXT    NOT NULL,
    contact_number TEXT    NOT NULL,
    address        TEXT    NOT NULL
);

-- ============================================================
-- 2. delivery_personnel
--    Courier staff who deliver parcels.
--    status tracks current availability.
-- ============================================================
CREATE TABLE delivery_personnel (
    personnel_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name           TEXT    NOT NULL,
    contact_number TEXT    NOT NULL,
    assigned_area  TEXT,
    status         TEXT    DEFAULT 'Available'
                          CHECK(status IN ('Available', 'On Delivery', 'Off Duty'))
);

-- ============================================================
-- 3. parcels
--    Core table: each row is a booked parcel/shipment.
--    delivery_charge = weight * base_rate * distance_multiplier
--      Base rates   — Standard: 50, Express: 80, Overnight: 120
--      Multipliers  — Local: 1.0, Regional: 1.5, National: 2.0, International: 3.5
-- ============================================================
CREATE TABLE parcels (
    parcel_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    tracking_number   TEXT    UNIQUE NOT NULL,
    sender_id         INTEGER NOT NULL,
    receiver_id       INTEGER NOT NULL,
    parcel_type       TEXT    NOT NULL
                             CHECK(parcel_type IN ('Document', 'Small Package', 'Medium Package', 'Large Package', 'Fragile')),
    weight            REAL   NOT NULL,
    booking_date      TEXT   NOT NULL,
    destination       TEXT   NOT NULL,
    distance_category TEXT   NOT NULL
                             CHECK(distance_category IN ('Local', 'Regional', 'National', 'International')),
    service_type      TEXT   NOT NULL
                             CHECK(service_type IN ('Standard', 'Express', 'Overnight')),
    delivery_charge   REAL,
    status            TEXT   DEFAULT 'Booked'
                             CHECK(status IN ('Booked', 'In Transit', 'Out for Delivery', 'Delivered', 'Returned')),
    FOREIGN KEY (sender_id)   REFERENCES customers(customer_id),
    FOREIGN KEY (receiver_id) REFERENCES customers(customer_id)
);

-- ============================================================
-- 4. shipment_tracking
--    Audit / history log: every status change for a parcel.
-- ============================================================
CREATE TABLE shipment_tracking (
    tracking_id  INTEGER PRIMARY KEY AUTOINCREMENT,
    parcel_id    INTEGER NOT NULL,
    status       TEXT    NOT NULL,
    location     TEXT,
    update_time  TEXT    DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (parcel_id) REFERENCES parcels(parcel_id)
);

-- ============================================================
-- 5. delivery_assignments
--    Maps a parcel to a delivery person for a given date.
-- ============================================================
CREATE TABLE delivery_assignments (
    assignment_id  INTEGER PRIMARY KEY AUTOINCREMENT,
    parcel_id      INTEGER NOT NULL,
    personnel_id   INTEGER NOT NULL,
    assigned_date  TEXT    NOT NULL,
    status         TEXT    DEFAULT 'Assigned'
                          CHECK(status IN ('Assigned', 'In Progress', 'Completed', 'Failed')),
    FOREIGN KEY (parcel_id)    REFERENCES parcels(parcel_id),
    FOREIGN KEY (personnel_id) REFERENCES delivery_personnel(personnel_id)
);

-- ============================================================
-- 6. delivery_attempts
--    Records each physical delivery attempt and its outcome.
-- ============================================================
CREATE TABLE delivery_attempts (
    attempt_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    parcel_id       INTEGER NOT NULL,
    personnel_id    INTEGER NOT NULL,
    attempt_date    TEXT    NOT NULL,
    delivery_status TEXT    NOT NULL
                           CHECK(delivery_status IN ('Successful', 'Failed')),
    failure_reason  TEXT,
    FOREIGN KEY (parcel_id)    REFERENCES parcels(parcel_id),
    FOREIGN KEY (personnel_id) REFERENCES delivery_personnel(personnel_id)
);
