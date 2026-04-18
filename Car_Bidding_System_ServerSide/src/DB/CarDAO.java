package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CarDAO {

    public boolean addCar(String sellerId, String title, String brand,
                          String model, int year, double price,
                          String description, String image) {

        String sql = """
            INSERT INTO car
            (car_id, seller_id, title, brand, model, year, price_start, description, image, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, generateCarId());
            ps.setString(2, sellerId);
            ps.setString(3, title);
            ps.setString(4, brand);
            ps.setString(5, model);
            ps.setInt(6, year);
            ps.setDouble(7, price);
            ps.setString(8, description);
            ps.setString(9, image);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getSellerCars(String sellerId) {

        StringBuilder sb = new StringBuilder();

        String sql = "SELECT * FROM car WHERE seller_id=? AND status='APPROVED'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sellerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sb.append(
                    rs.getString("car_id") + "|" +
                    rs.getString("title") + "|" +
                    rs.getString("brand") + "|" +
                    rs.getString("model") + "|" +
                    rs.getInt("year") + "|" +
                    rs.getDouble("price_start") + "|" +
                    rs.getString("image") + "\n"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private String generateCarId() {
        return "CAR" + System.currentTimeMillis();
    }
    
    public String getPendingCars() {

        StringBuilder sb = new StringBuilder();

        String sql = """
            SELECT car_id, seller_id, title, brand, model, year, 
                   price_start, description, image, status, created_at
            FROM car 
            WHERE status='PENDING'
            ORDER BY created_at DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sb.append(
                    rs.getString("car_id") + "|" +
                    rs.getString("seller_id") + "|" +
                    rs.getString("title") + "|" +
                    rs.getString("brand") + "|" +
                    rs.getString("model") + "|" +
                    rs.getInt("year") + "|" +
                    rs.getDouble("price_start") + "|" +
                    (rs.getString("description") != null ? rs.getString("description") : "No description") + "|" +
                    (rs.getString("image") != null ? rs.getString("image") : "") + "|" +
                    rs.getString("created_at") + "\n"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    
    public boolean updateCarStatus(String carId, String status) {

        String sql = "UPDATE car SET status=? WHERE car_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, carId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String getApprovedCars(String sellerId) {
        StringBuilder sb = new StringBuilder();

        try (Connection con = DBConnection.getConnection()) {

            String sql = """
                SELECT car_id, title, brand, model, image, year, price_start 
                FROM car 
                WHERE seller_id=? AND status='APPROVED'
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, sellerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sb.append(
                    rs.getString("car_id") + "|" +
                    rs.getString("title") + "|" +
                    rs.getString("brand") + "|" +
                    rs.getString("model") + "|" +
                    rs.getString("image") + "|" +
                    rs.getInt("year") + "|" +
                    rs.getDouble("price_start")
                ).append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    
    public String getMyCars(String sellerId) {

        StringBuilder sb = new StringBuilder();

        String sql = """
            SELECT 
                car_id,
                title,
                brand,
                model,
                image,
                status,
                created_at,
                year,
                price_start,
                description
            FROM car
            WHERE seller_id = ?
            ORDER BY created_at DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sellerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                sb.append(
                    rs.getString("car_id") + "|" +
                    rs.getString("title") + "|" +
                    rs.getString("brand") + "|" +
                    rs.getString("model") + "|" +
                    rs.getString("image") + "|" +
                    rs.getString("status") + "|" +
                    rs.getString("created_at") + "|" +
                    rs.getInt("year") + "|" +
                    rs.getDouble("price_start") + "\n"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    
    public boolean approveCar(String carId) {
        return updateCarStatus(carId, "APPROVED");
    }

    public boolean rejectCar(String carId) {
        return updateCarStatus(carId, "REJECTED");
    }

    // Get car details for admin
    public String getCarById(String carId) {

        String sql = """
            SELECT car_id, seller_id, title, brand, model, year, 
                   price_start, description, image, status, created_at
            FROM car WHERE car_id = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, carId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("car_id") + "|" +
                       rs.getString("seller_id") + "|" +
                       rs.getString("title") + "|" +
                       rs.getString("brand") + "|" +
                       rs.getString("model") + "|" +
                       rs.getInt("year") + "|" +
                       rs.getDouble("price_start") + "|" +
                       rs.getString("description") + "|" +
                       rs.getString("image") + "|" +
                       rs.getString("status");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "NOT_FOUND";
    }
}