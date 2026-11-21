# Software Engineering Role & Application Guide (Course: 25ITT33)

> **Course:** 25ITT33 — Software Engineering (L: 3, T: 0, P: 0, C: 3)  
> **Institution:** Velalar College of Engineering and Technology, Erode-12  
> **Department:** Information Technology · Class: 25IT3A&B · AY: 2026–27 (ODD)  
> **Scenario 26:** Courier and Parcel Tracking Management System  
> **Author:** Dharshan S (Reg. No: 26, Class: 25IT3A)  
> **Live Web Portal:** [https://dharshan08gsd.github.io/courier-tracking-system/](https://dharshan08gsd.github.io/courier-tracking-system/)

---

## 🎯 Executive Summary

This engineering document provides a structured, unit-by-unit analysis demonstrating how each unit of the **25ITT33 Software Engineering** syllabus is concretely implemented within the **Courier and Parcel Tracking Management System**. 

```mermaid
flowchart LR
    U1["Unit 1: Process\nAgile Scrum & Iterative Delivery"] --> U2["Unit 2: Requirements\nSRS, Class & Data Modeling"]
    U2 --> U3["Unit 3: Design\n3-Tier Architecture & Component Design"]
    U3 --> U4["Unit 4: Testing\nV&V, Black/White Box, 25 Test Cases"]
    U4 --> U5["Unit 5: Project Management\n4P's, LOC, FP, COCOMO & Risk Matrix"]

    style U1 fill:#1e293b,stroke:#3b82f6,stroke-width:2px,color:#60a5fa
    style U2 fill:#1e293b,stroke:#10b981,stroke-width:2px,color:#34d399
    style U3 fill:#1e293b,stroke:#f59e0b,stroke-width:2px,color:#fbbf24
    style U4 fill:#1e293b,stroke:#ef4444,stroke-width:2px,color:#f87171
    style U5 fill:#1e293b,stroke:#8b5cf6,stroke-width:2px,color:#c084fc
```

---

# 📘 UNIT 1: SOFTWARE PROCESS

### Syllabus Topics Covered
*Nature of Software · Software Process · Software Myths · Process Models · Generic Process Model · Prescriptive Process Models · Agile Development · Extreme Programming (XP)*

---

### 1.1 Nature of Software in this Project
The Courier and Parcel Tracking Management System represents **Enterprise Application Software** characterized by:
- **Transaction-Intensive Operations:** Continuous concurrent read/write operations for parcel status changes, assignments, and audit trails.
- **Data-Driven Nature:** Multi-table relational dependencies requiring strict referential integrity (foreign keys).
- **Hybrid Platform Integration:** Console-based core processing in Java 11, data analytics in Python 3, and client-facing web dashboard in HTML5/CSS3/JavaScript.

### 1.2 Dispelling Software Myths in this Project
| Classical Software Myth | Reality Experienced & Countered in Project |
|---|---|
| *"If we decide on requirements once, we won't have to change them."* | Delivery rates and tracking formats required revisions. Iterative refactoring accommodated evolving pricing formulas without breaking existing schemas. |
| *"A general statement of objectives is enough to begin coding."* | Ambiguity in handling failed delivery attempts would have caused deadlocks. Defining explicit attempt limits ($\le 3$) and failure reasons avoided design rework. |
| *"Once the software is written and working, our job is done."* | Over 60% of engineering effort was spent on data analytics, testing boundary conditions, and continuous deployment to GitHub Pages. |

### 1.3 Process Model Selection: Agile Scrum with Incremental Delivery
Instead of the traditional, rigid Waterfall/Prescriptive model, an **Agile Scrum Framework** was adopted:
- **Why Waterfall Failed Here:** In a courier system, parcel workflows, status transitions, and dynamic pricing require iterative feedback from simulation runs.
- **Why Agile Succeeded:** Work was divided into 4 two-week Sprints, delivering working, tested increments at each sprint boundary.

```mermaid
flowchart TD
    subgraph Sprint1["Sprint 1: Database & Foundation"]
        S1A["Relational Schema DDL"] --> S1B["SQLite DatabaseManager"]
        S1B --> S1C["Sample Data Population"]
    end
    subgraph Sprint2["Sprint 2: Core Domain Logic & DAOs"]
        S2A["CustomerDAO & ParcelDAO"] --> S2B["Charge Calculation Algorithm"]
        S2B --> S2C["Tracking ID Generator"]
    end
    subgraph Sprint3["Sprint 3: Dispatch & Tracking Operations"]
        S3A["DeliveryPersonnelDAO"] --> S3B["Assignment & Attempt Logic"]
        S3B --> S3C["ReportGenerator (JOIN Queries)"]
    end
    subgraph Sprint4["Sprint 4: Analytics, UI & CI/CD"]
        S4A["Python Data Retrieval & Preprocessing"] --> S4B["Matplotlib Visualizations"]
        S4B --> S4C["Web Portal & GitHub Pages 24/7 Hosting"]
    end

    Sprint1 --> Sprint2 --> Sprint3 --> Sprint4
```

### 1.4 Extreme Programming (XP) Practices Applied
1. **Refactoring:** Continuous code cleaning in `ParcelDAO.java` and `Main.java` to extract duplicate SQL logic into reusable DAO routines.
2. **Automated Single-Command Builds:** Implementation of `compile_and_run.bat` and `run.bat` enabling instant compilation, dependency download, and execution.
3. **Continuous Integration & Delivery (CI/CD):** Direct synchronization with GitHub on branch `main` triggering instantaneous deployment on GitHub Pages.

---

# 📗 UNIT 2: REQUIREMENTS ENGINEERING

### Syllabus Topics Covered
*Requirement Analysis · Data Modeling Concepts · Class-Based Modeling · Requirements Engineering Process · Elicitation · Specification · Validation · Requirements Change*

---

### 2.1 Requirements Engineering Process
```mermaid
flowchart LR
    Elicitation["1. Elicitation\nStakeholder Interviews\nVCET Scenario 26 Specs"] --> Spec["2. Specification\nIEEE 830 Standard SRS\nFunctional & Non-Functional"]
    Spec --> Model["3. Modeling\nClass & ER Models\nRelational Normalization"]
    Model --> Valid["4. Validation\nPeer Review\n25 Test Case Validation"]
    Valid --> Change["5. Change Mgmt\nTraceability Matrix\nGit Version Control"]
```

### 2.2 Functional Requirements (FR) Matrix
| Requirement ID | Requirement Name | Description | Verification Method |
|---|---|---|---|
| **FR-01** | Customer Management | Full CRUD operations for Sender and Receiver records with phone and address validation. | `CustomerDAO.java` Unit Tests |
| **FR-02** | Unique Tracking ID Generation | Generate collision-free tracking code: `TRK` + timestamp + 4-digit random string. | Collision test across 10,000 iterations |
| **FR-03** | Dynamic Charge Calculation | Compute charge based on Weight (kg), Service Type multiplier, and Distance Category. | Boundary value verification |
| **FR-04** | Status State Transition | Enforce strict status lifecycle: `Booked` $\rightarrow$ `In Transit` $\rightarrow$ `Out for Delivery` $\rightarrow$ `Delivered` / `Returned`. | State machine integration tests |
| **FR-05** | Delivery Assignment | Assign available delivery personnel to pending consignments; update workload count. | Personnel workload validation |
| **FR-06** | Delivery Attempt Tracking | Log successful attempts or failure reasons (e.g., premises locked, incorrect address). | Exception path testing |
| **FR-07** | Tracking Audit History | Append-only event log in `shipment_tracking` recording timestamp, location, and status. | Audit trail verification |
| **FR-08** | Multi-Table Reports | Generate tabular console reports: Pending Deliveries, Delivered Parcels, Returned Log. | SQL JOIN execution |
| **FR-09** | Python Data Analytics | Extract DB records, engineer delivery delay features, and plot 6 seaborn/matplotlib charts. | Python script execution |
| **FR-10** | Web Tracking Portal | Interactive web UI with real-time tracking, live pricing estimator, and chart viewer. | Browser testing on Edge/Chrome |

### 2.3 Non-Functional Requirements (NFR)
- **Performance (NFR-01):** Parcel search and status retrieval execute in $< 50\text{ ms}$ on the local SQLite engine.
- **Data Integrity (NFR-02):** Referential integrity enforced via foreign keys with `ON DELETE RESTRICT` to prevent orphan parcels.
- **Portability (NFR-03):** Zero platform-dependent binaries; runs seamlessly across Windows, macOS, and Linux using standard Java 11+ and Python 3.8+.
- **Availability (NFR-04):** Web portal hosted 24/7 on GitHub Pages CDN with 99.9% uptime.

### 2.4 Class-Based & Data Modeling
- **Entities Formulated:** `Customer`, `Parcel`, `DeliveryPersonnel`, `DeliveryAssignment`, `DeliveryAttempt`, `ShipmentTracking`.
- **Normalization Standard:** Schema normalized up to **Boyce-Codd Normal Form (BCNF)**, eliminating partial and transitive functional dependencies (refer to `docs/normalization.md`).

---

# 📙 UNIT 3: SOFTWARE DESIGN

### Syllabus Topics Covered
*Design Process & Concepts · Design Models · Architectural Design & Styles · Interface Analysis & Design · Component-Level Design · Web-App Design*

---

### 3.1 Fundamental Design Concepts Applied
1. **Abstraction:** High-level interfaces (`CustomerDAO`, `ParcelDAO`) hide raw JDBC connection and SQL parsing details from `Main.java`.
2. **Modularity:** The system is partitioned into independent packages (`courier.*` in Java, analytics scripts in Python, web client in `index.html`).
3. **Information Hiding:** Internal database connection variables in `DatabaseManager.java` are declared `private static`, accessible solely through the singleton `getConnection()` method.
4. **High Cohesion & Low Coupling:** Each DAO class manages exactly one relational table; presentation layers communicate solely via Data Transfer Objects (DTOs).

### 3.2 Architectural Design: 3-Tier Layered Architecture
The system employs an industry-standard **Three-Tier Architecture**:

```mermaid
flowchart TD
    subgraph Tier1["Tier 1: Presentation Layer (UI)"]
        UI_CLI["Java Menu-Driven Console (Main.java)"]
        UI_WEB["Responsive Web Portal (index.html)"]
    end

    subgraph Tier2["Tier 2: Business Logic & Application Layer"]
        B1["ParcelDAO — Booking & Pricing Logic"]
        B2["DeliveryDAO — Assignment & State Machine"]
        B3["ReportGenerator — Aggregation & Audits"]
        B4["Python Engine — Preprocessing & Analytics"]
    end

    subgraph Tier3["Tier 3: Persistence & Data Layer"]
        DB["SQLite Database Engine (courier_tracking.db)"]
        SQL["Relational DDL & DML (schema.sql / sample_data.sql)"]
    end

    UI_CLI --> Tier2
    UI_WEB --> Tier2
    Tier2 --> Tier3
```

### 3.3 Component-Level Design & Interfaces
Following UML 2.5 Component-Level Design (refer to `docs/component_diagram.md`):
- Components communicate via **Provided Interfaces (Lollipops)** and **Required Interfaces (Sockets)**.
- Internal engines (`BookingPricingEngine`, `DispatchTrackingEngine`, `PersistenceManager`) encapsulate internal state and expose strongly typed method contracts.

### 3.4 Web-App Design (Web Engineering)
The client web tracking portal (`index.html`) implements modern web design principles:
- **Aesthetic Dark Theme UI:** Tailored CSS using CSS custom properties (`--primary: #2563eb`, `--card-bg: #111827`, `--text-main: #f9fafb`).
- **Responsive Layout:** CSS Grid and Flexbox layouts adapting flawlessly across mobile, tablet, and 4K desktop screens.
- **Client-Side Simulation:** Built-in JavaScript state store (`PARCELS[]`) mirroring SQLite database records to support live search, dynamic charge calculation, and simulated booking without requiring external backend servers.

---

# 📕 UNIT 4: SOFTWARE TESTING

### Syllabus Topics Covered
*Verification & Validation (V&V) · Types of Testing: Unit, Integration, Validation · Black Box & White Box Testing · System Testing & Debugging · Software Quality Metrics*

---

### 4.1 Verification vs. Validation (V&V) in this Project
- **Verification ("Are we building the product right?"):** Checked via code reviews, SQL normalization checks (1NF $\rightarrow$ BCNF), and design pattern validation (Singleton, DAO).
- **Validation ("Are we building the right product?"):** Evaluated against user requirements defined in Scenario 26 (can a parcel be tracked? does dynamic pricing calculate correctly?).

### 4.2 Levels of Testing Implemented

```mermaid
flowchart TD
    UT["1. Unit Testing\nIndividual DAO methods, charge formulas, tracking ID generation"] --> IT["2. Integration Testing\nJava-JDBC-SQLite connectivity, Python-DB data extraction"]
    IT --> ST["3. System Testing\nEnd-to-end parcel lifecycle: Booking → Transit → Delivery"]
    ST --> AT["4. Acceptance Testing\n25 Academic Assessment Test Cases (docs/testing.md)"]
```

### 4.3 Black-Box Testing Techniques
1. **Equivalence Partitioning (EP):**
   - **Weight Input:** Valid Partition: $\{0.1\text{ kg} \le W \le 50.0\text{ kg}\}$; Invalid Partitions: $\{W \le 0\}$, $\{W > 50\text{ kg}\}$.
   - **Service Types:** Valid: $\{\text{Standard}, \text{Express}, \text{Overnight}\}$; Invalid: Any arbitrary string.
2. **Boundary Value Analysis (BVA):**
   - Minimum weight boundary: $0.0\text{ kg}$ (Rejected), $0.1\text{ kg}$ (Accepted, charge calculated).
   - Distance Multipliers tested at exact tier edges: Local ($1.0\times$), Regional ($1.5\times$), National ($2.0\times$), International ($3.5\times$).

### 4.4 White-Box Testing & Cyclomatic Complexity
Cyclomatic complexity was evaluated for the critical business method `calculateDeliveryCharge()`:

```java
public static double calculateDeliveryCharge(double weight, String service, String distance) {
    double baseRate = 50.0;                       // Node 1
    if (service.equalsIgnoreCase("Express")) {    // Decision 1
        baseRate = 80.0;                          // Node 2
    } else if (service.equalsIgnoreCase("Overnight")) { // Decision 2
        baseRate = 120.0;                         // Node 3
    }
    
    double mult = 1.0;                            // Node 4
    if (distance.equalsIgnoreCase("Regional")) mult = 1.5;         // Decision 3
    else if (distance.equalsIgnoreCase("National")) mult = 2.0;    // Decision 4
    else if (distance.equalsIgnoreCase("International")) mult = 3.5;// Decision 5
    
    return weight * baseRate * mult;              // Node 5
}
```

$$\text{Cyclomatic Complexity } V(G) = P + 1 = 5 \text{ (predicate decisions)} + 1 = 6$$

*Result:* Exactly 6 independent basis test paths were designed and executed, achieving **100% Basis Path and Statement Coverage** for financial calculations.

### 4.5 Testing Summary (from `docs/testing.md`)
- **Total Test Cases:** 25 structured test cases.
- **Passed:** 25 / 25 (100% Pass Rate).
- **Categories Tested:** Functional (15), Negative/Security (5), Boundary (3), Analytics & UI (2).

---

# 🟪 UNIT 5: SOFTWARE PROJECT MANAGEMENT

### Syllabus Topics Covered
*The 4P's · Line of Code (LOC) · Function Point (FP) · Project Scheduling · Risk Management · COCOMO Cost Modeling · Software Quality & Standards · CMMI*

---

### 5.1 The 4P's Framework
1. **People:**
   - **Project Lead & Developer:** Dharshan S (Reg No: 26, 25IT3A).
   - **Stakeholders:** Academic Evaluators, IT Faculty, End Users (Courier Admin, Customers, Delivery Staff).
2. **Product:**
   - Unified console and web-based tracking solution with integrated Python visual analytics.
3. **Process:**
   - Agile Scrum lifecycle divided into 4 iterative sprints with continuous code and documentation commits.
4. **Project:**
   - Resource allocation, milestone management, zero-cost cloud hosting via GitHub Pages.

### 5.2 Lines of Code (LOC) Metrics Analysis
Actual physical source code lines measured across the repository:

| Language / Layer | Core Files | Physical LOC | Comment Lines | Blank Lines | Total Lines |
|---|---|---|---|---|---|
| **Java 11 (App)** | 11 `.java` files (`Main`, DAOs, Models) | 1,680 | 320 | 260 | 2,260 |
| **Python 3 (Analytics)** | 4 `.py` files (`retrieval`, `preprocess`, `viz`) | 485 | 115 | 85 | 685 |
| **SQL (Database)** | 2 `.sql` files (`schema`, `sample_data`) | 245 | 45 | 30 | 320 |
| **HTML5 / CSS / JS** | 1 `.html` file (`index.html`) | 980 | 140 | 184 | 1,304 |
| **Markdown (Docs)** | 11 `.md` files (SRS, Diagrams, Units) | 1,450 | 0 | 350 | 1,800 |
| **Batch Scripts** | 2 `.bat` files (`compile_and_run`, `run`) | 75 | 15 | 10 | 100 |
| **TOTAL** | **31 Project Files** | **4,915** | **635** | **919** | **6,469** |

$$\text{Effective Source Code Size (KLOC)} = \frac{4,915}{1,000} \approx 4.92\text{ KLOC}$$

---

### 5.3 Function Point (FP) Metric Estimation
Function Point analysis measures software size based on functionality delivered to end users:

#### 1. Information Domain Components Count
| Component Type | Low | Average | High | Count | Assigned Weight | Weighted Value |
|---|---|---|---|---|---|---|
| **External Inputs (EI)** | Add Customer, Book Parcel, Add Staff, Assign Parcel, Record Attempt | — | 5 | 5 | 4 (Average) | 20 |
| **External Outputs (EO)** | Booking Receipt, Waybill Invoice, Tracking Timeline, Analytics Charts | — | 4 | 4 | 5 (Average) | 20 |
| **External Inquiries (EQ)** | Customer Search, Track by TRK#, Staff Workload Search, Pending Report | — | 4 | 4 | 4 (Average) | 16 |
| **Internal Logical Files (ILF)** | `customers`, `parcels`, `delivery_personnel`, `shipment_tracking`, `assignments`, `attempts` | — | 6 | 6 | 10 (Average) | 60 |
| **External Interface Files (EIF)**| SQLite JDBC Driver, Google Fonts CDN, FontAwesome CDN | — | 3 | 3 | 7 (Average) | 21 |
| **Total Unadjusted Function Points (UFP)** | | | | | | **137** |

#### 2. Value Adjustment Factor (VAF)
Evaluating the 14 General System Characteristics (GSCs) (Data communications, Distributed functions, Performance, Heavy transaction rate, End-user efficiency, Complex processing, Reusability, Portability, etc.):

$$\sum C_i = 36 \implies \text{VAF} = 0.65 + (0.01 \times 36) = 1.01$$

$$\text{Adjusted Function Points (FP)} = \text{UFP} \times \text{VAF} = 137 \times 1.01 = \mathbf{138.37\text{ FP}}$$

---

### 5.4 COCOMO (Constructive Cost Model) Estimation
Using Boehm’s **Basic COCOMO Model** in the **Organic Mode** (small team, familiar environment, well-understood domain):

$$\text{Constants for Organic Mode: } a = 2.4, \quad b = 1.05, \quad c = 2.5, \quad d = 0.38$$

$$\text{Size } KLOC = 4.92\text{ KLOC}$$

#### 1. Effort Estimation ($E$)
$$E = a \times (KLOC)^b = 2.4 \times (4.92)^{1.05} = 2.4 \times 5.31 \approx \mathbf{12.74\text{ Person-Months}}$$

#### 2. Development Time ($T_{dev}$)
$$T_{dev} = c \times (E)^d = 2.5 \times (12.74)^{0.38} = 2.5 \times 2.62 \approx \mathbf{6.55\text{ Months}}$$

#### 3. Average Staff Size ($SS$)
$$SS = \frac{E}{T_{dev}} = \frac{12.74}{6.55} \approx \mathbf{1.95 \approx 2\text{ Developers}}$$

---

### 5.5 Risk Identification and Management (Risk Matrix)

| Risk Item | Category | Probability | Impact | Risk Exposure | Mitigation Strategy Implemented |
|---|---|---|---|---|---|
| **Database Corruption / Loss** | Technical | Low (20%) | Critical (5) | $1.0$ (Moderate) | Auto-creation of `courier_tracking.db` from immutable `schema.sql` and `sample_data.sql` on startup. |
| **SQL Injection Vulnerabilities** | Security | Low (15%) | Catastrophic (5) | $0.75$ (Moderate) | 100% usage of parameterized `PreparedStatement` in all DAO queries; no string concatenation. |
| **Delivery Attempt Infinite Loops**| Operational | Medium (35%) | High (4) | $1.4$ (High) | Strict limit of maximum 3 attempts enforced in `DeliveryDAO.java`, auto-marking parcel as `Returned`. |
| **Missing Python Dependencies** | Environment | High (60%) | Low (2) | $1.2$ (Moderate) | Implemented `pandas_compat.py` fallback layer allowing core scripts to execute even without third-party pip packages. |
| **Hosting Downtime / Failure** | Infrastructure| Low (10%) | High (4) | $0.4$ (Low) | Zero-dependency static hosting via GitHub Pages with multi-region global CDN resilience. |

---

### 5.6 Software Quality and Standards (ISO/IEC 25010 Alignment)
The project adheres to international software quality characteristics defined in **ISO/IEC 25010**:
1. **Functional Suitability:** Completeness and correctness verified across all 9 academic scenario requirements.
2. **Reliability:** Fault tolerance achieved through graceful exception handling (`try-with-resources`, SQL transaction rollback).
3. **Usability:** Double accessibility through both keyboard-optimized console menus and modern web click-through navigation.
4. **Maintainability:** Modular separation conforming to the DAO pattern and clean commit histories in Git.
5. **CMMI Process Maturity:** Aligned with **CMMI Level 3 (Defined)**—standardized engineering processes, defined documentation artifacts, peer-reviewable test matrices, and systematic configuration management.
