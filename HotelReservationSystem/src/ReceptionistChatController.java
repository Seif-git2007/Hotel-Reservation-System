import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Map;

public class ReceptionistChatController implements SessionController {

    @FXML private ReceptionistSidebarController sidebarController;
    @FXML private VBox       messagesBox;
    @FXML private ScrollPane messagesScroll;
    @FXML private TextField  txtMessage;
    @FXML private Button     btnSend;
    @FXML private Label      lblActiveGuest, lblStatus;
    @FXML private ListView<String> guestsList;

    private AppSession session;
    private ChatClient client;
    private String     activeGuest = null;

    private final Map<String, VBox> conversationsCache = new HashMap<>();

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
        boolean ok = client.connect(session.getCurrentReceptionist().getUsername(), "RECEPTIONIST");
        if (!ok) {
            lblStatus.setText("Could not connect to chat server");
            txtMessage.setDisable(true);
            btnSend.setDisable(true);
            return;
        }
        lblStatus.setText("Connected. Pick a guest to start chatting.");
        client.setOnMessage(this::handleIncoming);

        guestsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null){
                return;
            }
            String guestUsername = parseUsername(newVal);
            if (guestUsername == null){
                return;
            }
            String state = parseState(newVal);
            if ("WAITING".equals(state)) {
                ChatMessage claim = new ChatMessage(
                        ChatMessage.Type.CLAIM,
                        session.getCurrentReceptionist().getUsername(), "",
                        guestUsername, "");
                client.send(claim);
            }
            if (state.equals("WAITING") || state.equals(session.getCurrentReceptionist().getUsername())) {
                openConversation(guestUsername);
            }
        });
    }

    private String parseUsername(String listItem) {
        int sep = listItem.indexOf("  ·  ");
        if (sep < 0){
            return listItem;
        }
        return listItem.substring(0, sep);
    }

    private String parseState(String listItem) {
        int sep = listItem.indexOf("  ·  ");
        if (sep < 0){
            return "";
        }
        return listItem.substring(sep + 5);
    }

    private void handleIncoming(ChatMessage msg) {
        switch (msg.getType()) {
            case GUEST_LIST_UPDATE -> {
                refreshGuestList(msg.getContent());
            }
            case TEXT -> {
                String guest = msg.getFromUsername();
                HBox row = buildMessageRow(msg.getFromUsername(), msg.getContent(), false);
                getConversation(guest).getChildren().add(row);
                if (guest.equals(activeGuest)) {
                    HBox liveRow = buildMessageRow(msg.getFromUsername(), msg.getContent(), false);
                    messagesBox.getChildren().add(liveRow);
                    messagesScroll.layout();
                    messagesScroll.setVvalue(1.0);
                }
            }
            case GUEST_LEFT -> {
                String guest = msg.getFromUsername();
                if (guest.equals(activeGuest)) {
                    addSystemMessage("Guest has left the chat.");
                }
            }
            default -> { }
        }
    }

    private void refreshGuestList(String payload) {
        guestsList.getItems().clear();
        if (payload == null || payload.isEmpty()){
            return;
        }
        String[] entries = payload.split(";");
        for (String entry : entries) {
            if (entry.isEmpty()){
                continue;
            }
            String[] parts = entry.split("\\|");
            if (parts.length < 2){
                continue;
            }
            String guest = parts[0];
            String state = parts[1];
            String label;
            if ("WAITING".equals(state)) {
                label = guest + "  ·  WAITING";
            } else if (state.equals(session.getCurrentReceptionist().getUsername())) {
                label = guest + "  ·  " + state;
            } else {
                continue;
            }
            guestsList.getItems().add(label);
        }
    }

    private void openConversation(String guest) {
        activeGuest = guest;
        lblActiveGuest.setText("Chat with " + guest);
        showConversation(guest);
    }

    private VBox getConversation(String guest) {
        return conversationsCache.computeIfAbsent(guest, k -> new VBox(10));
    }

    private void showConversation(String guest) {
        messagesBox.getChildren().clear();
        VBox cached = getConversation(guest);
        for (javafx.scene.Node node : cached.getChildren()) {
            if (node instanceof HBox srcRow && !srcRow.getChildren().isEmpty()
                    && srcRow.getChildren().get(0) instanceof VBox srcBubble) {
                String sender  = ((Label) srcBubble.getChildren().get(0)).getText();
                String content = ((Label) srcBubble.getChildren().get(1)).getText();
                boolean fromMe = ((Label) srcBubble.getChildren().get(0))
                        .getStyleClass().contains("chat-bubble-sender-me");
                messagesBox.getChildren().add(buildMessageRow(sender, content, fromMe));
            }
        }
        messagesScroll.layout();
        messagesScroll.setVvalue(1.0);
    }

    @FXML
    private void handleSend() {
        String text = txtMessage.getText().trim();
        if (text.isEmpty() || activeGuest == null){
            return;
        }
        ChatMessage msg = new ChatMessage(
                ChatMessage.Type.TEXT,
                session.getCurrentReceptionist().getUsername(), "",
                activeGuest, text);
        client.send(msg);

        HBox cacheRow = buildMessageRow("You", text, true);
        getConversation(activeGuest).getChildren().add(cacheRow);
        HBox liveRow = buildMessageRow("You", text, true);
        messagesBox.getChildren().add(liveRow);
        messagesScroll.layout();
        messagesScroll.setVvalue(1.0);
        txtMessage.clear();
    }

    private HBox buildMessageRow(String sender, String content, boolean fromMe) {
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
        return row;
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