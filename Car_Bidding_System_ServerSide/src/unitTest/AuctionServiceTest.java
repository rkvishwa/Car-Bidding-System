package unitTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuctionServiceTest {

    private boolean placeBid(double amount, double currentHighest) {
        if (amount <= currentHighest) {
            return false;
        }
        return true;
    }

    @Test
    public void testValidBid() {
        boolean result = placeBid(6000, 5000);
        assertTrue(result, "Bid should be accepted if higher");
    }

    @Test
    public void testEqualBid() {
        boolean result = placeBid(5000, 5000);
        assertFalse(result, "Bid should fail if equal");
    }

    @Test
    public void testLowerBid() {
        boolean result = placeBid(4000, 5000);
        assertFalse(result, "Bid should fail if lower");
    }

    @Test
    public void testNegativeBid() {
        boolean result = placeBid(-100, 5000);
        assertFalse(result, "Negative bid should fail");
    }
}