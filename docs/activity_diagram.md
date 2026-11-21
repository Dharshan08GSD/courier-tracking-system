# Activity Diagram — Courier and Parcel Tracking Management System

> **Course:** 25ITT33 Software Engineering · Unit 3: Software Design & Modeling  
> **Scenario 26:** Courier and Parcel Tracking Management System  
> **Author:** Dharshan S (Reg. No: 26, Class: 25IT3A)  
> **Institution:** Velalar College of Engineering and Technology, Erode-12

---

## 📌 Overview

An **Activity Diagram** models the dynamic, procedural control flow and business workflow of a system. It captures sequential activities, concurrent execution paths (using fork and join bars), decision branches (guard conditions), and terminal states.

In this Courier and Parcel Tracking Management System, the activity diagram models the end-to-end lifecycle of parcel booking, concurrent physical dispatch processing versus billing/waybill notification generation, and the final delivery attempt resolution.

---

## 📊 UML Activity Diagram (UML 2.5 Standard)

```mermaid
flowchart TD
    %% Initial Node
    Start([● Initial Node]) --> RecReq["Receive Booking & Parcel Details"]

    %% Fork Bar (Parallel Execution)
    RecReq --> ForkBar{{"══════════ FORK (Concurrent Operations) ══════════"}}

    %% Branch 1: Physical Fulfillment & Dispatch Flow
    ForkBar --> CalcCharge["Calculate Delivery Charge\n(Weight × Base Rate × Distance Multiplier)"]
    CalcCharge --> CheckService{"Service Type\nDecision"}
    
    CheckService -->|"[service == 'Overnight']"| Overnight["Arrange Overnight Priority Dispatch\n(24h Express Route)"]
    CheckService -->|"[service == 'Express']"| Express["Arrange Express Fast-Track Delivery\n(48h Route)"]
    CheckService -->|"[service == 'Standard']"| Regular["Arrange Standard Ground Delivery\n(3-5 Days Route)"]

    Overnight --> MergeService{"Merge"}
    Express --> MergeService
    Regular --> MergeService

    MergeService --> AssignStaff["Assign Available Delivery Personnel\n(Status = 'In Transit')"]
    AssignStaff --> OutForDelivery["Dispatch to Final Destination Hub\n(Status = 'Out for Delivery')"]
    
    OutForDelivery --> AttemptDelivery["Execute Physical Delivery Attempt"]
    AttemptDelivery --> CheckAttempt{"Delivery\nSuccessful?"}

    CheckAttempt -->|"[Success / Customer Signed]"| MarkDelivered["Update Status to 'Delivered'\nRecord Delivered Timestamp"]
    CheckAttempt -->|"[Failed / Customer Unavailable / Address Issue]"| LogFailure["Record Delivery Attempt with Failure Reason\n(Max 3 Attempts)"]
    
    LogFailure --> CheckRetries{"Retry Count\nExceeded?"}
    CheckRetries -->|"[Retries < 3]"| Reschedule["Reschedule Attempt\nReassign to Route"]
    Reschedule --> AttemptDelivery
    CheckRetries -->|"[Retries >= 3 or Address Invalid]"| MarkReturned["Update Status to 'Returned'\nRoute Back to Origin Sender"]

    %% Branch 2: Financial & Notification Flow (Concurrent)
    ForkBar --> GenWaybill["Generate Unique Tracking Number\n(e.g., TRK20241101100001)"]
    GenWaybill --> IssueInvoice["Generate Consignment Invoice\n& Waybill Documentation"]
    IssueInvoice --> ProcessPayment["Receive & Reconcile Payment\n(Cash on Delivery / Prepaid)"]
    ProcessPayment --> SendNotification["Send Tracking SMS & Email Alert\nto Sender & Receiver"]

    %% Join Bar (Synchronization)
    MarkDelivered --> JoinBar{{"══════════ JOIN (Synchronization Bar) ══════════"}}
    MarkReturned --> JoinBar
    SendNotification --> JoinBar

    %% Final Audit & End
    JoinBar --> AuditLog["Log Final Audit Record in shipment_tracking\nArchive Transaction"]
    AuditLog --> EndNode(((◉ Final Activity State)))

    %% Styling
    style Start fill:#000,stroke:#2563eb,stroke-width:3px,color:#fff
    style EndNode fill:#0b0f19,stroke:#10b981,stroke-width:4px,color:#10b981
    style ForkBar fill:#1e293b,stroke:#3b82f6,stroke-width:2px,color:#60a5fa
    style JoinBar fill:#1e293b,stroke:#3b82f6,stroke-width:2px,color:#60a5fa
    style CheckService fill:#1e293b,stroke:#f59e0b,stroke-width:2px,color:#fcd34d
    style CheckAttempt fill:#1e293b,stroke:#f59e0b,stroke-width:2px,color:#fcd34d
    style CheckRetries fill:#1e293b,stroke:#f59e0b,stroke-width:2px,color:#fcd34d
```

