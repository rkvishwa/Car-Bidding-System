package Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ClientManager {

    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    // REQUEST-RESPONSE SYNC
    private static final Object lock = new Object();
    private static String lastResponse;
    private static boolean hasResponse = false;

    // REAL-TIME OBSERVERS
    private static final List<Consumer<String>> listeners = new CopyOnWriteArrayList<>();

    public static void connect(String host, int port) throws Exception {

        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("Client connected!");

        startListener();
    }

    // ✅ multiple listeners supported
    public static void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }
    
    public static void removeListener(Consumer<String> listener) {
        listeners.remove(listener);
    }

    private static void startListener() {

        Thread t = new Thread(() -> {
            try {
                String msg;

                while ((msg = in.readLine()) != null) {

                    System.out.println("RECV: " + msg);

                    // 🔥 REAL-TIME EVENTS (all broadcast types from server)
                    if (msg.startsWith("NEW_BID") ||
                        msg.startsWith("AUCTION_CLOSED") ||
                        msg.startsWith("AUCTION_STARTED") ||
                        msg.startsWith("CAR_APPROVED") ||
                        msg.startsWith("CAR_REJECTED") ||
                        msg.startsWith("USER_BANNED") ||
                        msg.startsWith("USER_DELETED") ||
                        msg.startsWith("ROLE_CHANGED") ||
                        msg.startsWith("AUCTION_APPROVED") ||
                        msg.startsWith("WINNER_CONFIRMED") ||
                        msg.startsWith("AUCTION_PENDING") ||
                        msg.startsWith("USER_APPROVED") ||
                        msg.startsWith("CAR_DELETED") ||
                        msg.startsWith("CAR_DELETED_BY_SELLER")) {

                        for (Consumer<String> l : listeners) {
                            l.accept(msg);
                        }

                    } else {

                        synchronized (lock) {
                            lastResponse = msg;
                            hasResponse = true;
                            lock.notifyAll();
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Listener stopped: " + e.getMessage());
            }
        });

        t.setDaemon(true);
        t.start();
    }

    // ✅ SAFE SYNC REQUEST
    public static String send(String msg) throws Exception {

        synchronized (lock) {

            hasResponse = false;
            lastResponse = null;

            out.println(msg);
            out.flush();

            long start = System.currentTimeMillis();

            while (!hasResponse) {

                lock.wait(500);

                if (System.currentTimeMillis() - start > 15000) {
                    throw new RuntimeException("Server timeout (15s)");
                }
            }

            // Decode <<NL>> markers back to real newlines for multi-line responses
            return lastResponse.replace("<<NL>>", "\n");
        }
    }

    public static void close() throws Exception {
        if (socket != null) socket.close();
    }
}