# 📦 Courier and Parcel Tracking Management System

> **Scenario 26** | Velalar College of Engineering and Technology, Erode-12  
> **Department of Information Technology** | Class: 25IT3A&B | AY: 2026–27 (ODD)  
> **Courses:** 25ITT31 (DBMS) · 25ITT32 (Java Programming) · 25ITC31 (Data Science) · 25ITT33 (Software Engineering)

[![Live Web Portal](https://img.shields.io/badge/Live%20Website-GitHub%20Pages-2563eb?style=for-the-badge&logo=github)](https://dharshan08gsd.github.io/courier-tracking-system/)
🌐 **Live Website URL:** [https://dharshan08gsd.github.io/courier-tracking-system/](https://dharshan08gsd.github.io/courier-tracking-system/)


---

## 📋 Project Description

A console-based **Courier and Parcel Tracking Management System** that manages parcel bookings, shipment tracking, delivery assignments, and delivery performance analytics. The system uses **Java** for the application layer, **SQLite** as the database, and **Python** for data retrieval, preprocessing, and visualization.

---

## ✨ Features

- **Customer Management** — Add, view, update, and delete sender/receiver records
- **Parcel Booking** — Register parcels with auto-generated unique tracking numbers
- **Charge Calculation** — Dynamic charge based on weight, service type, and distance category
- **Status Tracking** — Real-time status updates: Booked → In Transit → Out for Delivery → Delivered / Returned
- **Delivery Assignment** — Assign available delivery personnel to parcels
- **Delivery Attempts** — Log successful and failed delivery attempts with failure reasons
- **Reports** — Tracking history, pending deliveries, delivered/returned parcels, personnel workload
- **Data Analytics (Python)** — 6 performance charts using pandas + matplotlib + seaborn

---

## 🛠️ Technologies Used

| Layer | Technology |
|---|---|
| Application | Java 11+ (Console-based, JDBC) |
| Database | SQLite 3 (via sqlite-jdbc-3.42.0.0) |
| Analytics | Python 3.8+ |
| Data Processing | pandas |
| Visualization | matplotlib, seaborn |

---

## 📁 Project Structure

```
project 2/
├── README.md
├── docs/
│   ├── SRS.md                    # Software Requirements Specification
│   ├── system_architecture.md   # Architecture + Sequence Diagrams
│   ├── use_case_diagram.md      # Use Case Diagram (Mermaid)
│   ├── class_diagram.md         # Class Diagram (Mermaid)
│   ├── activity_diagram.md      # Activity Diagram (UML 2.5 with Fork/Join)
│   ├── component_diagram.md     # Component Diagram with Ports & Interfaces
│   ├── er_diagram.md            # ER Diagram (Mermaid)
│   ├── relational_model.md      # Relational Model
│   ├── normalization.md         # 1NF → BCNF Analysis
│   ├── testing.md               # 25 Test Cases
│   └── software_engineering_units.md # Role of Units 1 to 5 (25ITT33 Course)
├── sql/
│   ├── schema.sql               # DDL — All 6 tables
│   └── sample_data.sql          # Sample data (8–10 records/table)
├── java/
│   ├── compile_and_run.bat      # Build + run script (downloads JAR if needed)
│   ├── lib/                     # sqlite-jdbc JAR (auto-downloaded)
│   ├── bin/                     # Compiled .class files
│   └── src/courier/
│       ├── Main.java
│       ├── DatabaseManager.java
│       ├── Customer.java
│       ├── Parcel.java
│       ├── DeliveryPersonnel.java
│       ├── CustomerDAO.java
│       ├── ParcelDAO.java
│       ├── DeliveryPersonnelDAO.java
│       ├── DeliveryDAO.java
│       └── ReportGenerator.java
├── python/
│   ├── requirements.txt
│   ├── data_retrieval.py        # DB → pandas DataFrames
│   ├── data_preprocessing.py   # Cleaning, feature engineering
│   ├── visualization.py        # 6 matplotlib/seaborn charts
│   ├── cleaned_data.csv        # Exported after preprocessing
│   └── visualizations/
│       ├── parcel_status_distribution.png
│       ├── delivery_charges_by_service.png
│       ├── shipment_volume_trend.png
│       ├── weight_vs_charge.png
│       ├── personnel_workload.png
│       └── distance_category_analysis.png
└── database/
    └── courier_tracking.db      # SQLite database (auto-created)
```

---

## ⚙️ Prerequisites

- **Java JDK 11+** — [Download](https://www.oracle.com/java/technologies/downloads/)
- **Python 3.8+** — [Download](https://www.python.org/downloads/)
- **pip** (bundled with Python)
- Internet connection (first run only — to download sqlite-jdbc JAR)

---

## 🚀 Setup & Installation

### Step 1 — Clone / Download the Repository
```bash
git clone <your-repository-url>
cd "project 2"
```

### Step 2 — Run the Java Application
```bash
java\compile_and_run.bat
```
> This script will:
> 1. Download `sqlite-jdbc-3.42.0.0.jar` automatically (first run)
> 2. Compile all Java source files
> 3. Create the SQLite database and load sample data on first launch
> 4. Start the interactive console menu

### Step 3 — Set Up Python Environment
```bash
cd python
pip install -r requirements.txt
```

### Step 4 — Run Python Analytics
```bash
# Retrieve and display stats
python data_retrieval.py

# Clean and preprocess data
python data_preprocessing.py

# Generate all 6 visualizations
python visualization.py
```

> **Note:** Run the Java app first (at least once) to create `database/courier_tracking.db` with sample data before running Python scripts.

---

## 📖 Usage

After running `compile_and_run.bat`, you will see:

```
╔══════════════════════════════════════════════════════╗
║   COURIER AND PARCEL TRACKING MANAGEMENT SYSTEM      ║
╚══════════════════════════════════════════════════════╝

  ══════════════ MAIN MENU ══════════════
  1. Customer Management
  2. Parcel Management
  3. Delivery Personnel Management
  4. Delivery Assignment
  5. Delivery Attempts
  6. Reports
  7. Exit
  ════════════════════════════════════════
```

Each menu option has sub-menus for Add / View / Update / Delete / List operations.

---

## 📊 Python Visualizations

| Chart | File | Description |
|---|---|---|
| Parcel Status Distribution | `parcel_status_distribution.png` | Pie chart of parcel statuses |
| Charges by Service Type | `delivery_charges_by_service.png` | Average charge: Standard vs Express vs Overnight |
| Shipment Volume Trend | `shipment_volume_trend.png` | Monthly parcel booking counts |
| Weight vs Charge | `weight_vs_charge.png` | Scatter plot coloured by service type |
| Personnel Workload | `personnel_workload.png` | Assignments per delivery person |
| Distance Category Analysis | `distance_category_analysis.png` | Grouped bar: distance × service type |

---

## 💾 Database Design

| Table | Description |
|---|---|
| `customers` | Sender and receiver master data |
| `delivery_personnel` | Courier staff details and availability |
| `parcels` | Core parcel/shipment records |
| `shipment_tracking` | Audit log of all status changes |
| `delivery_assignments` | Personnel-to-parcel assignments |
| `delivery_attempts` | Delivery attempt history with failure reasons |

---

## 📄 Documentation

| Document | Description |
|---|---|
| [SRS](docs/SRS.md) | Software Requirements Specification |
| [System Architecture](docs/system_architecture.md) | Architecture + Data Flow diagrams |
| [Use Case Diagram](docs/use_case_diagram.md) | Actor-use case relationships |
| [Class Diagram](docs/class_diagram.md) | Java class structure |
| [Activity Diagram](docs/activity_diagram.md) | UML 2.5 workflow diagram with parallel fork/join & decision points |
| [Component Diagram](docs/component_diagram.md) | Component-level design with ports, provided & required interfaces |
| [ER Diagram](docs/er_diagram.md) | Entity-relationship model |
| [Relational Model](docs/relational_model.md) | Schema in relational notation |
| [Normalization](docs/normalization.md) | 1NF → BCNF analysis for all 6 tables |
| [Testing](docs/testing.md) | 25 test cases with expected/actual results |
| [Software Engineering Units](docs/software_engineering_units.md) | Comprehensive mapping & analysis of all 5 units for 25ITT33 |

---

## 👤 Author
**Name:** Dharshan S
**Register No.:** 26 
**Class:** 25IT3A
**Institution:** Velalar College of Engineering and Technology, Erode-12

---

## 📝 License

This project is for academic purposes under the Activity-Project Based Assessment.  
© 2026 Department of Information Technology, VCET Erode.
