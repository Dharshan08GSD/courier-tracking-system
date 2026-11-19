# ER Diagram — Courier and Parcel Tracking Management System

## Entity-Relationship Diagram

```mermaid
erDiagram
    CUSTOMERS {
        int customer_id PK
        string name
        string contact_number
        string address
    }

    DELIVERY_PERSONNEL {
        int personnel_id PK
        string name
        string contact_number
        string assigned_area
        string status
    }

    PARCELS {
        int parcel_id PK
        string tracking_number
        int sender_id FK
        int receiver_id FK
        string parcel_type
        float weight
        string booking_date
        string destination
        string distance_category
        string service_type
        float delivery_charge
        string status
    }

    SHIPMENT_TRACKING {
        int tracking_id PK
        int parcel_id FK
        string status
        string location
        string update_time
    }

    DELIVERY_ASSIGNMENTS {
        int assignment_id PK
        int parcel_id FK
        int personnel_id FK
        string assigned_date
        string status
    }

    DELIVERY_ATTEMPTS {
        int attempt_id PK
        int parcel_id FK
        int personnel_id FK
        string attempt_date
        string delivery_status
        string failure_reason
    }

    CUSTOMERS ||--o{ PARCELS : "sends (sender_id)"
    CUSTOMERS ||--o{ PARCELS : "receives (receiver_id)"
    PARCELS   ||--o{ SHIPMENT_TRACKING : "has tracking entries"
    PARCELS   ||--o{ DELIVERY_ASSIGNMENTS : "assigned via"
    PARCELS   ||--o{ DELIVERY_ATTEMPTS : "attempted by"
    DELIVERY_PERSONNEL ||--o{ DELIVERY_ASSIGNMENTS : "handles"
    DELIVERY_PERSONNEL ||--o{ DELIVERY_ATTEMPTS : "performs"
```

---

## Relationship Descriptions

| Relationship | Cardinality | Description |
|---|---|---|
| CUSTOMERS → PARCELS (sender) | One-to-Many | One customer can send many parcels |
| CUSTOMERS → PARCELS (receiver) | One-to-Many | One customer can receive many parcels |
| PARCELS → SHIPMENT_TRACKING | One-to-Many | Each parcel has multiple tracking events |
| PARCELS → DELIVERY_ASSIGNMENTS | One-to-Many | A parcel may be assigned to multiple personnel over time |
| PARCELS → DELIVERY_ATTEMPTS | One-to-Many | Multiple delivery attempts per parcel are possible |
| DELIVERY_PERSONNEL → DELIVERY_ASSIGNMENTS | One-to-Many | One person handles many assignments |
| DELIVERY_PERSONNEL → DELIVERY_ATTEMPTS | One-to-Many | One person performs many attempts |

---

## Attribute Constraints

| Entity | Constraint |
|---|---|
| `customers.customer_id` | Auto-increment PRIMARY KEY |
| `parcels.tracking_number` | UNIQUE NOT NULL |
| `parcels.parcel_type` | CHECK IN (Document, Small Package, Medium Package, Large Package, Fragile) |
| `parcels.distance_category` | CHECK IN (Local, Regional, National, International) |
| `parcels.service_type` | CHECK IN (Standard, Express, Overnight) |
| `parcels.status` | CHECK IN (Booked, In Transit, Out for Delivery, Delivered, Returned) |
| `delivery_personnel.status` | CHECK IN (Available, On Delivery, Off Duty) |
| `delivery_assignments.status` | CHECK IN (Assigned, In Progress, Completed, Failed) |
| `delivery_attempts.delivery_status` | CHECK IN (Successful, Failed) |
