package DB;

import java.sql.*;

public class AuctionDAO {

	public void closeAuction(String auctionId) {

	    String getWinnerSQL = """
	        SELECT buyer_id, bid_amount
	        FROM bids
	        WHERE auction_id = ?
	        ORDER BY bid_amount DESC
	        LIMIT 1
	    """;

	    String updateAuctionSQL = """
	        UPDATE auctions 
	        SET status='CLOSED', winner_id=?, winner_confirmed=0 
	        WHERE auction_id=?
	    """;


	        try (Connection con = DBConnection.getConnection()) {

	            con.setAutoCommit(false);

	            String winnerId = null;
	            double winAmount = 0;

	            try (PreparedStatement ps1 = con.prepareStatement(getWinnerSQL)) {
	                ps1.setString(1, auctionId);
	                ResultSet rs = ps1.executeQuery();

	                if (rs.next()) {
	                    winnerId = rs.getString("buyer_id");
	                    winAmount = rs.getDouble("bid_amount");
	                }
	            }

	            if (winnerId != null) {
	                try (PreparedStatement ps2 = con.prepareStatement(updateAuctionSQL)) {
	                    ps2.setString(1, winnerId);
	                    ps2.setString(2, auctionId);
	                    ps2.executeUpdate();
	                }

	                // Notify winner
	                new NotificationDAO().sendNotification(
	                    winnerId,
	                    "Congratulations! You won the auction " + auctionId + " with bid " + winAmount,
	                    "WIN"
	                );

	                // Notify losers
	                notifyLosers(con, auctionId, winnerId);

	                // Notify seller
	                notifySeller(con, auctionId, winnerId, winAmount);

	            } else {
	                // No bids - just close
	                String closeSQL = "UPDATE auctions SET status='CLOSED' WHERE auction_id=?";
	                try (PreparedStatement ps = con.prepareStatement(closeSQL)) {
	                    ps.setString(1, auctionId);
	                    ps.executeUpdate();
	                }
	            }

	            con.commit();

	        } catch (Exception e) {
	        	
	            e.printStackTrace();
	        }
	}

	private void notifyLosers(Connection con, String auctionId, String winnerId) {

	    String sql = "SELECT DISTINCT buyer_id FROM bids WHERE auction_id=? AND buyer_id != ?";

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, auctionId);
	        ps.setString(2, winnerId);
	        ResultSet rs = ps.executeQuery();

	        NotificationDAO notifDAO = new NotificationDAO();

