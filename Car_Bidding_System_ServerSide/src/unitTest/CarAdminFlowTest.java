package unitTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CarAdminFlowTest {

    private String approveCar(String carId, String role) {
        if (role.equals("ADMIN") && carId != null) {
            return "CAR_APPROVED";
        }
        return "FAILED";
    }

    @Test
    public void testAdminApproveCar() {
        assertEquals("CAR_APPROVED", approveCar("CAR1", "ADMIN"));
    }

    @Test
    public void testNonAdminReject() {
        assertEquals("FAILED", approveCar("CAR1", "SELLER"));
    }
}
