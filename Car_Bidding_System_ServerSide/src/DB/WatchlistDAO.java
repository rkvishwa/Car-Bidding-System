package DB;

import java.sql.*;

public class WatchlistDAO {

    public boolean addToWatchlist(String userId, String auctionId) {

        // Check if already watching
        if (isWatching(userId, auctionId)) {
            return false;
        }

        String sql = "INSERT INTO watchlist (watchlist_id, user_id, auction_id, created_at) VALUES (?, ?, ?, NOW())";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "WL" + System.currentTimeMillis());
            ps.setString(2, userId);
            ps.setString(3, auctionId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFromWatchlist(String userId, String auctionId) {

        String sql = "DELETE FROM watchlist WHERE user_id=? AND auction_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, auctionId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isWatching(String userId, String auctionId) {

        String sql = "SELECT watchlist_id FROM watchlist WHERE user_id=? AND auction_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, auctionId);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getWatchlist(String userId) {

        StringBuilder sb = new StringBuilder();

        String sql = """
            SELECT 
                w.auction_id,
                c.title,
                c.brand,
                c.model,
                c.image,
                a.current_bid,
                a.status
            FROM watchlist w
            JOIN auctions a ON w.auction_id = a.auction_id
            JOIN car c ON a.car_id = c.car_id
            WHERE w.user_id = ?
            ORDER BY w.created_at DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sb.append(
                    rs.getString("auction_id") + "|" +
                    rs.getString("title") + "|" +
                    rs.getString("brand") + "|" +
                    rs.getString("model") + "|" +
                    rs.getString("image") + "|" +
                    rs.getDouble("current_bid") + "|" +
                    rs.getString("status") + "\n"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    // Get all users watching a specific auction (for notifications)
    public String getWatchers(String auctionId) {

        StringBuilder sb = new StringBuilder();

        String sql = "SELECT user_id FROM watchlist WHERE auction_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, auctionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sb.append(rs.getString("user_id")).append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
