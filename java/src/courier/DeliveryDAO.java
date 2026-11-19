package courier;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** Handles delivery assignments and attempt recording. */
public class DeliveryDAO {

    private final Connection conn;
    private final DeliveryPersonnelDAO personnelDAO;

    public DeliveryDAO() {
        this.conn         = DatabaseManager.getInstance().getConnection();
        this.personnelDAO = new DeliveryPersonnelDAO();
    }

    // ------------------------------------------------- ASSIGN PARCEL
    public boolean assignParcel(int parcelId, int personnelId) {
        // Check personnel is available
        DeliveryPersonnel dp = personnelDAO.getPersonnelById(personnelId);
        if (dp == null) { System.out.println("[WARN] Personnel not found."); return false; }
        if ("On Delivery".equals(dp.getStatus())) {
            System.out.println("[WARN] Personnel is already on a delivery. Choose another.");
            return false;
        }

        String sql = "INSERT INTO delivery_assignments (parcel_id, personnel_id, assigned_date, status) VALUES (?, ?, ?, 'Assigned')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, parcelId);
            ps.setInt   (2, personnelId);
            ps.setString(3, LocalDate.now().toString());
            if (ps.executeUpdate() > 0) {
                personnelDAO.updatePersonnelStatus(personnelId, "On Delivery");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] assignParcel: " + e.getMessage());
        }
        return false;
    }

    // ------------------------------------------------- GET ASSIGNMENTS BY PARCEL
    public void printAssignmentsByParcel(int parcelId) {
        String sql = "SELECT da.assignment_id, dp.name, dp.contact_number, da.assigned_date, da.status " +
                     "FROM delivery_assignments da " +
                     "JOIN delivery_personnel dp ON da.personnel_id = dp.personnel_id " +
                     "WHERE da.parcel_id = ? ORDER BY da.assignment_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parcelId);
            ResultSet rs = ps.executeQuery();
            System.out.println("+-----+----------------------+---------------+------------+-----------+");
            System.out.println("| ID  | Personnel            | Contact       | Date       | Status    |");
            System.out.println("+-----+----------------------+---------------+------------+-----------+");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("| %-3d | %-20s | %-13s | %-10s | %-9s |%n",
                    rs.getInt("assignment_id"), rs.getString("name"),
                    rs.getString("contact_number"), rs.getString("assigned_date"),
                    rs.getString("status"));
            }
            if (!found) System.out.println("| No assignments found for this parcel.                        |");
            System.out.println("+-----+----------------------+---------------+------------+-----------+");
        } catch (SQLException e) {
            System.err.println("[ERROR] printAssignmentsByParcel: " + e.getMessage());
        }
    }

    // ------------------------------------------------- GET ASSIGNMENTS BY PERSONNEL
    public void printAssignmentsByPersonnel(int personnelId) {
        String sql = "SELECT da.assignment_id, da.parcel_id, p.tracking_number, p.destination, da.assigned_date, da.status " +
                     "FROM delivery_assignments da " +
                     "JOIN parcels p ON da.parcel_id = p.parcel_id " +
                     "WHERE da.personnel_id = ? ORDER BY da.assignment_id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personnelId);
            ResultSet rs = ps.executeQuery();
            System.out.println("+-----+----------+--------------------+--------------------+------------+-----------+");
            System.out.println("| ID  | PrcID    | Tracking No.       | Destination        | Date       | Status    |");
            System.out.println("+-----+----------+--------------------+--------------------+------------+-----------+");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("| %-3d | %-8d | %-18s | %-18s | %-10s | %-9s |%n",
                    rs.getInt("assignment_id"), rs.getInt("parcel_id"),
                    rs.getString("tracking_number"), rs.getString("destination"),
                    rs.getString("assigned_date"), rs.getString("status"));
            }
            if (!found) System.out.println("| No assignments found for this personnel.                                        |");
            System.out.println("+-----+----------+--------------------+--------------------+------------+-----------+");
        } catch (SQLException e) {
            System.err.println("[ERROR] printAssignmentsByPersonnel: " + e.getMessage());
        }
    }

    // ------------------------------------------------- UPDATE ASSIGNMENT STATUS
    public boolean updateAssignmentStatus(int assignmentId, String status) {
        String sql = "UPDATE delivery_assignments SET status=? WHERE assignment_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt   (2, assignmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] updateAssignmentStatus: " + e.getMessage());
        }
        return false;
    }

    // ------------------------------------------------- RECORD DELIVERY ATTEMPT
    public boolean recordDeliveryAttempt(int parcelId, int personnelId,
                                          String deliveryStatus, String failureReason) {
        String sql = "INSERT INTO delivery_attempts (parcel_id, personnel_id, attempt_date, delivery_status, failure_reason) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, parcelId);
            ps.setInt   (2, personnelId);
            ps.setString(3, LocalDate.now().toString());
            ps.setString(4, deliveryStatus);
            ps.setString(5, failureReason);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] recordDeliveryAttempt: " + e.getMessage());
        }
        return false;
    }

    // ------------------------------------------------- GET ATTEMPTS BY PARCEL
    public void printAttemptsByParcel(int parcelId) {
        String sql = "SELECT da.attempt_id, dp.name, da.attempt_date, da.delivery_status, da.failure_reason " +
                     "FROM delivery_attempts da " +
                     "JOIN delivery_personnel dp ON da.personnel_id = dp.personnel_id " +
                     "WHERE da.parcel_id = ? ORDER BY da.attempt_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parcelId);
            ResultSet rs = ps.executeQuery();
            System.out.println("+-----+----------------------+------------+------------+-----------------------------+");
            System.out.println("| ID  | Personnel            | Date       | Status     | Failure Reason              |");
            System.out.println("+-----+----------------------+------------+------------+-----------------------------+");
            boolean found = false;
            while (rs.next()) {
                found = true;
                String reason = rs.getString("failure_reason");
                System.out.printf("| %-3d | %-20s | %-10s | %-10s | %-27s |%n",
                    rs.getInt("attempt_id"), rs.getString("name"),
                    rs.getString("attempt_date"), rs.getString("delivery_status"),
                    reason != null ? reason : "-");
            }
            if (!found) System.out.println("| No delivery attempts recorded for this parcel.                              |");
            System.out.println("+-----+----------------------+------------+------------+-----------------------------+");
        } catch (SQLException e) {
            System.err.println("[ERROR] printAttemptsByParcel: " + e.getMessage());
        }
    }
}
