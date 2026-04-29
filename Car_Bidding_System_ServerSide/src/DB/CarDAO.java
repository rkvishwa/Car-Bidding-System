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
        String carId = generateCarId();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, carId);
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
            WHERE status IN ('PENDING', 'DELETED_BY_SELLER', 'APPROVED', 'REJECTED')
            ORDER BY created_at DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String safeDesc = (rs.getString("description") != null ? rs.getString("description") : "No description")
                        .replace("\n", " ")
                        .replace("|", " ");
                String safeImage = (rs.getString("image") != null ? rs.getString("image") : "")
                        .replace("\n", " ")
                        .replace("|", " ");

                sb.append(
                    rs.getString("car_id") + "|" +
                    rs.getString("seller_id") + "|" +
                    rs.getString("title").replace("\n", " ").replace("|", " ") + "|" +
                    rs.getString("brand").replace("\n", " ").replace("|", " ") + "|" +
                    rs.getString("model").replace("\n", " ").replace("|", " ") + "|" +
                    rs.getInt("year") + "|" +
                    rs.getDouble("price_start") + "|" +
                    safeDesc + "|" +
                    safeImage + "|" +
                    rs.getString("created_at") + "|" +
                    rs.getString("status") + "\n"
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
                WHERE seller_id=? AND status='APPROVED' AND car_id NOT IN (SELECT car_id FROM auctions)
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, sellerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sb.append(
                    rs.getString("car_id") + "|" +
                    rs.getString("title").replace("\n", " ").replace("|", " ") + "|" +
                    rs.getString("brand").replace("\n", " ").replace("|", " ") + "|" +
                    rs.getString("model").replace("\n", " ").replace("|", " ") + "|" +
                    (rs.getString("image") != null ? rs.getString("image").replace("\n", " ").replace("|", " ") : "") + "|" +
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
            WHERE seller_id = ? AND status != 'DELETED_BY_SELLER'
            ORDER BY created_at DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sellerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                sb.append(
                    rs.getString("car_id") + "|" +
                    rs.getString("title").replace("\n", " ").replace("|", " ") + "|" +
                    rs.getString("brand").replace("\n", " ").replace("|", " ") + "|" +
                    rs.getString("model").replace("\n", " ").replace("|", " ") + "|" +
                    (rs.getString("image") != null ? rs.getString("image").replace("\n", " ").replace("|", " ") : "") + "|" +
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

    public boolean softDeleteCar(String carId) {
        return updateCarStatus(carId, "DELETED_BY_SELLER");
    }

    public boolean deleteCar(String carId) {
        String sql = "DELETE FROM car WHERE car_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, carId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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