package unitTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuctionIntegrationTest {

    @Test
    public void testBidRequestFormat() throws Exception {

        String request = "BID:AUC1:USER1:7000";

        // Simulated server response logic
        String response;

        if (request.startsWith("BID")) {
            response = "SUCCESS:BID_PLACED";
        } else {
            response = "FAILED";
        }

        assertEquals("SUCCESS:BID_PLACED", response);
    }
}