	        while (rs.next()) {
	            notifDAO.sendNotification(
	                rs.getString("buyer_id"),
	                "You lost the auction " + auctionId + ". Better luck next time!",
	                "LOSE"
	            );
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private void notifySeller(Connection con, String auctionId, String winnerId, double winAmount) {

	    String sql = """
	        SELECT c.seller_id, c.title 
	        FROM auctions a 
	        JOIN car c ON a.car_id = c.car_id 
	        WHERE a.auction_id = ?
	    """;

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, auctionId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            new NotificationDAO().sendNotification(
	                rs.getString("seller_id"),
	                "Your car '" + rs.getString("title") + "' sold for " + winAmount + "!",
	                "SOLD"
	            );
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

    public String getAllAuctions() {

        StringBuilder sb = new StringBuilder();

        String sql = """
        	    SELECT 
        	        a.auction_id, 
        	        c.title, 
        	        c.brand,
        	        c.model,
        	        c.image, 
        	        a.current_bid, 
        	        a.status
        	    FROM auctions a
        	    JOIN car c ON a.car_id = c.car_id
        	""";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

            	sb.append(
            		    rs.getString("auction_id") + "|" +
            		    rs.getString("title") + "|" +
            		    rs.getString("brand") + "|" +
            		    rs.getString("model") + "|" +
            		    rs.getDouble("current_bid") + "|" +
            		    rs.getString("status") + "|" +
            		    rs.getString("image") + "\n"
            		);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    
    public String getAuctionById(String auctionId) {
    	String sql = """
    		 SELECT a.auction_id, a.car_id, a.status, a.winner_id, a.current_bid,
			       c.title, c.brand, c.model, c.image
			FROM auctions a
			JOIN car c ON a.car_id = c.car_id
			WHERE a.auction_id=?
    		""";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, auctionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
            	return rs.getString("auction_id") + "|" +
            		       rs.getString("car_id") + "|" +
            		       rs.getString("title") + "|" +
            		       rs.getString("brand") + "|" +
            		       rs.getString("model") + "|" +
            		       rs.getDouble("current_bid") + "|" +
            		       rs.getString("image") + "|" +
            		       rs.getString("status");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "NOT_FOUND";
    }
    
    public String getBidsByAuction(String auctionId) {

        StringBuilder sb = new StringBuilder();

        String sql = """
            SELECT buyer_id, bid_amount, bid_time
            FROM bids
            WHERE auction_id=?
            ORDER BY bid_amount DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, auctionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
            	sb.append(
            		    rs.getString("buyer_id") + "|" +
            		    rs.getDouble("bid_amount") + "|" +
            		    rs.getTimestamp("bid_time") + "\n"
            		);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    
    public String createAuction(String carId, double minBid) {
    

        String sql = """
            INSERT INTO auctions 
            (auction_id, car_id, min_bid, seller_min_bid, current_bid, status, duration_minutes, start_time)
            VALUES (?, ?, ?, ?, ?, 'PENDING', 0, NULL)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String auctionId = "AUC" + System.currentTimeMillis();

            ps.setString(1, auctionId);
            ps.setString(2, carId);
            ps.setDouble(3, minBid);  // min_bid
            ps.setDouble(4, minBid);  // seller_min_bid
            ps.setDouble(5, minBid);  // current_bid

            ps.executeUpdate();
            
            return "SUCCESS:AuctionCreated:" + auctionId;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "FAILED:AuctionError";
}


//    public boolean approveAuction(String auctionId, double minBid, int duration) {
//        String sql = """
//            UPDATE auctions 
//            SET min_bid=?, current_bid=?, status='ACTIVE', 
//                duration_minutes=?, end_time=DATE_ADD(NOW(), INTERVAL ? MINUTE)
//            WHERE auction_id=?
//        """;
//
//        try (Connection con = DBConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//
//            ps.setDouble(1, minBid);
//            ps.setDouble(2, minBid);
//            ps.setInt(3, duration);
//            ps.setInt(4, duration);
//            ps.setString(5, auctionId);
//
//            int affected = ps.executeUpdate();
//            if (affected > 0) {
//                // Start timer
//                new Timer().schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        new AuctionDAO().closeAuction(auctionId);
//                    }
//                }, (long) duration * 60 * 1000);
//                return true;
//            }
//        } catch (Exception e) { e.printStackTrace(); }
//        return false;
//    }
    
    public boolean approveAuction(String auctionId, double adminMinBid, int duration) {

        String getMinSql = "SELECT min_bid FROM auctions WHERE auction_id=?";
        
        String updateSql = """
            UPDATE auctions 
            SET min_bid=?, current_bid=?, status='ACTIVE',
                start_time=NOW(),
                duration_minutes=?, 
                end_time=DATE_ADD(NOW(), INTERVAL ? MINUTE)
            WHERE auction_id=?
        """;

        try (Connection con = DBConnection.getConnection()) {

            double sellerMinBid = 0;

            // 🔹 Get seller's min bid
            try (PreparedStatement ps = con.prepareStatement(getMinSql)) {
                ps.setString(1, auctionId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    sellerMinBid = rs.getDouble("min_bid");
                }
            }

            // ❌ Prevent decreasing
            if (adminMinBid < sellerMinBid) {
                return false;
            }

            // ✅ Use higher value
            double finalMinBid = Math.max(adminMinBid, sellerMinBid);

            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setDouble(1, finalMinBid);
                ps.setDouble(2, finalMinBid);
                ps.setInt(3, duration);
                ps.setInt(4, duration);
                ps.setString(5, auctionId);

                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean confirmWinner(String auctionId) {
        String sql = "UPDATE auctions SET winner_confirmed=1 WHERE auction_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, auctionId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public String getPendingAuctions() {
        StringBuilder sb = new StringBuilder();
        String sql = """
            SELECT a.auction_id, c.title, c.brand, c.model, c.image, c.seller_id
            FROM auctions a JOIN car c ON a.car_id = c.car_id
            WHERE a.status='PENDING'
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sb.append(rs.getString("auction_id")).append("|")
                  .append(rs.getString("title")).append("|")
                  .append(rs.getString("brand")).append("|")
                  .append(rs.getString("model")).append("|")
                  .append(rs.getString("image")).append("|")
                  .append(rs.getString("seller_id")).append("\n");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return sb.toString();
    }

    public String getAuctionEndTime(String auctionId) {
        String sql = "SELECT end_time FROM auctions WHERE auction_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, auctionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("end_time");
                return ts != null ? ts.toString() : "NONE";
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "NONE";
    }
	
	public String getMyAuctions(String sellerId) {

	    StringBuilder sb = new StringBuilder();

	    String sql = """
	    	    SELECT 
	    	        a.auction_id,
	    	        c.title,
	    	        c.brand,
	    	        c.model,
	    	        c.image,
	    	        a.current_bid,
	    	        a.min_bid,
	    	        a.status,
	    	        a.winner_id
	    	    FROM auctions a
	    	    JOIN car c ON a.car_id = c.car_id
	    	    WHERE c.seller_id = ?
	    	    ORDER BY a.status ASC
	    	""";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, sellerId);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {

	        	sb.append(
	        		    rs.getString("auction_id") + "|" +
	        		    rs.getString("title") + "|" +
	        		    rs.getString("brand") + "|" +
	        		    rs.getString("model") + "|" +
	        		    rs.getString("image") + "|" +
	        		    rs.getDouble("current_bid") + "|" +
	        		    rs.getDouble("min_bid") + "|" +
	        		    rs.getString("status") + "|" +
	        		    (rs.getString("winner_id") != null ? rs.getString("winner_id") : "N/A") + "\n"
	        		);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return sb.toString();
	}

	// Get all auctions with full details for admin
	public String getAllAuctionsDetailed() {

	    StringBuilder sb = new StringBuilder();

	    String sql = """
	        SELECT 
	            a.auction_id,
	            c.title,
	            c.brand,
	            c.model,
	            c.image,
	            a.current_bid,
	            a.min_bid,
	            a.status,
	            a.winner_id,
	            c.seller_id
	        FROM auctions a
	        JOIN car c ON a.car_id = c.car_id
	        ORDER BY 
	            CASE a.status WHEN 'ACTIVE' THEN 0 ELSE 1 END,
	            a.auction_id DESC
	    """;

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            sb.append(
	                rs.getString("auction_id") + "|" +
	                rs.getString("title") + "|" +
	                rs.getString("brand") + "|" +
	                rs.getString("model") + "|" +
	                rs.getString("image") + "|" +
	                rs.getDouble("current_bid") + "|" +
	                rs.getDouble("min_bid") + "|" +
	                rs.getString("status") + "|" +
	                (rs.getString("winner_id") != null ? rs.getString("winner_id") : "N/A") + "|" +
	                rs.getString("seller_id") + "|" +
	                rs.getInt("winner_confirmed") + "\n"
	            );
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return sb.toString();
	}
}
