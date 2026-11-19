# Testing Document — Courier and Parcel Tracking Management System

**College:** Velalar College of Engineering and Technology, Erode-12  
**Department:** Information Technology | **Class:** 25IT3A&B

---

## 1. Test Plan

### 1.1 Scope
Unit-level and functional testing for all modules: Customer CRUD, Parcel Management, Personnel Management, Delivery Assignment, Delivery Attempts, Reports, Python Analytics.

### 1.2 Testing Types Used
- **Black-Box Testing:** Validating outputs against expected results without looking at code
- **Positive Testing:** Valid inputs to verify correct output
- **Negative Testing:** Invalid/boundary inputs to verify error handling

### 1.3 Tools
- Manual console-based testing (Java)
- Python script execution with assertion checks

---

## 2. Test Cases

| Test ID | Module | Test Description | Input | Expected Output | Actual Output | Status |
|---|---|---|---|---|---|---|
| TC-01 | Customer | Add a new customer with valid data | Name: "Ravi Kumar", Contact: "9876543210", Address: "12 MG Road, Chennai" | Customer added with auto-generated ID (e.g., 1); success message | As Expected | ✅ Pass |
| TC-02 | Customer | Add customer with empty name | Name: "" | Validation error or DB constraint error | As Expected | ✅ Pass |
| TC-03 | Customer | View customer by valid ID | Customer ID: 1 | Displays Ravi Kumar's details in table format | As Expected | ✅ Pass |
| TC-04 | Customer | View customer by non-existent ID | Customer ID: 9999 | "[!] Customer not found." | As Expected | ✅ Pass |
| TC-05 | Customer | Update customer address | ID: 1, New Address: "45 Anna Nagar, Chennai" | "✔ Updated." message | As Expected | ✅ Pass |
| TC-06 | Customer | Delete customer with valid ID | Customer ID: 8 | "✔ Deleted." message | As Expected | ✅ Pass |
| TC-07 | Parcel | Book parcel with valid data | Sender: 1, Receiver: 2, Type: Document, Weight: 0.5 kg, Dest: Mumbai, Distance: National, Service: Express | Tracking number generated (e.g., TRK20241105143022xxxx), Charge = 0.5 × 80 × 2.0 = ₹80.00 | As Expected | ✅ Pass |
| TC-08 | Parcel | Charge calculation — Standard/Local | Weight: 2.0 kg, Service: Standard, Distance: Local | Charge = 2.0 × 50 × 1.0 = ₹100.00 | As Expected | ✅ Pass |
| TC-09 | Parcel | Charge calculation — Overnight/International | Weight: 1.0 kg, Service: Overnight, Distance: International | Charge = 1.0 × 120 × 3.5 = ₹420.00 | As Expected | ✅ Pass |
| TC-10 | Parcel | Search by tracking number (valid) | Tracking: TRK20241105143022xxxx | Displays full parcel details | As Expected | ✅ Pass |
| TC-11 | Parcel | Search by tracking number (invalid) | Tracking: TRK000000 | "[!] Parcel not found." | As Expected | ✅ Pass |
| TC-12 | Parcel | Update parcel status to "In Transit" | Parcel ID: 1, Status: In Transit, Location: "Delhi Hub" | "✔ Status updated." + tracking entry logged | As Expected | ✅ Pass |
| TC-13 | Personnel | Add delivery personnel | Name: "Suresh M", Contact: "9345678901", Area: "South Chennai" | Personnel added with ID; status = Available | As Expected | ✅ Pass |
| TC-14 | Assignment | Assign available personnel to parcel | Parcel ID: 1, Personnel ID: 1 | "✔ Assigned."; Personnel status changes to On Delivery | As Expected | ✅ Pass |
| TC-15 | Assignment | Assign personnel already On Delivery | Parcel ID: 2, Personnel ID: 1 | "[WARN] Personnel is already on a delivery." | As Expected | ✅ Pass |
| TC-16 | Delivery Attempt | Record successful attempt | Parcel ID: 1, Personnel ID: 1, Status: Successful | "✔ Attempt recorded."; failure_reason = NULL | As Expected | ✅ Pass |
| TC-17 | Delivery Attempt | Record failed attempt with reason | Parcel ID: 2, Personnel ID: 2, Status: Failed, Reason: "Customer absent" | "✔ Attempt recorded."; failure_reason = "Customer absent" | As Expected | ✅ Pass |
| TC-18 | Report | View tracking history for parcel | Parcel ID: 1 | Shows all status changes with locations and timestamps | As Expected | ✅ Pass |
| TC-19 | Report | Pending deliveries report | — | Lists all parcels with Booked/In Transit/Out for Delivery status | As Expected | ✅ Pass |
| TC-20 | Report | Personnel workload report | — | Shows each person's total and completed assignment counts | As Expected | ✅ Pass |
| TC-21 | Python | data_retrieval.py with valid DB | DB exists with sample data | Prints table record counts; all DataFrames populated | As Expected | ✅ Pass |
| TC-22 | Python | data_retrieval.py with missing DB | DB file deleted | "[ERROR] Database not found" message; graceful exit | As Expected | ✅ Pass |
| TC-23 | Python | visualization.py chart generation | Valid DB with sample data | 6 PNG files created in python/visualizations/ | As Expected | ✅ Pass |
| TC-24 | Parcel | Book parcel with invalid weight input | Weight: "abc" | "Please enter a valid number." re-prompt | As Expected | ✅ Pass |
| TC-25 | Customer | List all customers — empty table | No customers in DB | "No customers found." message | As Expected | ✅ Pass |

---

## 3. Test Coverage Summary

| Module | Tests | Passed | Failed |
|---|---|---|---|
| Customer Management | 6 | 6 | 0 |
| Parcel Management | 7 | 7 | 0 |
| Personnel Management | 1 | 1 | 0 |
| Delivery Assignment | 2 | 2 | 0 |
| Delivery Attempts | 2 | 2 | 0 |
| Reports | 3 | 3 | 0 |
| Python Analytics | 3 | 3 | 0 |
| Input Validation | 1 | 1 | 0 |
| **Total** | **25** | **25** | **0** |

---

## 4. Boundary Value Analysis

| Parameter | Min | Normal | Max | Tested |
|---|---|---|---|---|
| Weight (kg) | 0.1 | 5.0 | 100.0 | ✅ |
| Customer ID | 1 | 50 | Integer.MAX_VALUE | ✅ |
| Tracking Number length | 23 chars | 23 chars | 23 chars | ✅ |
| Delivery status options | 5 valid | — | 5 valid | ✅ |
