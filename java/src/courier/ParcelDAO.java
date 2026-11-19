package courier;

import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Data Access Object for Parcel operations. */
public class ParcelDAO {

    private final Connection conn;

    public ParcelDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // ------------------------------------------------- CHARGE CALCULATION
    public double calculateCharge(double weight, String serviceType, String distanceCategory) {
        double baseRate = switch (serviceType) {
            case "Express"   -> 80.0;
            case "Overnight" -> 120.0;
            default          -> 50.0;  // Standard
        };
        double multiplier = switch (distanceCategory) {
            case "Regional"      -> 1.5;
            case "National"      -> 2.0;
            case "International" -> 3.5;
            default              -> 1.0;  // Local
        };
        return weight * baseRate * multiplier;
    }

    // ------------------------------------------------- TRACKING NUMBER
    private String generateTrackingNumber() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = (int)(Math.random() * 9000) + 1000;
        return "TRK" + ts + rand;
    }

    // ------------------------------------------------- BOOK PARCEL
    public boolean bookParcel(Parcel p) {
        p.setTrackingNumber(generateTrackingNumber());
        double charge = calculateCharge(p.getWeight(), p.getServiceType(), p.getDistanceCategory());
        p.setDeliveryCharge(charge);

        String sql = "INSERT INTO parcels (tracking_number, sender_id, receiver_id, parcel_type, " +
                     "weight, booking_date, destination, distance_category, service_type, delivery_charge, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Booked')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTrackingNumber());
            ps.setInt   (2, p.getSenderId());
            ps.setInt   (3, p.getReceiverId());
            ps.setString(4, p.getParcelType());
            ps.setDouble(5, p.getWeight());
            ps.setString(6, p.getBookingDate());
            ps.setString(7, p.getDestination());
            ps.setString(8, p.getDistanceCategory());
            ps.setString(9, p.getServiceType());
            ps.setDouble(10, p.getDeliveryCharge());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    p.setParcelId(rs.getInt(1));
                    // Log initial status in tracking
                    addTrackingEntry(p.getParcelId(), "Booked", "Origin Facility");
                }
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] bookParcel: " + e.getMessage());
        }
        return false;
    }

    // ------------------------------------------------- GET BY ID
    public Parcel getParcelById(int id) {
        String sql = "SELECT * FROM parcels WHERE parcel_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] getParcelById: " + e.getMessage());
        }
        return null;
    }

    // ------------------------------------------------- GET BY TRACKING NUMBER
    public Parcel getParcelByTrackingNumber(String tn) {
        String sql = "SELECT * FROM parcels WHERE tracking_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ERROR] getParcelByTrackingNumber: " + e.getMessage());
        }
        return null;
    }

    // ------------------------------------------------- GET ALL
    public List<Parcel> getAllParcels() {
        List<Parcel> list = new ArrayList<>();
        String sql = "SELECT * FROM parcels ORDER BY parcel_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ERROR] getAllParcels: " + e.getMessage());
        }
        return list;
    }

    // ------------------------------------------------- UPDATE STATUS
    public boolean updateParcelStatus(int parcelId, String status, String location) {
        String sql = "UPDATE parcels SET status = ? WHERE parcel_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt   (2, parcelId);
            if (ps.executeUpdate() > 0) {
                addTrackingEntry(parcelId, status, location);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] updateParcelStatus: " + e.getMessage());
        }
        return false;
    }

    // ------------------------------------------------- ADD TRACKING ENTRY
    public void addTrackingEntry(int parcelId, String status, String location) {
        String sql = "INSERT INTO shipment_tracking (parcel_id, status, location) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, parcelId);
            ps.setString(2, status);
            ps.setString(3, location);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[WARN] addTrackingEntry: " + e.getMessage());
        }
    }

    // ------------------------------------------------- MAP ROW
    private Parcel mapRow(ResultSet rs) throws SQLException {
        return new Parcel(
            rs.getInt   ("parcel_id"),
            rs.getString("tracking_number"),
            rs.getInt   ("sender_id"),
            rs.getInt   ("receiver_id"),
            rs.getString("parcel_type"),
            rs.getDouble("weight"),
            rs.getString("booking_date"),
            rs.getString("destination"),
            rs.getString("distance_category"),
            rs.getString("service_type"),
            rs.getDouble("delivery_charge"),
            rs.getString("status")
        );
    }
}
