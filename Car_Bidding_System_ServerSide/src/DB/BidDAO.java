package DB;

import java.sql.*;
import java.util.UUID;

import Util.IDGenerator;

public class BidDAO {

    // Place Bid
	public boolean placeBid(String auctionId, String buyerId, double amount) {

	    // Check auction exists
	    if (!isAuctionValid(auctionId)) {
	        System.out.println("Invalid Auction ID!");
	        return false;
	    }

	    double currentHighest = getHighestBid(auctionId);

	    System.out.println("Current Highest: " + currentHighest);
	    System.out.println("Your Bid: " + amount);

	    if (amount <= currentHighest) {
	        System.out.println("Bid must be higher than current highest!");
	        return false;
	    }

	    String sql = "INSERT INTO bids VALUES (?, ?, ?, ?, NOW())";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	    	String bidId = IDGenerator.generateID("BID", "bids", "bid_id");

	        ps.setString(1, bidId);
	        ps.setString(2, auctionId);
	        ps.setString(3, buyerId);
	        ps.setDouble(4, amount);

	        ps.executeUpdate();
	        updateAuctionBid(auctionId, amount);
	        System.out.println("Bid placed successfully!");

	        // send notification to bidder
	        new NotificationDAO().sendNotification(
	            buyerId,
	            "You placed a bid of " + amount,
	            "BID"
	        );

	        // notify watchlist users about new bid
	        notifyWatchers(auctionId, buyerId, amount);

	        return true;

	    } catch (Exception e) {
	        System.out.println("SQL ERROR:");
	        e.printStackTrace();
	    }

	    return false;
	}
	
	
	private void updateAuctionBid(String auctionId, double amount) {

	    String sql = "UPDATE auctions SET current_bid=? WHERE auction_id=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setDouble(1, amount);
	        ps.setString(2, auctionId);

	        ps.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
    
    private boolean isAuctionValid(String auctionId) {
        String sql = "SELECT * FROM auctions WHERE auction_id=? AND status='ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, auctionId);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Get Highest Bid
    public double getHighestBid(String auctionId) {
        String sql = "SELECT MAX(bid_amount) FROM bids WHERE auction_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, auctionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double value = rs.getDouble(1);
                if (rs.wasNull()) {
                    return 0;
                }
                return value;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Get bid history for a specific buyer
    public String getBidHistory(String buyerId) {

        StringBuilder sb = new StringBuilder();

        String sql = """
            SELECT 
                b.bid_id,
                b.auction_id,
                c.title,
                c.brand,
                c.model,
                c.image,
                b.bid_amount,
                b.bid_time,
                a.status AS auction_status,
                a.winner_id,
                a.current_bid
            FROM bids b
            JOIN auctions a ON b.auction_id = a.auction_id
            JOIN car c ON a.car_id = c.car_id
            WHERE b.buyer_id = ?
            ORDER BY b.bid_time DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, buyerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String auctionStatus = rs.getString("auction_status");
                String winnerId = rs.getString("winner_id");

                // Determine bid result
                String result;
                if (auctionStatus.equals("ACTIVE")) {
                    result = "ACTIVE";
                } else if (buyerId.equals(winnerId)) {
                    result = "WON";
                } else {
                    result = "LOST";
                }

                sb.append(
                    rs.getString("bid_id") + "|" +
                    rs.getString("auction_id") + "|" +
                    rs.getString("title") + "|" +
                    rs.getString("brand") + "|" +
                    rs.getString("model") + "|" +
                    rs.getString("image") + "|" +
                    rs.getDouble("bid_amount") + "|" +
                    rs.getString("bid_time") + "|" +
                    result + "|" +
                    rs.getDouble("current_bid") + "\n"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    // Notify watchlist users about a new bid
    private void notifyWatchers(String auctionId, String bidderId, double amount) {

        WatchlistDAO watchlistDAO = new WatchlistDAO();
        String watchers = watchlistDAO.getWatchers(auctionId);
        NotificationDAO notifDAO = new NotificationDAO();

        if (watchers == null || watchers.isEmpty()) return;

        for (String userId : watchers.split("\n")) {
            userId = userId.trim();
            if (userId.isEmpty() || userId.equals(bidderId)) continue;

            notifDAO.sendNotification(
                userId,
                "New bid of " + amount + " on an auction you're watching!",
                "WATCHLIST_BID"
            );
        }
    }
}