---

## 🏊 Swimlane / Partition Diagram

To clarify which subsystem or role owns each activity, the workflow is organized into three organizational swimlanes:

```mermaid
flowchart LR
    subgraph Customer["👤 Customer / Client"]
        C1["Submit Booking Request\n(Sender, Receiver, Weight, Destination)"]
        C2["Make Payment\n(Prepaid / Cash)"]
        C3["Receive Tracking Number & SMS"]
        C4["Receive Parcel & Sign Delivery"]
    end

    subgraph System["⚙️ System Controller / Java & Database Engine"]
        S1["Validate Input & Dimensions"]
        S2["Compute Charge Formula"]
        S3["Generate Unique TRK ID"]
        S4["Create DB Records (parcels, customers)"]
        S5["Log Tracking Audit: 'Booked'"]
        S6["Assign Delivery Personnel"]
        S7["Update Status: 'In Transit' → 'Out for Delivery'"]
        S8["Final Status Update: 'Delivered' / 'Returned'"]
    end

    subgraph Operations["🚚 Delivery Personnel & Hub Operations"]
        O1["Receive Assigned Parcel Consignment"]
        O2["Transport to Destination Hub"]
        O3["Execute Delivery Attempt"]
        O4{"Customer\nAvailable?"}
        O5["Collect Signature / Confirm Delivery"]
        O6["Log Attempt Failure Reason"]
    end

    C1 --> S1
    S1 --> S2
    S2 --> S3
    S3 --> S4
    S4 --> S5
    S4 --> C2
    C2 --> C3
    S5 --> S6
    S6 --> O1
    O1 --> O2
    O2 --> S7
    S7 --> O3
    O3 --> O4
    O4 -->|Yes| O5
    O5 --> S8
    S8 --> C4
    O4 -->|No| O6
    O6 --> S8
```

---

## 📋 Activity Workflow Specifications

### 1. Initial State & Order Reception
- **Action:** User/Admin enters booking information via the Web Portal or Java Console (`Main.java`).
- **Data Required:** Sender ID, Receiver ID, Parcel Type, Weight (kg), Destination, Distance Category (`Local`, `Regional`, `National`, `International`), and Service Type (`Standard`, `Express`, `Overnight`).

### 2. Fork Synchronization (Concurrency)
Once validated, the process splits into **two concurrent threads**:
1. **Physical Logistics Thread:**
   - Evaluates service type priority (`Overnight` $\rightarrow$ high-priority air route, `Express` $\rightarrow$ express surface, `Standard` $\rightarrow$ regular ground).
   - Queries `delivery_personnel` for available personnel in the target delivery zone.
   - Updates `delivery_assignments` and sets parcel status to `In Transit`.
   - Transitions to `Out for Delivery` when assigned staff initiates the route.
2. **Billing & Customer Communication Thread:**
   - Auto-generates a collision-free 14-character tracking code (`TRK` + `YYYYMMDDHHMMSS` + `4-digit random`).
   - Issues invoice billing statement:
     $$\text{Charge} = \text{Weight} \times \text{BaseRate} \times \text{DistanceMultiplier}$$
   - Reconciles payment and emits tracking notification via SMS/Email webhook.

### 3. Delivery Attempt & Exception Handling
- The courier agent attempts delivery at the receiver's address.
- **Success Path:** Customer signs acknowledgment $\rightarrow$ status becomes `Delivered` $\rightarrow$ `delivered_date` set to current timestamp.
- **Failure Path:** Customer unavailable, premises locked, or incorrect address:
  - System logs an entry in `delivery_attempts` with specific failure reason.
  - If attempts $< 3$: Route rescheduled for next business day.
  - If attempts $\ge 3$ or address unresolvable: Parcel marked `Returned` and routed back to origin sender.

### 4. Join Synchronization & Final State
- Both physical delivery resolution and administrative invoice records synchronize at the **Join Bar**.
- The tracking history audit log in `shipment_tracking` is closed and committed.
- Reaches the terminal **Final Activity State**.
