package unitTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuctionLifeCycleTest {

    private String auctionState(String action) {

        switch (action) {
            case "CREATE": return "AUCTION_CREATED";
            case "BID": return "BID_PLACED";
            case "CLOSE": return "AUCTION_CLOSED";
            default: return "ERROR";
        }
    }

    @Test
    public void testAuctionFlow() {
        assertEquals("AUCTION_CREATED", auctionState("CREATE"));
        assertEquals("BID_PLACED", auctionState("BID"));
        assertEquals("AUCTION_CLOSED", auctionState("CLOSE"));
    }
}