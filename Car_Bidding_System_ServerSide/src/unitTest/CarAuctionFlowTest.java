package unitTest;

import DB.CarDAO;
import DB.AuctionDAO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CarAuctionFlowTest {

    static CarDAO carDAO;
    static AuctionDAO auctionDAO;

    static String carId;
    static String auctionId;

    @BeforeAll
    static void setup() {
        carDAO = new CarDAO();
        auctionDAO = new AuctionDAO();
    }

    // TC-CA-01
    @Test
    @Order(1)
    void addCar() {
        boolean result = carDAO.addCar(
                "USR0008",
                "BMW M5",
                "BMW",
                "M5",
                2022,
                500000000,
                "Luxury car",
                "https://www.google.com/car.jpg"
        );
        assertTrue(result);
        
        String pendingCars = carDAO.getPendingCars();
        String firstRow = pendingCars.split("\n")[0];
        carId = firstRow.split("\\|")[0]; 

        assertNotNull(carId);
    }

    // TC-CA-02
    @Test
    @Order(2)
    void approveCar() {
        boolean result = carDAO.approveCar(carId);
        assertTrue(result);
    }

    // TC-CA-03
    @Test
    @Order(3)
    void createAuction() {
        String result = auctionDAO.createAuction(carId, 10000);
        assertTrue(result.startsWith("SUCCESS"));

        auctionId = result.split(":")[2];
    }

    // TC-CA-04
    @Test
    @Order(4)
    void approveAuction() {
        boolean result = auctionDAO.approveAuction(auctionId, 12000, 10,null);
        assertTrue(result);
    }

    // TC-CA-05
    @Test
    @Order(5)
    void activateAuctionCheck() {
        String auction = auctionDAO.getAuctionById(auctionId);
        assertTrue(auction.contains("ACTIVE"));
    }

    // TC-CA-06
    @Test
    @Order(6)
    void preventInvalidAuctionAction() {
        String invalid = auctionDAO.getAuctionById("WRONG_ID");
        assertEquals("NOT_FOUND", invalid);
    }
}