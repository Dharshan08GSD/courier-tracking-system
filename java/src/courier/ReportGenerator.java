package courier;

import java.sql.*;

/** Generates various operational reports from the database. */
public class ReportGenerator {

    private final Connection conn;

    public ReportGenerator() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // ------------------------------------------------- TRACKING HISTORY
    public void getTrackingHistory(int parcelId) {
        // Print parcel header
        String parcelSql = "SELECT tracking_number, destination, status FROM parcels WHERE parcel_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(parcelSql)) {
            ps.setInt(1, parcelId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) { System.out.println("[INFO] Parcel not found."); return; }
            System.out.println("\n  Tracking Number : " + rs.getString("tracking_number"));
            System.out.println("  Destination     : " + rs.getString("destination"));
            System.out.println("  Current Status  : " + rs.getString("status"));
        } catch (SQLException e) {
            System.err.println("[ERROR] getTrackingHistory header: " + e.getMessage()); return;
        }

        // Print history
        String sql = "SELECT status, location, update_time FROM shipment_tracking " +
                     "WHERE parcel_id = ? ORDER BY tracking_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parcelId);
            ResultSet rs = ps.executeQuery();
            System.out.println("\n  +--------------------+---------------------+---------------------+");
            System.out.println("  | Status             | Location            | Timestamp           |");
            System.out.println("  +--------------------+---------------------+---------------------+");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("  | %-18s | %-19s | %-19s |%n",
                    rs.getString("status"), rs.getString("location"), rs.getString("update_time"));
            }
            if (!found) System.out.println("  | No tracking history available.                                 |");
            System.out.println("  +--------------------+---------------------+---------------------+");
        } catch (SQLException e) {
            System.err.println("[ERROR] getTrackingHistory: " + e.getMessage());
        }
    }

    // ------------------------------------------------- PENDING DELIVERIES
    public void getPendingDeliveries() {
        String sql = "SELECT p.parcel_id, p.tracking_number, c1.name AS sender, c2.name AS receiver, " +
                     "p.destination, p.status, p.booking_date " +
                     "FROM parcels p " +
                     "JOIN customers c1 ON p.sender_id   = c1.customer_id " +
                     "JOIN customers c2 ON p.receiver_id = c2.customer_id " +
                     "WHERE p.status IN ('Booked','In Transit','Out for Delivery') ORDER BY p.parcel_id";
        printParcelReport(sql, "PENDING DELIVERIES");
    }

    // ------------------------------------------------- DELIVERED PARCELS
    public void getDeliveredParcels() {
        String sql = "SELECT p.parcel_id, p.tracking_number, c1.name AS sender, c2.name AS receiver, " +
                     "p.destination, p.status, p.booking_date " +
                     "FROM parcels p " +
                     "JOIN customers c1 ON p.sender_id   = c1.customer_id " +
                     "JOIN customers c2 ON p.receiver_id = c2.customer_id " +
                     "WHERE p.status = 'Delivered' ORDER BY p.parcel_id";
        printParcelReport(sql, "DELIVERED PARCELS");
    }

    // ------------------------------------------------- RETURNED PARCELS
    public void getReturnedParcels() {
        String sql = "SELECT p.parcel_id, p.tracking_number, c1.name AS sender, c2.name AS receiver, " +
                     "p.destination, p.status, p.booking_date " +
                     "FROM parcels p " +
                     "JOIN customers c1 ON p.sender_id   = c1.customer_id " +
                     "JOIN customers c2 ON p.receiver_id = c2.customer_id " +
                     "WHERE p.status = 'Returned' ORDER BY p.parcel_id";
        printParcelReport(sql, "RETURNED PARCELS");
    }

    private void printParcelReport(String sql, String title) {
        System.out.println("\n  ========== " + title + " ==========");
        System.out.println("  +-----+--------------------+------------------+------------------+------------------+------------------+------------+");
        System.out.println("  | ID  | Tracking No.       | Sender           | Receiver         | Destination      | Status           | Booked On  |");
        System.out.println("  +-----+--------------------+------------------+------------------+------------------+------------------+------------+");
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.printf("  | %-3d | %-18s | %-16s | %-16s | %-16s | %-16s | %-10s |%n",
                    rs.getInt("parcel_id"), rs.getString("tracking_number"),
                    rs.getString("sender"), rs.getString("receiver"),
                    rs.getString("destination"), rs.getString("status"),
                    rs.getString("booking_date"));
            }
            if (count == 0) System.out.println("  | No records found.                                                                                                             |");
            System.out.println("  +-----+--------------------+------------------+------------------+------------------+------------------+------------+");
            System.out.println("  Total: " + count + " record(s).\n");
        } catch (SQLException e) {
            System.err.println("[ERROR] printParcelReport: " + e.getMessage());
        }
    }

    // ------------------------------------------------- PERSONNEL WORKLOAD
    public void getPersonnelWorkload() {
        String sql = "SELECT dp.personnel_id, dp.name, dp.assigned_area, dp.status, " +
                     "COUNT(da.assignment_id) AS total_assignments, " +
                     "SUM(CASE WHEN da.status='Completed' THEN 1 ELSE 0 END) AS completed " +
                     "FROM delivery_personnel dp " +
                     "LEFT JOIN delivery_assignments da ON dp.personnel_id = da.personnel_id " +
                     "GROUP BY dp.personnel_id ORDER BY total_assignments DESC";
        System.out.println("\n  ========== DELIVERY PERSONNEL WORKLOAD ==========");
        System.out.println("  +-----+----------------------+------------------+-----------+-------+----------+");
        System.out.println("  | ID  | Name                 | Area             | Status    | Total | Done     |");
        System.out.println("  +-----+----------------------+------------------+-----------+-------+----------+");
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("  | %-3d | %-20s | %-16s | %-9s | %-5d | %-8d |%n",
                    rs.getInt("personnel_id"), rs.getString("name"),
                    rs.getString("assigned_area"), rs.getString("status"),
                    rs.getInt("total_assignments"), rs.getInt("completed"));
            }
            System.out.println("  +-----+----------------------+------------------+-----------+-------+----------+");
        } catch (SQLException e) {
            System.err.println("[ERROR] getPersonnelWorkload: " + e.getMessage());
        }
    }

    // ------------------------------------------------- DELIVERY SUMMARY
    public void getDeliverySummary() {
        String sql = "SELECT status, COUNT(*) as cnt FROM parcels GROUP BY status";
        System.out.println("\n  ========== DELIVERY SUMMARY ==========");
        System.out.println("  +---------------------+-------+");
        System.out.println("  | Status              | Count |");
        System.out.println("  +---------------------+-------+");
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int total = 0;
            while (rs.next()) {
                int c = rs.getInt("cnt");
                total += c;
                System.out.printf("  | %-19s | %-5d |%n", rs.getString("status"), c);
            }
            System.out.println("  +---------------------+-------+");
            System.out.printf("  | %-19s | %-5d |%n", "TOTAL", total);
            System.out.println("  +---------------------+-------+\n");
        } catch (SQLException e) {
            System.err.println("[ERROR] getDeliverySummary: " + e.getMessage());
        }
    }
}
