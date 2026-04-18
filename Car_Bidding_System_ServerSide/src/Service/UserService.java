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

            String sql = "SELECT user_id, role FROM users WHERE email=? AND password=? AND status='ACTIVE'";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("user_id") + ":" + rs.getString("role");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "FAILED";
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
