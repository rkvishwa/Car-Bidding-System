package Service;

import Client.ClientManager;

public class BidHistoryService {

    public static String getBidHistory(String userId) throws Exception {
        return ClientManager.send("GET_BID_HISTORY:" + userId);
    }
}
