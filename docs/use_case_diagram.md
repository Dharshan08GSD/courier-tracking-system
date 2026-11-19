# Use Case Diagram — Courier and Parcel Tracking Management System

## Actors
- **Admin** — Primary user who operates all system features
- **Delivery Personnel** — Referenced entity; their details managed by Admin

## Use Case Diagram

```mermaid
flowchart LR
    Admin["👤 Admin"]
    DP["🚚 Delivery Personnel\n(entity managed by Admin)"]

    subgraph CPTMS["Courier and Parcel Tracking Management System"]
        UC1["Manage Customers\n(Add / View / Update / Delete)"]
        UC2["Book Parcel"]
        UC3["Generate Tracking Number"]
        UC4["Calculate Delivery Charge"]
        UC5["View Parcel Details"]
        UC6["Update Parcel Status"]
        UC7["Manage Delivery Personnel\n(Add / View / Update / Delete)"]
        UC8["Assign Personnel to Parcel"]
        UC9["Record Delivery Attempt"]
        UC10["View Tracking History"]
        UC11["Generate Reports\n(Pending / Delivered / Returned /\nWorkload / Summary)"]
        UC12["Analyze & Visualize\nDelivery Performance (Python)"]
    end

    Admin --> UC1
    Admin --> UC2
    UC2 --> UC3
    UC2 --> UC4
    Admin --> UC5
    Admin --> UC6
    Admin --> UC7
    Admin --> UC8
    Admin --> UC9
    Admin --> UC10
    Admin --> UC11
    Admin --> UC12
    UC8 --> DP
    UC9 --> DP
```

---

## Use Case Descriptions

| Use Case | Actor | Description |
|---|---|---|
| Manage Customers | Admin | Add, view, update, delete customer records (sender/receiver) |
| Book Parcel | Admin | Register a new parcel with all details; triggers tracking number generation and charge calculation |
| Generate Tracking Number | System | Auto-generates unique TRK+timestamp+random number |
| Calculate Delivery Charge | System | Computes charge = weight × base_rate × distance_multiplier |
| View Parcel Details | Admin | Retrieve parcel by ID or tracking number |
| Update Parcel Status | Admin | Change status and log in shipment_tracking table |
| Manage Delivery Personnel | Admin | CRUD operations on delivery staff records |
| Assign Personnel to Parcel | Admin | Link a parcel to an available delivery person |
| Record Delivery Attempt | Admin | Log successful or failed delivery attempt with optional failure reason |
| View Tracking History | Admin | Show all status changes for a parcel with timestamps and locations |
| Generate Reports | Admin | Display pending, delivered, returned parcels and personnel workload |
| Analyze & Visualize (Python) | Admin | Run Python script to generate 6 performance charts from DB data |
