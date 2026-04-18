package Client;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class Client implements AutoCloseable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String host, int port) throws IOException {

        socket = new Socket(host, port);

        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        out = new PrintWriter(socket.getOutputStream(), true);
    }

    // =========================
    // SEND REQUEST (UI → SERVER)
    // =========================
    public String sendRequest(String request) {

        try {
            out.println(request);
            return in.readLine(); // response from server

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // REAL-TIME LISTENER (SERVER → UI)
    // =========================
    public void listen(Consumer<String> callback) {

        Thread listenerThread = new Thread(() -> {

            try {

                String response;

                while ((response = in.readLine()) != null) {

                    String msg = response;

                    callback.accept(msg); // send to UI/controller
                }

            } catch (Exception e) {
                System.out.println("Listener stopped: " + e.getMessage());
            }

        });

        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    // =========================
    // CLOSE CONNECTION
    // =========================
    @Override
    public void close() {

        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}