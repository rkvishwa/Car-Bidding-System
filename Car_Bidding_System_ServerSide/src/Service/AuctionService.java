package Service;

import DB.AuctionDAO;
import DB.BidDAO;

public class AuctionService {

    private BidDAO bidDAO = new BidDAO();
    private AuctionDAO auctionDAO = new AuctionDAO();

    public boolean placeBid(String auctionId, String buyerId, double amount) {
        return bidDAO.placeBid(auctionId, buyerId, amount);
    }

    public void closeAuction(String auctionId) {
        auctionDAO.closeAuction(auctionId);
    }
    

}
