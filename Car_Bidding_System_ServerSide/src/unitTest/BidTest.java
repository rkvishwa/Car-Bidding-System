package unitTest;

import DB.BidDAO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class BidTest {

    static BidDAO bidDAO = new BidDAO();

    static String auctionId = "AUC1777384321699";
    static String buyerId = "USR0004";

    // TC-BD-01
    @Test
    void validBid() {
        boolean result = bidDAO.placeBid(auctionId, buyerId, 1240000000);
        assertTrue(result);
    }

    // TC-BD-02
    @Test
    void lowBid() {
        boolean result = bidDAO.placeBid(auctionId, buyerId, 100);
        assertFalse(result);
    }

    // TC-BD-03
    @Test
    void equalBid() {
        double highest = bidDAO.getHighestBid(auctionId);
        boolean result = bidDAO.placeBid(auctionId, buyerId, highest);
        assertFalse(result);
    }

    // TC-BD-04
    @Test
    void wrongRole() {
        boolean result = bidDAO.placeBid(auctionId, "USR0008", 30000);//Seller Role
        assertFalse(result);
    }
}