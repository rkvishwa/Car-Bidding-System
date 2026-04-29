package unitTest;

import DB.AuctionDAO;
import DB.CarDAO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionCloseTest {

    static AuctionDAO auctionDAO = new AuctionDAO();
    static CarDAO carDAO = new CarDAO();
    static String auctionId = "AUC1777384321699";//Active Auction

    // TC-CL-01
    @Test
    void closeAuction() {
        auctionDAO.closeAuction(auctionId);

        String result = auctionDAO.getAuctionById(auctionId);
        assertTrue(result.contains("CLOSED"));
    }

    // TC-CL-02
    @Test
    void winnerSelected() {
        auctionDAO.closeAuction(auctionId);

        String result = auctionDAO.getAuctionById(auctionId);
        assertFalse(result.contains("null"));
    }

    // TC-CL-03
    @Test
    void closeWithoutBids() {

        // 1. Insert car
        boolean carInserted = carDAO.addCar(
                "USR0008",
                "Test Car",
                "Toyota",
                "Camry",
                2020,
                10000000,
                "Test",
                "img.jpg"
        );

        assertTrue(carInserted);
        String pendingCars = carDAO.getPendingCars();
        String carId = pendingCars.split("\\|")[0]; 
        assertNotNull(carId);
        assertTrue(carDAO.approveCar(carId));
        String result = auctionDAO.createAuction(carId, 5000);
        assertTrue(result.startsWith("SUCCESS"));
        String auctionId = result.split(":")[2];
        auctionDAO.closeAuction(auctionId);
        String auction = auctionDAO.getAuctionById(auctionId);
        assertTrue(auction.contains("CLOSED"));
    }

    // TC-CL-04
    @Test
    void verifyFinalBidStored() {
        String result = auctionDAO.getAuctionById(auctionId);
        assertNotNull(result);
    }
    
    
}