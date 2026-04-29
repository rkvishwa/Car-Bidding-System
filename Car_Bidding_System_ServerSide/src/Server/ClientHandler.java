package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import DB.*;
import Service.AuctionService;
import Service.UserService;

public class ClientHandler extends Thread {

    private static final List<PrintWriter> clients = new ArrayList<>();

    private Socket socket;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            out = new PrintWriter(socket.getOutputStream(), true);

            // register client
            synchronized (clients) {
                clients.add(out);
            }

            String request;

            while ((request = in.readLine()) != null) {

                System.out.println("Request: " + request);

                String[] parts = request.split(":");
                String cmd = parts[0];

                String response = "ERROR";

                try {

                    switch (cmd) {

                        case "LOGIN": {
                            UserService service = new UserService();
                            response = service.loginAndGetRole(parts[1], parts[2]);
                            break;
                        }

                        case "SIGNUP": {

                            if (parts.length < 6) {
                                response = "ERROR:INVALID_SIGNUP_FORMAT";
                                break;
                            }
                            UserService service = new UserService();

                            if (service.userExists(parts[1])) {
                                response = "FAILED:UserExists";
                            } else {
                                boolean ok = service.register(parts[1], parts[2], parts[3], parts[4],parts[5]);
                                response = ok ? "SUCCESS:Registered" : "FAILED:DBError";
                            }
                            break;
                        }
                        
                        case "GET_USERS": {
                            UserDAO dao = new UserDAO();
                            response = dao.getAllUsers();
                            break;
                        }

                        case "GET_PENDING_USERS": {
                            UserService service = new UserService();
                            response = service.getPendingUsers();
                            break;
                        }

                        case "APPROVE_USER": {
                            UserService service = new UserService();
                            boolean ok = service.approveUser(parts[1]);
                            if (ok) {
                                new AuditDAO().logAction(parts[2], "APPROVE_USER", parts[1]);
                                broadcast("USER_APPROVED:" + parts[1]);
                            }
                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }

                        case "FORGOT_PASSWORD": {
                            UserService service = new UserService();
                            response = service.forgotPassword(parts[1]);
                            break;
                        }

                        case "ADD_CAR": {
                        	String[] parts1 = request.split(":");
                            CarDAO dao = new CarDAO();

                            boolean ok = dao.addCar(
                                    parts1[1], parts1[2], parts1[3], parts1[4],
                                    Integer.parseInt(parts1[5]),
                                    Double.parseDouble(parts1[6]),
                                    parts1[7], parts1[8]
                            );

                            response = ok ? "SUCCESS:CarAdded" : "FAILED:CarNotAdded";
                            break;
                        }

                        case "GET_AUCTION": {
                            AuctionDAO dao = new AuctionDAO();
                            response = dao.getAuctionById(parts[1]);
                            break;
                        }
                        
                        case "GET_AUCTIONS": {
                            AuctionDAO dao = new AuctionDAO();
                            response = dao.getAllAuctions();
                            break;
                        }

                        case "GET_AUCTIONS_DETAILED": {
                            AuctionDAO dao = new AuctionDAO();
                            response = dao.getAllAuctionsDetailed();
                            break;
                        }

                        case "GET_BIDS": {
                            AuctionDAO dao = new AuctionDAO();
                            response = dao.getBidsByAuction(parts[1]);
                            break;
                        }

                        case "GET_MY_AUCTIONS": {
                            AuctionDAO dao = new AuctionDAO();
                            response = dao.getMyAuctions(parts[1]);
                            break;
                        }

                        case "GET_PENDING_AUCTIONS": {
                            AuctionDAO dao = new AuctionDAO();
                            response = dao.getPendingAuctions();
                            break;
                        }

                        case "APPROVE_AUCTION": {
                            AuctionDAO dao = new AuctionDAO();
                            String startTime = parts[4];
                            String adminId = parts.length > 5 ? parts[5] : parts[4]; 
                            if (parts.length > 5) {
                                adminId = parts[5];
                            } else {
                                // fallback if startTime is missing
                                startTime = "";
                                adminId = parts[4];
                            }
                            boolean ok = dao.approveAuction(parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3]), startTime);
                            if (ok) {
                                new AuditDAO().logAction(adminId, "APPROVE_AUCTION", parts[1]);
                                broadcast("AUCTION_APPROVED:" + parts[1]);
                            }
                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }

                        case "CONFIRM_WINNER": {
                            AuctionDAO dao = new AuctionDAO();
                            boolean ok = dao.confirmWinner(parts[1]);
                            if (ok) {
                                new AuditDAO().logAction(parts[2], "CONFIRM_WINNER", parts[1]);
                                broadcast("WINNER_CONFIRMED:" + parts[1]);
                            }
                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }

                        case "GET_AUCTION_END_TIME": {
                            AuctionDAO dao = new AuctionDAO();
                            response = dao.getAuctionEndTime(parts[1]);
                            break;
                        }

                        case "CREATE_AUCTION": {
                            AuctionDAO dao = new AuctionDAO();
                            double minBid = Double.parseDouble(parts[2]);
                            response = dao.createAuction(parts[1], minBid);

                            if (response.startsWith("SUCCESS")) {
                                broadcast("AUCTION_PENDING");
                            }
                            break;
                        }

                        case "BID": {
                            AuctionService service = new AuctionService();

                            boolean ok = service.placeBid(
                                    parts[1], parts[2], Double.parseDouble(parts[3])
                            );

                            if (ok) {
                                response = "SUCCESS:BidPlaced";
                                broadcast("NEW_BID:" + parts[1] + ":" + parts[3]);
                            } else {
                                response = "FAILED:InvalidBid";
                            }
                            break;
                        }
                        
                        case "GET_APPROVED_CARS": {
                            CarDAO dao = new CarDAO();
                            response = dao.getApprovedCars(parts[1]); // sellerId
                            break;
                        }
                        
                        case "GET_PENDING_CARS": {
                            CarDAO dao = new CarDAO();
                            response = dao.getPendingCars();
                            break;
                        }
                        
                        case "GET_MY_CARS": {
                            CarDAO dao = new CarDAO();
                            response = dao.getMyCars(parts[1]);
                            break;
                        }

                        case "GET_CAR_DETAILS": {
                            CarDAO dao = new CarDAO();
                            response = dao.getCarById(parts[1]);
                            break;
                        }
                        
                        case "DELETE_USER": {
                            UserDAO dao = new UserDAO();
                            boolean ok = dao.deleteUser(parts[1]);

                            if (ok) {
                                new AuditDAO().logAction(parts[2], "DELETE_USER" , parts[1]);
                                broadcast("USER_DELETED:" + parts[1]);
                            }

                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }

                        case "BAN_USER": {
                            UserDAO dao = new UserDAO();
                            boolean ok = dao.banUser(parts[1]);

                            if (ok) {
                            	new AuditDAO().logAction(
                            		    parts[2],
                            		    "BAN_USER",
                            		    parts[1]
                            		);
                                broadcast("USER_BANNED:" + parts[1]);
                            }

                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }

                        case "MAKE_ADMIN": {
                            UserDAO dao = new UserDAO();
                            boolean ok = dao.makeAdmin(parts[1]);

                            if (ok) {
                                new AuditDAO().logAction(parts[2], "MAKE_ADMIN" , parts[1]);
                                broadcast("ROLE_CHANGED:" + parts[1]);
                            }

                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }
                        
                        case "APPROVE_CAR": {
                            CarDAO dao = new CarDAO();
                            boolean ok = dao.approveCar(parts[1]);

                            if (ok) {
                                new AuditDAO().logAction(parts[2], "APPROVE_CAR" , parts[1]);
                                broadcast("CAR_APPROVED:" + parts[1]);

                                // Notify the seller
                                String carDetails = dao.getCarById(parts[1]);
                                if (!carDetails.equals("NOT_FOUND")) {
                                    String sellerId = carDetails.split("\\|")[1];
                                    new NotificationDAO().sendNotification(
                                        sellerId,
                                        "Your car has been approved by admin!",
                                        "CAR_APPROVED"
                                    );
                                }
                            }

                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }
                        
                        case "REJECT_CAR": {
                            CarDAO dao = new CarDAO();
                            boolean ok = dao.rejectCar(parts[1]);

                            if (ok) {
                                new AuditDAO().logAction(parts[2], "REJECT_CAR" , parts[1]);
                                broadcast("CAR_REJECTED:" + parts[1]);

                                // Notify the seller
                                String carDetails = dao.getCarById(parts[1]);
                                if (!carDetails.equals("NOT_FOUND")) {
                                    String sellerId = carDetails.split("\\|")[1];
                                    new NotificationDAO().sendNotification(
                                        sellerId,
                                        "Your car has been rejected by admin.",
                                        "CAR_REJECTED"
                                    );
                                }
                            }

                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }

                        case "SOFT_DELETE_CAR": {
                            CarDAO dao = new CarDAO();
                            boolean ok = dao.softDeleteCar(parts[1]);
                            if (ok) {
                                broadcast("CAR_DELETED_BY_SELLER:" + parts[1]);
                            }
                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }

                        case "DELETE_CAR_PERMANENT": {
                            CarDAO dao = new CarDAO();
                            boolean ok = dao.deleteCar(parts[1]);
                            if (ok) {
                                new AuditDAO().logAction(parts[2], "DELETE_CAR", parts[1]);
                                broadcast("CAR_DELETED:" + parts[1]);
                            }
                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }
                        
                        
                        case "GET_AUDIT_LOGS": {
                            AuditDAO dao = new AuditDAO();
                            response = dao.getAllLogs();
                            break;
                        }

                        // ==================== NOTIFICATIONS ====================
                        case "GET_NOTIFICATIONS": {
                            NotificationDAO dao = new NotificationDAO();
                            response = dao.getNotifications(parts[1]);
                            break;
                        }

                        case "MARK_NOTIFICATION_READ": {
                            NotificationDAO dao = new NotificationDAO();
                            boolean ok = dao.markRead(parts[1]);
                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }

                        case "MARK_ALL_NOTIFICATIONS_READ": {
                            NotificationDAO dao = new NotificationDAO();
                            boolean ok = dao.markAllRead(parts[1]);
                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }

                        // ==================== BID HISTORY ====================
                        case "GET_BID_HISTORY": {
                            BidDAO dao = new BidDAO();
                            response = dao.getBidHistory(parts[1]);
                            break;
                        }

                        // ==================== WATCHLIST ====================
                        case "ADD_WATCHLIST": {
                            WatchlistDAO dao = new WatchlistDAO();
                            boolean ok = dao.addToWatchlist(parts[1], parts[2]);

                            if (ok) {
                                new NotificationDAO().sendNotification(
                                    parts[1],
                                    "Auction " + parts[2] + " added to your watchlist!",
                                    "WATCHLIST"
                                );
                            }

                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }

                        case "REMOVE_WATCHLIST": {
                            WatchlistDAO dao = new WatchlistDAO();
                            boolean ok = dao.removeFromWatchlist(parts[1], parts[2]);
                            response = ok ? "SUCCESS" : "FAILED";
                            break;
                        }

                        case "GET_WATCHLIST": {
                            WatchlistDAO dao = new WatchlistDAO();
                            response = dao.getWatchlist(parts[1]);
                            break;
                        }
                        
                        case "CLOSE": {
                            AuctionDAO dao = new AuctionDAO();
                            dao.closeAuction(parts[1]);

                            String adminId = parts.length > 2 ? parts[2] : "SYSTEM";

                            new AuditDAO().logAction(adminId, "CLOSE_AUCTION" , parts[1]);

                            response = "SUCCESS:CLOSED";
                            broadcast("AUCTION_CLOSED:" + parts[1]);
                            break;
                        }

                        case "STOP_AUCTION": {
                            AuctionDAO dao = new AuctionDAO();
                            dao.closeAuction(parts[1]);

                            String adminId = parts.length > 2 ? parts[2] : "SYSTEM";
                            new AuditDAO().logAction(adminId, "STOP_AUCTION", parts[1]);

                            response = "SUCCESS:STOPPED";
                            broadcast("AUCTION_CLOSED:" + parts[1]);
                            break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    response = "ERROR:" + e.getMessage();
                }

                // Replace newlines with <<NL>> so multi-line responses are sent as a single line
                response = response.replace("\n", "<<NL>>");
                out.println(response);
                out.flush();
            }

        } catch (Exception e) {
            System.out.println("Client disconnected");
        } finally {

            try {
                synchronized (clients) {
                    clients.remove(out);
                }
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // REAL BROADCAST SYSTEM
    private void broadcast(String message) {

        synchronized (clients) {
            for (PrintWriter c : clients) {
                c.println(message);
                c.flush();
            }
        }

        System.out.println("[BROADCAST] " + message);
    }
}