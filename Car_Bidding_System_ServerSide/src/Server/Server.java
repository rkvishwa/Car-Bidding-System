package Server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server started...");

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
