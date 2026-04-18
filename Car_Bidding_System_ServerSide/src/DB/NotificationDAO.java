package DB;

import java.sql.*;

public class NotificationDAO {

    public void sendNotification(String userId, String message, String type) {

        String sql = "INSERT INTO notifications VALUES (?, ?, ?, ?, 'UNREAD', NOW())";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String id = java.util.UUID.randomUUID().toString();

            ps.setString(1, id);
            ps.setString(2, userId);
            ps.setString(3, message);
            ps.setString(4, type);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNotifications(String userId) {

        StringBuilder sb = new StringBuilder();

        String sql = """
            SELECT notification_id, message, type, status, created_at
            FROM notifications
            WHERE user_id = ?
            ORDER BY created_at DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sb.append(
                    rs.getString("notification_id") + "|" +
                    rs.getString("message") + "|" +
                    rs.getString("type") + "|" +
                    rs.getString("status") + "|" +
                    rs.getString("created_at") + "\n"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public boolean markRead(String notifId) {

        String sql = "UPDATE notifications SET status='READ' WHERE notification_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, notifId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAllRead(String userId) {

        String sql = "UPDATE notifications SET status='READ' WHERE user_id=? AND status='UNREAD'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
