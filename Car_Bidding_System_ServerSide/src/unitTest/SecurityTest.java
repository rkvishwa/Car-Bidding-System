package unitTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SecurityTest {

    private boolean validateInput(String input) {

        if (input == null || input.isEmpty()) return false;

        // simple injection detection
        if (input.contains("DROP") || input.contains("--")) return false;

        return true;
    }

    @Test
    public void testValidInput() {
        assertTrue(validateInput("BID:CAR1:USER1:5000"));
    }

    @Test
    public void testEmptyInput() {
        assertFalse(validateInput(""));
    }

    @Test
    public void testInjectionAttempt() {
        assertFalse(validateInput("DROP TABLE users"));
    }
}