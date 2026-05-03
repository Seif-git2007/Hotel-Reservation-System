import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {

    public static String HOST = "localhost";
    public static final int PORT = ChatServer.PORT;

    private Socket             socket;
    private ObjectOutputStream out;
    private ObjectInputStream  in;
    private Thread             listenerThread;

    private Consumer<ChatMessage> onMessage;
    private boolean connected = false;

    public boolean connect(String username, String role) {
        try {
            socket = new Socket(HOST, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            ChatMessage register = new ChatMessage(
                    ChatMessage.Type.REGISTER, username, "", "SERVER", role);
            out.writeObject(register);
            out.flush();

            connected = true;
            startListening();
            return true;
        } catch (IOException e) {
            System.out.println("Could not connect to chat server: " + e.getMessage());
            return false;
        }
    }

    public void setOnMessage(Consumer<ChatMessage> handler) {
        this.onMessage = handler;
    }

    public synchronized void send(ChatMessage msg) {
        if (!connected){
            return;
        }
        try {
            out.writeObject(msg);
            out.flush();
            out.reset();
        } catch (IOException e) {
            System.out.println("Send failed: " + e.getMessage());
        }
    }

    private void startListening() {
        listenerThread = new Thread(() -> {
            try {
                while (connected) {
                    Object obj = in.readObject();
                    if (obj instanceof ChatMessage msg && onMessage != null) {
                        Platform.runLater(() -> onMessage.accept(msg));
                    }
                }
            } catch (Exception e) {
                System.out.println("Listener stopped: " + e.getMessage());
                connected = false;
            }
        }, "Chat-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void disconnect() {
        connected = false;
        try { if (socket != null){ socket.close(); } } catch (IOException ignored) {}
    }

    public boolean isConnected() {
        return connected;
    }
}
