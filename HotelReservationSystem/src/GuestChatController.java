import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class GuestChatController implements SessionController {

    @FXML private GuestSidebarController sidebarController;
    @FXML private VBox       messagesBox;
    @FXML private ScrollPane messagesScroll;
    @FXML private TextField  txtMessage;
    @FXML private Button     btnSend;
    @FXML private Label      lblStatus;

    private AppSession session;
    private ChatClient client;
    private String     receptionistUsername = null;
    private final List<String> pendingMessages = new ArrayList<>();

    @Override
    public void initSession(AppSession session) {
        this.session = session;

        if (sidebarController != null) {
            sidebarController.initSession(session);
            if (sidebarController.btnLiveChat != null){
                sidebarController.btnLiveChat.getStyleClass().add("sidebar-nav-btn-active");
            }
        }

        client = new ChatClient();
        boolean ok = client.connect(session.getCurrentGuest().getUsername(), "GUEST");
        if (!ok) {
            lblStatus.setText("Could not reach front desk");
            txtMessage.setDisable(true);
            btnSend.setDisable(true);
            return;
        }

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

    private void handleIncoming(ChatMessage msg) {
        switch (msg.getType()) {
            case CLAIM -> {
                receptionistUsername = msg.getFromUsername();
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
        for (String text : pendingMessages) {
            ChatMessage msg = new ChatMessage(
                    ChatMessage.Type.TEXT,
                    session.getCurrentGuest().getUsername(),
                    session.getCurrentGuest().getDisplayname(),
                    receptionistUsername, text);
            client.send(msg);
        }
        pendingMessages.clear();
    }

    @FXML
    private void handleSend() {
        String text = txtMessage.getText().trim();
        if (text.isEmpty()){
            return;
        }
        if (receptionistUsername == null) {
            pendingMessages.add(text);
            addMessage("You", text, true);
            addSystemMessage("(will be delivered when a receptionist joins)");
            txtMessage.clear();
            return;
        }
        ChatMessage msg = new ChatMessage(
                ChatMessage.Type.TEXT,
                session.getCurrentGuest().getUsername(),
                session.getCurrentGuest().getDisplayname(),
                receptionistUsername, text);
        client.send(msg);
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
        messagesBox.getChildren().add(l);
    }
}