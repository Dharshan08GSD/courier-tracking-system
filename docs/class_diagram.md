# Class Diagram — Courier and Parcel Tracking Management System

## Class Diagram

```mermaid
classDiagram
    class DatabaseManager {
        -static DatabaseManager instance
        -Connection connection
        -String DB_URL
        +static getInstance() DatabaseManager
        +getConnection() Connection
        -initializeDatabase() void
        -executeSqlFile(String path) void
        +closeConnection() void
    }

    class Customer {
        -int customerId
        -String name
        -String contactNumber
        -String address
        +Customer()
        +Customer(id, name, contact, address)
        +getCustomerId() int
        +getName() String
        +getContactNumber() String
        +getAddress() String
        +setCustomerId(int) void
        +setName(String) void
        +setContactNumber(String) void
        +setAddress(String) void
        +toString() String
    }

    class Parcel {
        -int parcelId
        -String trackingNumber
        -int senderId
        -int receiverId
        -String parcelType
        -double weight
        -String bookingDate
        -String destination
        -String distanceCategory
        -String serviceType
        -double deliveryCharge
        -String status
        +Parcel()
        +Parcel(all fields)
        +getters() various
        +setters() void
        +toString() String
    }

    class DeliveryPersonnel {
        -int personnelId
        -String name
        -String contactNumber
        -String assignedArea
        -String status
        +DeliveryPersonnel()
        +DeliveryPersonnel(all fields)
        +getters() various
        +setters() void
        +toString() String
    }

    class CustomerDAO {
        -Connection conn
        +CustomerDAO()
        +addCustomer(Customer) boolean
        +getCustomerById(int) Customer
        +getAllCustomers() List~Customer~
        +updateCustomer(Customer) boolean
        +deleteCustomer(int) boolean
        -mapRow(ResultSet) Customer
    }

    class ParcelDAO {
        -Connection conn
        +ParcelDAO()
        +calculateCharge(double, String, String) double
        -generateTrackingNumber() String
        +bookParcel(Parcel) boolean
        +getParcelById(int) Parcel
        +getParcelByTrackingNumber(String) Parcel
        +getAllParcels() List~Parcel~
        +updateParcelStatus(int, String, String) boolean
        +addTrackingEntry(int, String, String) void
        -mapRow(ResultSet) Parcel
    }

    class DeliveryPersonnelDAO {
        -Connection conn
        +DeliveryPersonnelDAO()
        +addPersonnel(DeliveryPersonnel) boolean
        +getPersonnelById(int) DeliveryPersonnel
        +getAllPersonnel() List~DeliveryPersonnel~
        +updatePersonnel(DeliveryPersonnel) boolean
        +updatePersonnelStatus(int, String) boolean
        +deletePersonnel(int) boolean
        -mapRow(ResultSet) DeliveryPersonnel
    }

    class DeliveryDAO {
        -Connection conn
        -DeliveryPersonnelDAO personnelDAO
        +DeliveryDAO()
        +assignParcel(int, int) boolean
        +printAssignmentsByParcel(int) void
        +printAssignmentsByPersonnel(int) void
        +updateAssignmentStatus(int, String) boolean
        +recordDeliveryAttempt(int, int, String, String) boolean
        +printAttemptsByParcel(int) void
    }

    class ReportGenerator {
        -Connection conn
        +ReportGenerator()
        +getTrackingHistory(int) void
        +getPendingDeliveries() void
        +getDeliveredParcels() void
        +getReturnedParcels() void
        +getPersonnelWorkload() void
        +getDeliverySummary() void
        -printParcelReport(String, String) void
    }

    class Main {
        -static Scanner sc
        -static CustomerDAO customerDAO
        -static ParcelDAO parcelDAO
        -static DeliveryPersonnelDAO personnelDAO
        -static DeliveryDAO deliveryDAO
        -static ReportGenerator reporter
        +static main(String[]) void
        -static customerMenu() void
        -static parcelMenu() void
        -static personnelMenu() void
        -static assignmentMenu() void
        -static deliveryAttemptMenu() void
        -static reportMenu() void
        -static readInt(String) int
        -static readDouble(String) double
    }

    DatabaseManager <.. CustomerDAO : uses
    DatabaseManager <.. ParcelDAO : uses
    DatabaseManager <.. DeliveryPersonnelDAO : uses
    DatabaseManager <.. DeliveryDAO : uses
    DatabaseManager <.. ReportGenerator : uses
    CustomerDAO ..> Customer : creates/returns
    ParcelDAO ..> Parcel : creates/returns
    DeliveryPersonnelDAO ..> DeliveryPersonnel : creates/returns
    DeliveryDAO --> DeliveryPersonnelDAO : uses
    Main --> CustomerDAO : uses
    Main --> ParcelDAO : uses
    Main --> DeliveryPersonnelDAO : uses
    Main --> DeliveryDAO : uses
    Main --> ReportGenerator : uses
```

---

## Design Patterns Used

| Pattern | Where Used | Purpose |
|---|---|---|
| **Singleton** | `DatabaseManager` | Single DB connection throughout app lifecycle |
| **DAO (Data Access Object)** | All `*DAO` classes | Separation of DB logic from business logic |
| **POJO** | `Customer`, `Parcel`, `DeliveryPersonnel` | Simple data containers with getters/setters |
