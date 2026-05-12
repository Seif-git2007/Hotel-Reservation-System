import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class GuestChatController implements SessionController {

    @FXML private GuestSidebarController sidebarController;
    @FXML private VBox       messagesBox;
    @FXML private ScrollPane messagesScroll;
    @FXML private TextField  txtMessage;
    @FXML private Button     btnSend;
    @FXML private Label      lblStatus;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;

        if (sidebarController != null) {
            sidebarController.initSession(session);
            if (sidebarController.btnLiveChat != null) {
                sidebarController.btnLiveChat.getStyleClass().add("sidebar-nav-btn-active");
            }
        }

        restoreMessages();

        if (session.getChatClientLocal() != null && session.getChatClientLocal().isConnected()) {
            session.getChatClientLocal().setOnMessage(this::handleIncoming);
            if (session.getReceptionistUsername() != null) {
                lblStatus.setText("Connected with " + session.getReceptionistUsername());
            } else {
                lblStatus.setText("Waiting for a receptionist to respond...");
            }
            return;
        }

        ChatClient client = new ChatClient();
        boolean ok = client.connect(session.getCurrentGuest().getUsername(), "GUEST");
        if (!ok) {
            lblStatus.setText("Could not reach front desk");
            txtMessage.setDisable(true);
            btnSend.setDisable(true);
            return;
        }

        session.setChatClientLocal(client);
        lblStatus.setText("Waiting for a receptionist to respond...");
        client.setOnMessage(this::handleIncoming);

        ChatMessage request = new ChatMessage(
                ChatMessage.Type.GUEST_REQUEST,
                session.getCurrentGuest().getUsername(),
                session.getCurrentGuest().getDisplayname(),
                "FRONT_DESK", "");
        client.send(request);

        addSystemMessage("You are connected. A receptionist will be with you shortly.");
    }

    private void restoreMessages() {
        messagesBox.getChildren().clear();
        for (Node node : session.getGuestChatMessages()) {
            messagesBox.getChildren().add(node);
        }
        messagesScroll.layout();
        messagesScroll.setVvalue(1.0);
    }

    private void handleIncoming(ChatMessage msg) {
        switch (msg.getType()) {
            case CLAIM -> {
                session.setReceptionistUsername(msg.getFromUsername());
                lblStatus.setText("Connected with " + msg.getFromUsername());
                addSystemMessage(msg.getFromUsername() + " has joined the chat.");
                flushPendingMessages();
            }
            case TEXT -> {
                addMessage(msg.getFromUsername(), msg.getContent(), false);
            }
            default -> { }
        }
    }

    private void flushPendingMessages() {
        for (String text : session.getPendingMessages()) {
            ChatMessage msg = new ChatMessage(
                    ChatMessage.Type.TEXT,
                    session.getCurrentGuest().getUsername(),
                    session.getCurrentGuest().getDisplayname(),
                    session.getReceptionistUsername(), text);
            session.getChatClientLocal().send(msg);
        }
        session.getPendingMessages().clear();
    }

    @FXML
    private void handleSend() {
        String text = txtMessage.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        if (session.getReceptionistUsername() == null) {
            session.getPendingMessages().add(text);
            addMessage("You", text, true);
            addSystemMessage("(will be delivered when a receptionist joins)");
            txtMessage.clear();
            return;
        }
        ChatMessage msg = new ChatMessage(
                ChatMessage.Type.TEXT,
                session.getCurrentGuest().getUsername(),
                session.getCurrentGuest().getDisplayname(),
                session.getReceptionistUsername(), text);
        session.getChatClientLocal().send(msg);
        addMessage("You", text, true);
        txtMessage.clear();
    }

    private void addMessage(String sender, String content, boolean fromMe) {
        VBox bubble = new VBox(2);
        bubble.setMaxWidth(420);

        Label senderLabel = new Label(sender);
        senderLabel.getStyleClass().add(fromMe ? "chat-bubble-sender-me" : "chat-bubble-sender-them");

        Label body = new Label(content);
        body.setWrapText(true);
        body.getStyleClass().add(fromMe ? "chat-bubble-me" : "chat-bubble-them");

        bubble.getChildren().addAll(senderLabel, body);

        HBox row = new HBox(bubble);
        row.setAlignment(fromMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        session.getGuestChatMessages().add(row);
        messagesBox.getChildren().add(row);
        messagesScroll.layout();
        messagesScroll.setVvalue(1.0);
    }

    private void addSystemMessage(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("chat-system-message");
        l.setWrapText(true);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setAlignment(Pos.CENTER);
        session.getGuestChatMessages().add(l);
        messagesBox.getChildren().add(l);
    }
}