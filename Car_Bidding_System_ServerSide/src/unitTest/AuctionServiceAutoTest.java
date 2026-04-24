package unitTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuctionServiceAutoTest {

    @Test
    public void testAuctionCreation() {

        String result = createAuction("CAR1", 5000);

        assertTrue(result.contains("SUCCESS"));
    }

    // Simulated service method
    private String createAuction(String carId, double minBid) {
        if (carId != null && minBid > 0) {
            return "SUCCESS:AUCTION_CREATED";
        }
        return "FAILED";
    }
}
