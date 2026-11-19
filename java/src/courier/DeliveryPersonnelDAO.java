package courier;

import java.sql.*;
import java.util.*;

/** Data Access Object for Delivery Personnel CRUD. */
public class DeliveryPersonnelDAO {

    private final Connection conn;

    public DeliveryPersonnelDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // ADD
    public boolean addPersonnel(DeliveryPersonnel dp) {
        String sql = "INSERT INTO delivery_personnel (name, contact_number, assigned_area, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dp.getName());
            ps.setString(2, dp.getContactNumber());
            ps.setString(3, dp.getAssignedArea());
            ps.setString(4, dp.getStatus() != null ? dp.getStatus() : "Available");
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) dp.setPersonnelId(rs.getInt(1));
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] addPersonnel: " + e.getMessage());
        }
        return false;
    }

    // GET BY ID
    public DeliveryPersonnel getPersonnelById(int id) {
        String sql = "SELECT * FROM delivery_personnel WHERE personnel_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] getPersonnelById: " + e.getMessage());
        }
        return null;
    }

    // GET ALL
    public List<DeliveryPersonnel> getAllPersonnel() {
        List<DeliveryPersonnel> list = new ArrayList<>();
        String sql = "SELECT * FROM delivery_personnel ORDER BY personnel_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] getAllPersonnel: " + e.getMessage());
        }
        return list;
    }

    // UPDATE
    public boolean updatePersonnel(DeliveryPersonnel dp) {
        String sql = "UPDATE delivery_personnel SET name=?, contact_number=?, assigned_area=?, status=? WHERE personnel_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dp.getName());
            ps.setString(2, dp.getContactNumber());
            ps.setString(3, dp.getAssignedArea());
            ps.setString(4, dp.getStatus());
            ps.setInt   (5, dp.getPersonnelId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] updatePersonnel: " + e.getMessage());
        }
        return false;
    }

    // UPDATE STATUS
    public boolean updatePersonnelStatus(int id, String status) {
        String sql = "UPDATE delivery_personnel SET status=? WHERE personnel_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt   (2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] updatePersonnelStatus: " + e.getMessage());
        }
        return false;
    }

    // DELETE
    public boolean deletePersonnel(int id) {
        String sql = "DELETE FROM delivery_personnel WHERE personnel_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] deletePersonnel: " + e.getMessage());
        }
        return false;
    }

    // MAP ROW
    private DeliveryPersonnel mapRow(ResultSet rs) throws SQLException {
        return new DeliveryPersonnel(
            rs.getInt   ("personnel_id"),
            rs.getString("name"),
            rs.getString("contact_number"),
            rs.getString("assigned_area"),
            rs.getString("status")
        );
    }
}
