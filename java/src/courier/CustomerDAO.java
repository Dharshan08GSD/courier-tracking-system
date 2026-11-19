package courier;

import java.sql.*;
import java.util.*;

/** Data Access Object for Customer CRUD operations. */
public class CustomerDAO {

    private final Connection conn;

    public CustomerDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // ------------------------------------------------------------------ ADD
    public boolean addCustomer(Customer c) {
        String sql = "INSERT INTO customers (name, contact_number, address) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getContactNumber());
            ps.setString(3, c.getAddress());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) c.setCustomerId(rs.getInt(1));
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] addCustomer: " + e.getMessage());
        }
        return false;
    }

    // --------------------------------------------------------------- GET BY ID
    public Customer getCustomerById(int id) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] getCustomerById: " + e.getMessage());
        }
        return null;
    }

    // --------------------------------------------------------------- GET ALL
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] getAllCustomers: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------------- UPDATE
    public boolean updateCustomer(Customer c) {
        String sql = "UPDATE customers SET name=?, contact_number=?, address=? WHERE customer_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getContactNumber());
            ps.setString(3, c.getAddress());
            ps.setInt(4, c.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] updateCustomer: " + e.getMessage());
        }
        return false;
    }

    // -------------------------------------------------------------- DELETE
    public boolean deleteCustomer(int id) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] deleteCustomer: " + e.getMessage());
        }
        return false;
    }

    // -------------------------------------------------------- HELPER: map row
    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("customer_id"),
            rs.getString("name"),
            rs.getString("contact_number"),
            rs.getString("address")
        );
    }
}
