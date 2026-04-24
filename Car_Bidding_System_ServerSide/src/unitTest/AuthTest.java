package unitTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {

    public String login(String email, String password) {
        if (email.equals("admin@test.com") && password.equals("1234")) {
            return "SUCCESS";
        }
        return "FAILED";
    }

    @Test
    public void testValidLogin() {
        assertEquals("SUCCESS", login("admin@test.com", "1234"));
    }

    @Test
    public void testInvalidLogin() {
        assertEquals("FAILED", login("wrong@test.com", "0000"));
    }
}
