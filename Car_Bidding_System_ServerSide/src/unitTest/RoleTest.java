package unitTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {

    public String checkAccess(String role, String action) {
        if (role.equals("ADMIN") && action.equals("DELETE_USER")) {
            return "ALLOWED";
        }
        if (role.equals("BUYER") && action.equals("PLACE_BID")) {
            return "ALLOWED";
        }
        return "DENIED";
    }

    @Test
    public void testAdminAccess() {
        assertEquals("ALLOWED", checkAccess("ADMIN", "DELETE_USER"));
    }

    @Test
    public void testBuyerRestrictedAccess() {
        assertEquals("DENIED", checkAccess("BUYER", "DELETE_USER"));
    }
}
