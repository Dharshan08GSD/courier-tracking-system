# Software Requirements Specification (SRS)
## Courier and Parcel Tracking Management System

**College:** Velalar College of Engineering and Technology, Erode-12  
**Department:** Information Technology  
**Class:** 25IT3A&B | **Academic Year:** 2026–27 (ODD)  
**Scenario:** 26 | **Courses:** 25ITT31 (DBMS), 25ITT32 (Java), 25ITC31 (Data Science), 25ITT33 (SE)

---

## 1. Introduction

### 1.1 Purpose
This SRS defines the functional and non-functional requirements for the **Courier and Parcel Tracking Management System (CPTMS)** — a software solution for managing parcel bookings, shipment tracking, delivery assignments, and performance analytics.

### 1.2 Scope
The system manages:
- Customer and parcel registration
- Unique tracking number generation
- Delivery charge calculation
- Delivery personnel assignment
- Real-time shipment status tracking
- Delivery attempt records
- Report generation and performance visualization (Python)

### 1.3 Definitions
| Term | Definition |
|------|-----------|
| Parcel | A physical item booked for delivery |
| Tracking Number | Unique identifier assigned to each parcel |
| Shipment Status | Current state: Booked → In Transit → Out for Delivery → Delivered / Returned |
| DAO | Data Access Object — Java class managing DB operations |

### 1.4 References
- Project Scenario Document — Velalar College of Engineering and Technology
- Java SE 11+ Documentation
- SQLite 3 Documentation
- Python pandas / matplotlib / seaborn Documentation

---

## 2. Overall Description

### 2.1 Product Perspective
CPTMS is a standalone three-tier console application:

```
[User: Console] → [Business Logic: Java] → [Data: SQLite DB]
                                         → [Analytics: Python]
```

### 2.2 Product Functions
1. Manage customer master data
2. Register and book parcels with auto-generated tracking numbers
3. Calculate delivery charges dynamically
4. Assign delivery personnel to parcels
5. Track shipment status at every stage
6. Record delivery attempts (successful or failed)
7. Generate operational reports
8. Visualize delivery performance using Python

### 2.3 User Characteristics
- **Courier Admin:** Manages customers, parcels, assignments, and reports (primary user)
- **Delivery Personnel:** Referenced in the system; updates provided by admin

### 2.4 Constraints
- Database: SQLite (file-based, no server required)
- Language: Java 11+ (console), Python 3.8+ (analytics)
- Platform: Windows (primary), cross-platform compatible

### 2.5 Assumptions
- One admin operates the system at a time
- Delivery charges are calculated at booking time and not modified later
- Internet connection not required (local SQLite database)

---

## 3. Functional Requirements

### FR-01: Manage Customer Details
- Store customer ID (auto-generated), name, contact number, and address
- Support Create, Read, Update, Delete (CRUD) operations

### FR-02: Register Parcels
- Record parcel ID, sender, receiver, parcel type, weight, booking date, and destination
- Supported parcel types: Document, Small Package, Medium Package, Large Package, Fragile

### FR-03: Generate Tracking Numbers
- Automatically assign a unique tracking number on parcel booking
- Format: `TRKYYYYMMDDHHmmssXXXX` (timestamp + 4-digit random)

### FR-04: Calculate Delivery Charges
- Formula: `charge = weight × base_rate × distance_multiplier`
- Base Rates: Standard = ₹50/kg, Express = ₹80/kg, Overnight = ₹120/kg
- Multipliers: Local = 1.0, Regional = 1.5, National = 2.0, International = 3.5

### FR-05: Assign Delivery Personnel
- Allocate an available delivery person to a parcel
- Update personnel status to "On Delivery" upon assignment
- Prevent assignment if personnel is already "On Delivery"

### FR-06: Track Shipment Status
- Record and update statuses: Booked, In Transit, Out for Delivery, Delivered, Returned
- Every status change is logged in `shipment_tracking` with timestamp and location

### FR-07: Record Delivery Attempts
- Store attempt date, delivery status (Successful/Failed), and failure reason

### FR-08: Generate Reports
- Parcel tracking history
- Pending deliveries list
- Delivered parcels list
- Returned parcels list
- Delivery personnel workload summary
- Overall delivery status summary

### FR-09: Analyze Delivery Performance (Python)
- Retrieve shipment data from SQLite using Python
- Preprocess data (clean, derive fields, merge tables)
- Generate 6 visualizations: status distribution, charges by service, volume trend, weight vs charge, personnel workload, distance category analysis

---

## 4. Non-Functional Requirements

| ID | Category | Requirement |
|----|----------|-------------|
| NFR-01 | Performance | Menu response time < 1 second for all DB queries |
| NFR-02 | Usability | Clear console menus with numbered options; validation on all inputs |
| NFR-03 | Reliability | All DB operations use PreparedStatement; SQLExceptions handled gracefully |
| NFR-04 | Security | No raw SQL strings (uses parameterized queries); foreign key constraints enforced |
| NFR-05 | Maintainability | DAO pattern separates business logic from data access |
| NFR-06 | Portability | SQLite file-based DB — no server installation required |
| NFR-07 | Scalability | Schema supports thousands of parcel records without redesign |

---

## 5. External Interface Requirements

### 5.1 User Interface
- Console-based text menus (numbered options)
- Table-formatted output with borders for readability

### 5.2 Hardware Interface
- Standard PC/laptop with keyboard and display
- Minimum 512 MB RAM, 100 MB disk space

### 5.3 Software Interface
- JDK 11+
- SQLite JDBC Driver (sqlite-jdbc-3.42.0.0.jar)
- Python 3.8+ with pandas, matplotlib, seaborn

### 5.4 Communication Interface
- Local file I/O only; no network communication
