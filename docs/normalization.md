# Normalization — Courier and Parcel Tracking Management System

## Overview
All six tables in the CPTMS schema have been designed to satisfy **Third Normal Form (3NF)** / **BCNF**. The analysis below documents functional dependencies (FDs) and confirms satisfaction of each normal form.

---

## 1. CUSTOMERS

**Relation:** CUSTOMERS(customer_id, name, contact_number, address)  
**Primary Key:** customer_id

### Functional Dependencies
- customer_id → name, contact_number, address

### Normal Form Analysis
| NF | Satisfied? | Reason |
|---|---|---|
| 1NF | ✅ Yes | All attributes are atomic; no repeating groups |
| 2NF | ✅ Yes | PK is a single attribute (no partial dependency possible) |
| 3NF | ✅ Yes | No transitive dependencies: name, contact, address all depend only on customer_id |
| BCNF | ✅ Yes | The only determinant is customer_id, which is a superkey |

---

## 2. DELIVERY_PERSONNEL

**Relation:** DELIVERY_PERSONNEL(personnel_id, name, contact_number, assigned_area, status)  
**Primary Key:** personnel_id

### Functional Dependencies
- personnel_id → name, contact_number, assigned_area, status

### Normal Form Analysis
| NF | Satisfied? | Reason |
|---|---|---|
| 1NF | ✅ Yes | All attributes are single-valued |
| 2NF | ✅ Yes | Single-attribute PK — no partial dependencies |
| 3NF | ✅ Yes | No non-key attribute determines another non-key attribute |
| BCNF | ✅ Yes | Only determinant is personnel_id (the PK) |

---

## 3. PARCELS

**Relation:** PARCELS(parcel_id, tracking_number, sender_id, receiver_id, parcel_type, weight, booking_date, destination, distance_category, service_type, delivery_charge, status)  
**Primary Key:** parcel_id  
**Candidate Key:** tracking_number

### Functional Dependencies
- parcel_id → all attributes
- tracking_number → all attributes *(alternate key)*
- {weight, service_type, distance_category} → delivery_charge *(derived)*

### Normal Form Analysis
| NF | Satisfied? | Reason |
|---|---|---|
| 1NF | ✅ Yes | All attributes are atomic |
| 2NF | ✅ Yes | Single-attribute PK; every attribute fully depends on parcel_id |
| 3NF | ✅ Yes | delivery_charge is computed from attributes of the same tuple — no transitive dependency through a non-key |
| BCNF | ✅ Yes | Both determinants (parcel_id, tracking_number) are superkeys |

> **Note:** delivery_charge is a *derived* attribute stored for performance; it is fully determined by {weight, service_type, distance_category} — all of which belong to the same relation, so no violation occurs.

---

## 4. SHIPMENT_TRACKING

**Relation:** SHIPMENT_TRACKING(tracking_id, parcel_id, status, location, update_time)  
**Primary Key:** tracking_id  
**Foreign Key:** parcel_id → PARCELS

### Functional Dependencies
- tracking_id → parcel_id, status, location, update_time

### Normal Form Analysis
| NF | Satisfied? | Reason |
|---|---|---|
| 1NF | ✅ Yes | Each column is atomic |
| 2NF | ✅ Yes | Single-attribute PK — no partial dependencies |
| 3NF | ✅ Yes | No non-key determines another non-key |
| BCNF | ✅ Yes | Only determinant is tracking_id |

---

## 5. DELIVERY_ASSIGNMENTS

**Relation:** DELIVERY_ASSIGNMENTS(assignment_id, parcel_id, personnel_id, assigned_date, status)  
**Primary Key:** assignment_id  
**Foreign Keys:** parcel_id → PARCELS, personnel_id → DELIVERY_PERSONNEL

### Functional Dependencies
- assignment_id → parcel_id, personnel_id, assigned_date, status

### Normal Form Analysis
| NF | Satisfied? | Reason |
|---|---|---|
| 1NF | ✅ Yes | All values are atomic |
| 2NF | ✅ Yes | PK is a single surrogate key |
| 3NF | ✅ Yes | No transitive dependencies |
| BCNF | ✅ Yes | Only determinant is assignment_id |

---

## 6. DELIVERY_ATTEMPTS

**Relation:** DELIVERY_ATTEMPTS(attempt_id, parcel_id, personnel_id, attempt_date, delivery_status, failure_reason)  
**Primary Key:** attempt_id  
**Foreign Keys:** parcel_id → PARCELS, personnel_id → DELIVERY_PERSONNEL

### Functional Dependencies
- attempt_id → parcel_id, personnel_id, attempt_date, delivery_status, failure_reason
- {delivery_status = 'Successful'} → failure_reason IS NULL *(a business rule, not a FD)*

### Normal Form Analysis
| NF | Satisfied? | Reason |
|---|---|---|
| 1NF | ✅ Yes | All attributes are atomic; failure_reason is nullable (not a repeating group) |
| 2NF | ✅ Yes | Single-attribute PK |
| 3NF | ✅ Yes | No transitive dependencies among non-key attributes |
| BCNF | ✅ Yes | Only determinant is attempt_id |

---

## Summary

| Table | 1NF | 2NF | 3NF | BCNF |
|---|:---:|:---:|:---:|:---:|
| CUSTOMERS | ✅ | ✅ | ✅ | ✅ |
| DELIVERY_PERSONNEL | ✅ | ✅ | ✅ | ✅ |
| PARCELS | ✅ | ✅ | ✅ | ✅ |
| SHIPMENT_TRACKING | ✅ | ✅ | ✅ | ✅ |
| DELIVERY_ASSIGNMENTS | ✅ | ✅ | ✅ | ✅ |
| DELIVERY_ATTEMPTS | ✅ | ✅ | ✅ | ✅ |

All six tables are in **BCNF**, confirming a fully normalized relational schema with no redundancy, partial dependencies, or transitive dependencies.
