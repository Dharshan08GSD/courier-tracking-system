# Relational Model — Courier and Parcel Tracking Management System

## Notation
- **Bold** = Primary Key
- *Italic* = Foreign Key

---

## 1. CUSTOMERS
```
CUSTOMERS (**customer_id**, name, contact_number, address)
```
| Attribute | Type | Constraint |
|---|---|---|
| **customer_id** | INTEGER | PRIMARY KEY, AUTOINCREMENT |
| name | TEXT | NOT NULL |
| contact_number | TEXT | NOT NULL |
| address | TEXT | NOT NULL |

---

## 2. DELIVERY_PERSONNEL
```
DELIVERY_PERSONNEL (**personnel_id**, name, contact_number, assigned_area, status)
```
| Attribute | Type | Constraint |
|---|---|---|
| **personnel_id** | INTEGER | PRIMARY KEY, AUTOINCREMENT |
| name | TEXT | NOT NULL |
| contact_number | TEXT | NOT NULL |
| assigned_area | TEXT | NULLABLE |
| status | TEXT | DEFAULT 'Available', CHECK (Available \| On Delivery \| Off Duty) |

---

## 3. PARCELS
```
PARCELS (**parcel_id**, tracking_number, *sender_id*, *receiver_id*, parcel_type, weight, booking_date, destination, distance_category, service_type, delivery_charge, status)
```
| Attribute | Type | Constraint |
|---|---|---|
| **parcel_id** | INTEGER | PRIMARY KEY, AUTOINCREMENT |
| tracking_number | TEXT | UNIQUE NOT NULL |
| *sender_id* | INTEGER | FK → CUSTOMERS(customer_id) |
| *receiver_id* | INTEGER | FK → CUSTOMERS(customer_id) |
| parcel_type | TEXT | CHECK (Document \| Small Package \| Medium Package \| Large Package \| Fragile) |
| weight | REAL | NOT NULL |
| booking_date | TEXT | NOT NULL |
| destination | TEXT | NOT NULL |
| distance_category | TEXT | CHECK (Local \| Regional \| National \| International) |
| service_type | TEXT | CHECK (Standard \| Express \| Overnight) |
| delivery_charge | REAL | Computed at booking |
| status | TEXT | DEFAULT 'Booked', CHECK (Booked \| In Transit \| Out for Delivery \| Delivered \| Returned) |

---

## 4. SHIPMENT_TRACKING
```
SHIPMENT_TRACKING (**tracking_id**, *parcel_id*, status, location, update_time)
```
| Attribute | Type | Constraint |
|---|---|---|
| **tracking_id** | INTEGER | PRIMARY KEY, AUTOINCREMENT |
| *parcel_id* | INTEGER | FK → PARCELS(parcel_id) |
| status | TEXT | NOT NULL |
| location | TEXT | NULLABLE |
| update_time | TEXT | DEFAULT datetime('now','localtime') |

---

## 5. DELIVERY_ASSIGNMENTS
```
DELIVERY_ASSIGNMENTS (**assignment_id**, *parcel_id*, *personnel_id*, assigned_date, status)
```
| Attribute | Type | Constraint |
|---|---|---|
| **assignment_id** | INTEGER | PRIMARY KEY, AUTOINCREMENT |
| *parcel_id* | INTEGER | FK → PARCELS(parcel_id) |
| *personnel_id* | INTEGER | FK → DELIVERY_PERSONNEL(personnel_id) |
| assigned_date | TEXT | NOT NULL |
| status | TEXT | DEFAULT 'Assigned', CHECK (Assigned \| In Progress \| Completed \| Failed) |

---

## 6. DELIVERY_ATTEMPTS
```
DELIVERY_ATTEMPTS (**attempt_id**, *parcel_id*, *personnel_id*, attempt_date, delivery_status, failure_reason)
```
| Attribute | Type | Constraint |
|---|---|---|
| **attempt_id** | INTEGER | PRIMARY KEY, AUTOINCREMENT |
| *parcel_id* | INTEGER | FK → PARCELS(parcel_id) |
| *personnel_id* | INTEGER | FK → DELIVERY_PERSONNEL(personnel_id) |
| attempt_date | TEXT | NOT NULL |
| delivery_status | TEXT | CHECK (Successful \| Failed) |
| failure_reason | TEXT | NULLABLE |

---

## Referential Integrity Summary

| Foreign Key | References |
|---|---|
| PARCELS.sender_id | CUSTOMERS.customer_id |
| PARCELS.receiver_id | CUSTOMERS.customer_id |
| SHIPMENT_TRACKING.parcel_id | PARCELS.parcel_id |
| DELIVERY_ASSIGNMENTS.parcel_id | PARCELS.parcel_id |
| DELIVERY_ASSIGNMENTS.personnel_id | DELIVERY_PERSONNEL.personnel_id |
| DELIVERY_ATTEMPTS.parcel_id | PARCELS.parcel_id |
| DELIVERY_ATTEMPTS.personnel_id | DELIVERY_PERSONNEL.personnel_id |
