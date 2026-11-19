# System Architecture — Courier and Parcel Tracking Management System

## Architecture Overview
The system follows a **Three-Tier Architecture**:

```mermaid
flowchart TD
    A["👤 Admin / User\n(Console Input)"]
    B["☕ Java Presentation Layer\nMain.java — Menu Driven Console"]
    C["⚙️ Business Logic Layer\nDAO Classes + ReportGenerator"]
    D["🗄️ Data Layer\nSQLite Database\ncourier_tracking.db"]
    E["🐍 Python Analytics Layer\ndata_retrieval → preprocessing → visualization"]
    F["📊 Output\nCharts / PNG Files"]

    A -->|"User Input"| B
    B -->|"Calls DAO methods"| C
    C -->|"SQL via JDBC"| D
    D -->|"ResultSet"| C
    C -->|"Formatted Output"| B
    B -->|"Console Display"| A
    D -->|"sqlite3 connection"| E
    E -->|"Charts saved"| F
```

---

## Component Descriptions

### Presentation Layer — `Main.java`
- Console menu system using `Scanner`
- Dispatches user selections to appropriate DAO/Report methods
- Formats and prints all output with table borders

### Business Logic Layer — DAO Classes

| Class | Responsibility |
|---|---|
| `DatabaseManager` | Singleton SQLite connection + schema initialisation |
| `CustomerDAO` | Full CRUD for customers |
| `ParcelDAO` | Parcel booking, tracking number generation, charge calculation, status updates |
| `DeliveryPersonnelDAO` | CRUD for delivery staff |
| `DeliveryDAO` | Parcel assignment + delivery attempt recording |
| `ReportGenerator` | Multi-table JOIN queries for all reports |

### Data Layer — SQLite (`courier_tracking.db`)
- 6 normalised tables
- Foreign key constraints enforced via `PRAGMA foreign_keys = ON`
- Schema auto-initialised on first run from `sql/schema.sql`

### Analytics Layer — Python
- `data_retrieval.py` — Connects to SQLite, returns pandas DataFrames
- `data_preprocessing.py` — Cleans data, derives features, exports CSV
- `visualization.py` — Generates 6 charts using matplotlib + seaborn

---

## Data Flow

```mermaid
sequenceDiagram
    participant U as User
    participant M as Main.java
    participant D as DAO Layer
    participant DB as SQLite DB
    participant P as Python

    U->>M: Select "Book Parcel"
    M->>D: parcelDAO.bookParcel(p)
    D->>D: generateTrackingNumber()
    D->>D: calculateCharge(weight, service, distance)
    D->>DB: INSERT INTO parcels
    D->>DB: INSERT INTO shipment_tracking (Booked)
    DB-->>D: Generated parcel_id
    D-->>M: true (success)
    M-->>U: Display Tracking Number + Charge

    Note over P,DB: Analytics (run separately)
    P->>DB: SELECT * FROM parcels, tracking, personnel
    P->>P: Preprocess & clean data
    P->>P: Generate 6 visualisation charts
    P-->>U: Save PNG files to python/visualizations/
```

---

## Technology Stack

| Component | Technology | Version |
|---|---|---|
| Programming Language | Java | 11+ |
| Database | SQLite | 3.42+ |
| JDBC Driver | sqlite-jdbc | 3.42.0.0 |
| Analytics | Python | 3.8+ |
| Data Manipulation | pandas | 1.5+ |
| Visualization | matplotlib + seaborn | 3.6+ / 0.12+ |
| Build | javac + batch script | — |
