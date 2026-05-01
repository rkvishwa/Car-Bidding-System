package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import DB.AuctionDAO;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server started on port 5000...");

            // Background task to auto-close expired auctions
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    new AuctionDAO().autoCloseExpiredAuctions();
                } catch (Exception e) {
                    System.err.println("Error in auto-close thread:");
                    e.printStackTrace();
                }
            }, 0, 10, TimeUnit.SECONDS);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");

                new ClientHandler(socket).start(); // multi-thread
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
