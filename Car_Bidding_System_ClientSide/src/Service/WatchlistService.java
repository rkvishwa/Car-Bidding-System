package Service;

import Client.ClientManager;

public class WatchlistService {

    public static String addToWatchlist(String userId, String auctionId) throws Exception {
        return ClientManager.send("ADD_WATCHLIST:" + userId + ":" + auctionId);
    }

    public static String removeFromWatchlist(String userId, String auctionId) throws Exception {
        return ClientManager.send("REMOVE_WATCHLIST:" + userId + ":" + auctionId);
    }

    public static String getWatchlist(String userId) throws Exception {
        return ClientManager.send("GET_WATCHLIST:" + userId);
    }
}
