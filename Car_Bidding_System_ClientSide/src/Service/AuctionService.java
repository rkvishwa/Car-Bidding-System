package Service;

import Client.ClientManager;

public class AuctionService {

    public static String getAuctions() throws Exception {
        return ClientManager.send("GET_AUCTIONS");
    }

    public static String getAuctionsDetailed() throws Exception {
        return ClientManager.send("GET_AUCTIONS_DETAILED");
    }

    public static String getAuction(String id) throws Exception {
        return ClientManager.send("GET_AUCTION:" + id);
    }

    public static String placeBid(String auctionId, String userId, double amount) throws Exception {
        return ClientManager.send("BID:" + auctionId + ":" + userId + ":" + amount);
    }

    public static String getBids(String auctionId) throws Exception {
        return ClientManager.send("GET_BIDS:" + auctionId);
    }

    public static String createAuction(String carId, String bid) throws Exception {
        return ClientManager.send("CREATE_AUCTION:" + carId + ":" + bid);
    }

    public static String closeAuction(String id, String adminId) throws Exception {
        return ClientManager.send("CLOSE:" + id + ":" + adminId);
    }

    public static String stopAuction(String id, String adminId) throws Exception {
        return ClientManager.send("STOP_AUCTION:" + id + ":" + adminId);
    }

    public static String getMyAuctions(String userId) throws Exception {
        return ClientManager.send("GET_MY_AUCTIONS:" + userId);
    }

    public static String getPendingAuctions() throws Exception {
        return ClientManager.send("GET_PENDING_AUCTIONS");
    }

    public static String approveAuction(String id, String minBid, String duration, String adminId) throws Exception {
        return ClientManager.send("APPROVE_AUCTION:" + id + ":" + minBid + ":" + duration + ":" + adminId);
    }

    public static String confirmWinner(String id, String adminId) throws Exception {
        return ClientManager.send("CONFIRM_WINNER:" + id + ":" + adminId);
    }

    public static String getAuctionEndTime(String id) throws Exception {
        return ClientManager.send("GET_AUCTION_END_TIME:" + id);
    }
}