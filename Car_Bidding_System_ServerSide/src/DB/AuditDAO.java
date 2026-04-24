package DB;

import java.sql.*;

public class AuditDAO {

    public boolean logAction(String adminId, String action , String targetId) {

        String sql = "INSERT INTO admin_logs (log_id, admin_id, action,target_id) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "LOG" + (System.currentTimeMillis() % 100000000));
            ps.setString(2, adminId);
            ps.setString(3, action);
            ps.setString(4, targetId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getAllLogs() {

        StringBuilder sb = new StringBuilder();

        String sql = """
        	    SELECT log_id, admin_id, action, target_id, created_at
        	    FROM admin_logs
        	    ORDER BY created_at DESC
        	""";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

            	sb.append(
            		    rs.getString("log_id") + "|" +
            		    rs.getString("admin_id") + "|" +
            		    rs.getString("action") + "|" +
            		    rs.getString("target_id") + "|" +
            		    rs.getString("created_at") + "\n"
            		);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}