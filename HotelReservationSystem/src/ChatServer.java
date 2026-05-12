import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    public static final int PORT = 9090;

    private static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private static final Map<String, String>         claims  = new ConcurrentHashMap<>();
    private static final Set<String>                 waitingGuests = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        start();
        System.out.println("Chat server running on port " + PORT);
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignored) {}
    }

    public static void start() {
        Thread acceptor = new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("ChatServer listening on port " + PORT);
                while (true) {
                    Socket socket = server.accept();
                    ClientHandler handler = new ClientHandler(socket);
                    Thread t = new Thread(handler, "Client-Handler");
                    t.setDaemon(true);
                    t.start();
                }
            } catch (IOException e) {
                System.out.println("Chat server stopped: " + e.getMessage());
            }
        }, "Chat-Acceptor");
        acceptor.setDaemon(true);
        acceptor.start();
    }

    private static void register(String username, ClientHandler handler) {
        clients.put(username, handler);
        System.out.println("Client registered: " + username);
    }

    private static void unregister(String username) {
        if (username == null){
            return;
        }
        clients.remove(username);
        waitingGuests.remove(username);
        String receptionist = claims.remove(username);
        if (receptionist != null) {
            ChatMessage notice = new ChatMessage(
                    ChatMessage.Type.GUEST_LEFT, username, "", receptionist, "");
            sendTo(receptionist, notice);
        }
        broadcastGuestListToReceptionists();
        System.out.println("Client disconnected: " + username);
    }

    private static void route(ChatMessage msg) {
        switch (msg.getType()) {
            case GUEST_REQUEST -> {
                waitingGuests.add(msg.getFromUsername());
                broadcastGuestListToReceptionists();
            }
            case CLAIM -> {
                String guest = msg.getToUsername();
                String receptionist = msg.getFromUsername();
                if (claims.containsKey(guest)){
                    return;
                }
                claims.put(guest, receptionist);
                waitingGuests.remove(guest);
                sendTo(guest, msg);
                broadcastGuestListToReceptionists();
            }
            case UNCLAIM -> {
                String guest = msg.getToUsername();
                claims.remove(guest);
                waitingGuests.add(guest);
                broadcastGuestListToReceptionists();
            }
            case TEXT -> {
                sendTo(msg.getToUsername(), msg);
            }
            default -> { }
        }
    }

    private static void sendTo(String username, ChatMessage msg) {
        ClientHandler handler = clients.get(username);
        if (handler != null){
            handler.send(msg);
        }
    }

    private static void broadcastGuestListToReceptionists() {
        StringBuilder sb = new StringBuilder();
        for (String g : waitingGuests){
            sb.append(g).append("|WAITING;");
        }
        for (Map.Entry<String, String> e : claims.entrySet()){
            sb.append(e.getKey()).append("|").append(e.getValue()).append(";");
        }

        for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
            if (e.getValue().isReceptionist) {
                ChatMessage update = new ChatMessage(
                        ChatMessage.Type.GUEST_LIST_UPDATE, "SERVER", "", e.getKey(), sb.toString());
                e.getValue().send(update);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private ObjectOutputStream out;
        private String username;
        private boolean isReceptionist = false;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    Object obj = in.readObject();
                    if (!(obj instanceof ChatMessage msg)){
                        continue;
                    }
                    if (msg.getType() == ChatMessage.Type.REGISTER) {
                        this.username = msg.getFromUsername();
                        this.isReceptionist = "RECEPTIONIST".equals(msg.getContent());
                        register(username, this);
                        if (isReceptionist){
                            broadcastGuestListToReceptionists();
                        }
                    } else {
                        route(msg);
                    }
                }
            } catch (Exception e) {
                System.out.println("Client " + username + " disconnected: " + e.getMessage());
            } finally {
                unregister(username);
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        synchronized void send(ChatMessage msg) {
            try {
                out.writeObject(msg);
                out.flush();
                out.reset();
            } catch (IOException e) {
                System.out.println("Failed to send to " + username);
            }
        }
    }
}
