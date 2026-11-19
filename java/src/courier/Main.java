package courier;

import java.util.*;
import java.time.LocalDate;

/**
 * Main entry point — Console-based menu-driven Courier & Parcel Tracking System.
 * Compile & run via: compile_and_run.bat
 */
public class Main {

    static Scanner sc = new Scanner(System.in);
    static CustomerDAO         customerDAO  = new CustomerDAO();
    static ParcelDAO           parcelDAO    = new ParcelDAO();
    static DeliveryPersonnelDAO personnelDAO = new DeliveryPersonnelDAO();
    static DeliveryDAO         deliveryDAO  = new DeliveryDAO();
    static ReportGenerator     reporter     = new ReportGenerator();

    public static void main(String[] args) {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1 -> customerMenu();
                case 2 -> parcelMenu();
                case 3 -> personnelMenu();
                case 4 -> assignmentMenu();
                case 5 -> deliveryAttemptMenu();
                case 6 -> reportMenu();
                case 7 -> { running = false; System.out.println("\n  Goodbye! Thank you for using the system.\n"); }
                default -> System.out.println("  [!] Invalid option. Please try again.");
            }
        }
        DatabaseManager.getInstance().closeConnection();
    }

    // ================================================================ BANNERS
    private static void printBanner() {
        System.out.println("\n+========================================================+");
        System.out.println("|   COURIER AND PARCEL TRACKING MANAGEMENT SYSTEM        |");
        System.out.println("|   Velalar College of Engineering & Technology           |");
        System.out.println("|   Department of Information Technology                 |");
        System.out.println("+========================================================+\n");
    }

    private static void printMainMenu() {
        System.out.println("\n  ============== MAIN MENU ==============");
        System.out.println("  1. Customer Management");
        System.out.println("  2. Parcel Management");
        System.out.println("  3. Delivery Personnel Management");
        System.out.println("  4. Delivery Assignment");
        System.out.println("  5. Delivery Attempts");
        System.out.println("  6. Reports");
        System.out.println("  7. Exit");
        System.out.println("  ========================================");
    }

    // ================================================================ CUSTOMER MENU
    private static void customerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n  -- Customer Management --");
            System.out.println("  1. Add Customer");
            System.out.println("  2. View Customer by ID");
            System.out.println("  3. List All Customers");
            System.out.println("  4. Update Customer");
            System.out.println("  5. Delete Customer");
            System.out.println("  0. Back");
            int ch = readInt("  Choice: ");
            switch (ch) {
                case 1 -> {
                    System.out.print("  Name           : "); String name = sc.nextLine().trim();
                    System.out.print("  Contact Number : "); String contact = sc.nextLine().trim();
                    System.out.print("  Address        : "); String address = sc.nextLine().trim();
                    Customer c = new Customer(0, name, contact, address);
                    if (customerDAO.addCustomer(c))
                        System.out.println("  [OK] Customer added with ID: " + c.getCustomerId());
                    else System.out.println("  [FAIL] Failed to add customer.");
                }
                case 2 -> {
                    int id = readInt("  Customer ID: ");
                    Customer c = customerDAO.getCustomerById(id);
                    if (c != null) {
                        System.out.println("\n  +------+----------------------+---------------+-------------------------------------+");
                        System.out.println("  | ID   | Name                 | Contact       | Address                             |");
                        System.out.println("  +------+----------------------+---------------+-------------------------------------+");
                        System.out.println("  " + c);
                        System.out.println("  +------+----------------------+---------------+-------------------------------------+");
                    } else System.out.println("  [!] Customer not found.");
                }
                case 3 -> {
                    List<Customer> list = customerDAO.getAllCustomers();
                    System.out.println("\n  +------+----------------------+---------------+-------------------------------------+");
                    System.out.println("  | ID   | Name                 | Contact       | Address                             |");
                    System.out.println("  +------+----------------------+---------------+-------------------------------------+");
                    for (Customer c : list) System.out.println("  " + c);
                    if (list.isEmpty()) System.out.println("  | No customers found.                                                           |");
                    System.out.println("  +------+----------------------+---------------+-------------------------------------+");
                    System.out.println("  Total: " + list.size() + " customer(s).");
                }
                case 4 -> {
                    int id = readInt("  Customer ID to update: ");
                    Customer c = customerDAO.getCustomerById(id);
                    if (c == null) { System.out.println("  [!] Not found."); break; }
                    System.out.print("  New Name     [" + c.getName() + "]: "); String n = sc.nextLine().trim();
                    System.out.print("  New Contact  [" + c.getContactNumber() + "]: "); String ct = sc.nextLine().trim();
                    System.out.print("  New Address  [" + c.getAddress() + "]: "); String ad = sc.nextLine().trim();
                    if (!n.isEmpty())  c.setName(n);
                    if (!ct.isEmpty()) c.setContactNumber(ct);
                    if (!ad.isEmpty()) c.setAddress(ad);
                    System.out.println(customerDAO.updateCustomer(c) ? "  [OK] Updated." : "  [FAIL] Update failed.");
                }
                case 5 -> {
                    int id = readInt("  Customer ID to delete: ");
                    System.out.print("  Confirm delete? (yes/no): ");
                    if ("yes".equalsIgnoreCase(sc.nextLine().trim()))
                        System.out.println(customerDAO.deleteCustomer(id) ? "  [OK] Deleted." : "  [FAIL] Delete failed.");
                }
                case 0 -> back = true;
                default -> System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ================================================================ PARCEL MENU
    private static void parcelMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n  -- Parcel Management --");
            System.out.println("  1. Book a Parcel");
            System.out.println("  2. View Parcel by ID");
            System.out.println("  3. View Parcel by Tracking Number");
            System.out.println("  4. List All Parcels");
            System.out.println("  5. Update Parcel Status");
            System.out.println("  0. Back");
            int ch = readInt("  Choice: ");
            switch (ch) {
                case 1 -> {
                    Parcel p = new Parcel();
                    int sId = readInt("  Sender Customer ID   : "); p.setSenderId(sId);
                    int rId = readInt("  Receiver Customer ID : "); p.setReceiverId(rId);
                    System.out.println("  Parcel Types: Document | Small Package | Medium Package | Large Package | Fragile");
                    System.out.print("  Parcel Type     : "); p.setParcelType(sc.nextLine().trim());
                    double w = readDouble("  Weight (kg)     : "); p.setWeight(w);
                    System.out.print("  Destination     : "); p.setDestination(sc.nextLine().trim());
                    System.out.println("  Distance Category: Local | Regional | National | International");
                    System.out.print("  Distance Category: "); p.setDistanceCategory(sc.nextLine().trim());
                    System.out.println("  Service Type: Standard | Express | Overnight");
                    System.out.print("  Service Type    : "); p.setServiceType(sc.nextLine().trim());
                    p.setBookingDate(LocalDate.now().toString());

                    // Preview charge
                    double charge = parcelDAO.calculateCharge(p.getWeight(), p.getServiceType(), p.getDistanceCategory());
                    System.out.printf("  Estimated Charge: Rs. %.2f%n", charge);
                    System.out.print("  Confirm booking? (yes/no): ");
                    if ("yes".equalsIgnoreCase(sc.nextLine().trim())) {
                        if (parcelDAO.bookParcel(p)) {
                            System.out.println("  [OK] Parcel booked successfully!");
                            System.out.println("  Tracking Number : " + p.getTrackingNumber());
                            System.out.printf ("  Delivery Charge : Rs. %.2f%n", p.getDeliveryCharge());
                        } else System.out.println("  [FAIL] Booking failed.");
                    }
                }
                case 2 -> {
                    int id = readInt("  Parcel ID: ");
                    Parcel p = parcelDAO.getParcelById(id);
                    System.out.println(p != null ? p : "  [!] Parcel not found.");
                }
                case 3 -> {
                    System.out.print("  Tracking Number: ");
                    String tn = sc.nextLine().trim();
                    Parcel p = parcelDAO.getParcelByTrackingNumber(tn);
                    System.out.println(p != null ? p : "  [!] Parcel not found.");
                }
                case 4 -> {
                    List<Parcel> list = parcelDAO.getAllParcels();
                    System.out.println("\n  +-----+--------------------+------------------+------------+--------+-----------+");
                    System.out.println("  | ID  | Tracking No.       | Destination      | Service    | Wt(kg) | Status    |");
                    System.out.println("  +-----+--------------------+------------------+------------+--------+-----------+");
                    for (Parcel p : list)
                        System.out.printf("  | %-3d | %-18s | %-16s | %-10s | %-6.1f | %-9s |%n",
                            p.getParcelId(), p.getTrackingNumber(), p.getDestination(),
                            p.getServiceType(), p.getWeight(), p.getStatus());
                    if (list.isEmpty()) System.out.println("  | No parcels found.                                                            |");
                    System.out.println("  +-----+--------------------+------------------+------------+--------+-----------+");
                    System.out.println("  Total: " + list.size() + " parcel(s).");
                }
                case 5 -> {
                    int id = readInt("  Parcel ID       : ");
                    System.out.println("  Statuses: Booked | In Transit | Out for Delivery | Delivered | Returned");
                    System.out.print("  New Status      : "); String status = sc.nextLine().trim();
                    System.out.print("  Current Location: "); String loc    = sc.nextLine().trim();
                    System.out.println(parcelDAO.updateParcelStatus(id, status, loc) ? "  [OK] Status updated." : "  [FAIL] Update failed.");
                }
                case 0 -> back = true;
                default -> System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ================================================================ PERSONNEL MENU
    private static void personnelMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n  -- Delivery Personnel Management --");
            System.out.println("  1. Add Personnel");
            System.out.println("  2. View Personnel by ID");
            System.out.println("  3. List All Personnel");
            System.out.println("  4. Update Personnel");
            System.out.println("  5. Delete Personnel");
            System.out.println("  0. Back");
            int ch = readInt("  Choice: ");
            switch (ch) {
                case 1 -> {
                    System.out.print("  Name           : "); String name = sc.nextLine().trim();
                    System.out.print("  Contact Number : "); String contact = sc.nextLine().trim();
                    System.out.print("  Assigned Area  : "); String area = sc.nextLine().trim();
                    DeliveryPersonnel dp = new DeliveryPersonnel(0, name, contact, area, "Available");
                    System.out.println(personnelDAO.addPersonnel(dp)
                        ? "  [OK] Personnel added with ID: " + dp.getPersonnelId()
                        : "  [FAIL] Failed.");
                }
                case 2 -> {
                    int id = readInt("  Personnel ID: ");
                    DeliveryPersonnel dp = personnelDAO.getPersonnelById(id);
                    if (dp != null) {
                        System.out.println("\n  +------+----------------------+---------------+--------------------+--------------+");
                        System.out.println("  | ID   | Name                 | Contact       | Area               | Status       |");
                        System.out.println("  +------+----------------------+---------------+--------------------+--------------+");
                        System.out.println("  " + dp);
                        System.out.println("  +------+----------------------+---------------+--------------------+--------------+");
                    } else System.out.println("  [!] Personnel not found.");
                }
                case 3 -> {
                    List<DeliveryPersonnel> list = personnelDAO.getAllPersonnel();
                    System.out.println("\n  +------+----------------------+---------------+--------------------+--------------+");
                    System.out.println("  | ID   | Name                 | Contact       | Area               | Status       |");
                    System.out.println("  +------+----------------------+---------------+--------------------+--------------+");
                    for (DeliveryPersonnel dp : list) System.out.println("  " + dp);
                    if (list.isEmpty()) System.out.println("  | No personnel found.                                                           |");
                    System.out.println("  +------+----------------------+---------------+--------------------+--------------+");
                    System.out.println("  Total: " + list.size() + " personnel.");
                }
                case 4 -> {
                    int id = readInt("  Personnel ID to update: ");
                    DeliveryPersonnel dp = personnelDAO.getPersonnelById(id);
                    if (dp == null) { System.out.println("  [!] Not found."); break; }
                    System.out.print("  New Name    [" + dp.getName() + "]: "); String n = sc.nextLine().trim();
                    System.out.print("  New Contact [" + dp.getContactNumber() + "]: "); String ct = sc.nextLine().trim();
                    System.out.print("  New Area    [" + dp.getAssignedArea() + "]: "); String ar = sc.nextLine().trim();
                    System.out.println("  Status: Available | On Delivery | Off Duty");
                    System.out.print("  New Status  [" + dp.getStatus() + "]: "); String st = sc.nextLine().trim();
                    if (!n.isEmpty())  dp.setName(n);
                    if (!ct.isEmpty()) dp.setContactNumber(ct);
                    if (!ar.isEmpty()) dp.setAssignedArea(ar);
                    if (!st.isEmpty()) dp.setStatus(st);
                    System.out.println(personnelDAO.updatePersonnel(dp) ? "  [OK] Updated." : "  [FAIL] Failed.");
                }
                case 5 -> {
                    int id = readInt("  Personnel ID to delete: ");
                    System.out.print("  Confirm? (yes/no): ");
                    if ("yes".equalsIgnoreCase(sc.nextLine().trim()))
                        System.out.println(personnelDAO.deletePersonnel(id) ? "  [OK] Deleted." : "  [FAIL] Failed.");
                }
                case 0 -> back = true;
                default -> System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ================================================================ ASSIGNMENT MENU
    private static void assignmentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n  -- Delivery Assignment --");
            System.out.println("  1. Assign Parcel to Personnel");
            System.out.println("  2. View Assignments by Parcel");
            System.out.println("  3. View Assignments by Personnel");
            System.out.println("  4. Update Assignment Status");
            System.out.println("  0. Back");
            int ch = readInt("  Choice: ");
            switch (ch) {
                case 1 -> {
                    int pId = readInt("  Parcel ID    : ");
                    int dpId = readInt("  Personnel ID : ");
                    System.out.println(deliveryDAO.assignParcel(pId, dpId) ? "  [OK] Assigned." : "  [FAIL] Assignment failed.");
                }
                case 2 -> { int id = readInt("  Parcel ID: "); deliveryDAO.printAssignmentsByParcel(id); }
                case 3 -> { int id = readInt("  Personnel ID: "); deliveryDAO.printAssignmentsByPersonnel(id); }
                case 4 -> {
                    int aid = readInt("  Assignment ID: ");
                    System.out.println("  Status: Assigned | In Progress | Completed | Failed");
                    System.out.print("  New Status: "); String st = sc.nextLine().trim();
                    System.out.println(deliveryDAO.updateAssignmentStatus(aid, st) ? "  [OK] Updated." : "  [FAIL] Failed.");
                }
                case 0 -> back = true;
                default -> System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ================================================================ DELIVERY ATTEMPT MENU
    private static void deliveryAttemptMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n  -- Delivery Attempts --");
            System.out.println("  1. Record Delivery Attempt");
            System.out.println("  2. View Attempts for a Parcel");
            System.out.println("  0. Back");
            int ch = readInt("  Choice: ");
            switch (ch) {
                case 1 -> {
                    int pId  = readInt("  Parcel ID    : ");
                    int dpId = readInt("  Personnel ID : ");
                    System.out.println("  Delivery Status: Successful | Failed");
                    System.out.print("  Status        : "); String status = sc.nextLine().trim();
                    String reason = null;
                    if ("Failed".equalsIgnoreCase(status)) {
                        System.out.print("  Failure Reason: "); reason = sc.nextLine().trim();
                    }
                    System.out.println(deliveryDAO.recordDeliveryAttempt(pId, dpId, status, reason)
                        ? "  [OK] Attempt recorded." : "  [FAIL] Failed.");
                }
                case 2 -> { int id = readInt("  Parcel ID: "); deliveryDAO.printAttemptsByParcel(id); }
                case 0 -> back = true;
                default -> System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ================================================================ REPORT MENU
    private static void reportMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n  -- Reports --");
            System.out.println("  1. Tracking History for a Parcel");
            System.out.println("  2. Pending Deliveries");
            System.out.println("  3. Delivered Parcels");
            System.out.println("  4. Returned Parcels");
            System.out.println("  5. Delivery Personnel Workload");
            System.out.println("  6. Overall Delivery Summary");
            System.out.println("  0. Back");
            int ch = readInt("  Choice: ");
            switch (ch) {
                case 1 -> { int id = readInt("  Parcel ID: "); reporter.getTrackingHistory(id); }
                case 2 -> reporter.getPendingDeliveries();
                case 3 -> reporter.getDeliveredParcels();
                case 4 -> reporter.getReturnedParcels();
                case 5 -> reporter.getPersonnelWorkload();
                case 6 -> reporter.getDeliverySummary();
                case 0 -> back = true;
                default -> System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ================================================================ HELPERS
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try { return Integer.parseInt(line); }
            catch (NumberFormatException e) { System.out.println("  [!] Please enter a valid number."); }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try { return Double.parseDouble(line); }
            catch (NumberFormatException e) { System.out.println("  [!] Please enter a valid number."); }
        }
    }
}
