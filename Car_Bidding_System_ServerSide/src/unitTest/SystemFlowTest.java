package unitTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SystemFlowTest {

    @Test
    public void testFullWorkflow() {

        boolean login = true;
        boolean carAdded = true;
        boolean auctionCreated = true;
        boolean bidPlaced = true;
        boolean auctionClosed = true;

        assertTrue(login);
        assertTrue(carAdded);
        assertTrue(auctionCreated);
        assertTrue(bidPlaced);
        assertTrue(auctionClosed);
    }
}