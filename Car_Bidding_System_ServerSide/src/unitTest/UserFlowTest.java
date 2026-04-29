package unitTest;

import DB.UserDAO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserFlowTest {

    static UserDAO userDAO;
    static String testEmail = "testuser@gmail.com";
    static String password = "1234";
    static String userId;

    @BeforeAll
    static void setup() {
        userDAO = new UserDAO();
    }

    // TC-UL-01
    @Test
    @Order(1)
    void testRegisterUser() {
        boolean result = userDAO.registerUser(
                "Test User",
                testEmail,
                password,
                "BUYER",
                "091234567"
        );
        assertTrue(result);
    }

    // TC-UL-02
    @Test
    @Order(2)
    void testLoginBeforeApproval() {
        boolean result = userDAO.login(testEmail, password);
        assertFalse(result); // PENDING -> blocked
    }

    // TC-UL-03
    @Test
    @Order(3)
    void testAdminApproveUser() {

        String user = userDAO.getUserByEmail(testEmail);
        assertNotNull(user);

        userId = user.split("\\|")[0];

        boolean result = userDAO.approveUser(userId);
        assertTrue(result);
    }

    // TC-UL-04
    @Test
    @Order(4)
    void testLoginAfterApproval() {
        boolean result = userDAO.login(testEmail, password);
        assertTrue(result);
    }

    // TC-UL-05
    @Test
    @Order(5)
    void testBanUserLogin() {

        userDAO.banUser(userId);

        boolean result = userDAO.login(testEmail, password);
        assertFalse(result);
    }
}