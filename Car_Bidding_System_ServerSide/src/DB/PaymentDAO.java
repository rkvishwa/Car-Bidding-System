package DB;

import java.sql.*;
import java.util.UUID;

import Util.IDGenerator;

public class PaymentDAO {

    public void makePayment(String auctionId, String buyerId, double amount) {

        String sql = "INSERT INTO payments VALUES (?, ?, ?, ?, 'CARD', 'COMPLETED', NOW())";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

        	String paymentId = IDGenerator.generateID("PAY", "payments", "payment_id");

            ps.setString(1, paymentId);
            ps.setString(2, auctionId);
            ps.setString(3, buyerId);
            ps.setDouble(4, amount);

            ps.executeUpdate();
            System.out.println("Payment successful!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
