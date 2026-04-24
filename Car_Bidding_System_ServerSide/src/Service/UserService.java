package Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import DB.DBConnection;
import DB.UserDAO;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    public boolean register(String name, String email, String password, String role) {
        System.out.println("Registering user: " + email);
        boolean result = userDAO.registerUser(name, email, password, role);
        System.out.println("User inserted: " + result);
        return result;
        
    }

    public boolean login(String email, String password) {
        return userDAO.login(email, password);
    }
    
    public boolean userExists(String email) {
        return userDAO.userExists(email);
    }
    
    public String loginAndGetRole(String email, String password) {

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT user_id, role, name, status FROM users WHERE email=? AND password=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                if ("PENDING".equals(status)) return "PENDING";
                if ("BANNED".equals(status)) return "BANNED";
                
                return rs.getString("user_id") + ":" + rs.getString("role") + ":" + rs.getString("name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "FAILED";
    }

    public String getPendingUsers() {
        return userDAO.getPendingUsers();
    }

    public boolean approveUser(String userId) {
        return userDAO.approveUser(userId);
    }

    public String forgotPassword(String email) {
        if (!userDAO.userExists(email)) return "FAILED:UserNotFound";
        
        int attempts = userDAO.getForgotAttempts(email);
        if (attempts >= 2) { // 3rd attempt blocks
            userDAO.deleteUserByEmail(email);
            return "FAILED:AccountDeleted";
        }
        
        userDAO.incrementForgotAttempts(email);
        String newPass = "TEMP" + (int)(Math.random() * 9000 + 1000);
        userDAO.resetPassword(email, newPass);
        
        return "SUCCESS:" + newPass;
    }
    
    public String getAllUsers() {

        StringBuilder sb = new StringBuilder();

        String sql = "SELECT user_id, name, role FROM users";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sb.append(
                    rs.getString("user_id") + "|" +
                    rs.getString("name") + "|" +
                    rs.getString("role") + "\n"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
