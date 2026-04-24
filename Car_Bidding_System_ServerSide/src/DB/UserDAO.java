package DB;

import java.sql.*;
import java.util.UUID;

import Util.IDGenerator;

public class UserDAO {

    public boolean registerUser(String name, String email, String password, String role) {
        String sql = "INSERT INTO users VALUES (?, ?, ?, ?, ?, 'ACTIVE', ?, NOW())";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

        	String userId = IDGenerator.generateID("USR", "users", "user_id");

            ps.setString(1, userId);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, role);
            ps.setString(6, "0912345678");

            ps.executeUpdate();
            System.out.println("User Registered!");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
    }

    public boolean login(String email, String password) {
        String sql = "SELECT * FROM users \r\n"
        		+ "WHERE email=? AND password=? AND status='ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean userExists(String email) {

        String sql = "SELECT user_id FROM users WHERE email=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public String getAllUsers() {
        String sql = "SELECT user_id, name, role, status FROM users";
        StringBuilder sb = new StringBuilder();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sb.append(rs.getString("user_id"))
                  .append("|")
                  .append(rs.getString("name"))
                  .append("|")
                  .append(rs.getString("role"))
                  .append("|")
                  .append(rs.getString("status"))
                  .append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    
    public boolean deleteUser(String userId) {

        String sql = "DELETE FROM users WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public boolean banUser(String userId) {

        String sql = "UPDATE users SET status='BANNED' WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public boolean makeAdmin(String userId) {

        String sql = "UPDATE users SET role='ADMIN' WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
}
