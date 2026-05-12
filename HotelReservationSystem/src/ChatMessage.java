import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatMessage implements Serializable {

    public enum Type {
        REGISTER,
        GUEST_REQUEST,
        CLAIM,
        UNCLAIM,
        TEXT,
        GUEST_LIST_UPDATE,
        GUEST_LEFT
    }

    private final Type   type;
    private final String fromUsername;
    private final String fromDisplay;
    private final String toUsername;
    private final String content;
    private final LocalDateTime timestamp;

    public ChatMessage(Type type, String fromUsername, String fromDisplay,
                       String toUsername, String content) {
        this.type         = type;
        this.fromUsername = fromUsername;
        this.fromDisplay  = fromDisplay;
        this.toUsername   = toUsername;
        this.content      = content;
        this.timestamp    = LocalDateTime.now();
    }

    public Type   getType()         { return type; }
    public String getFromUsername() { return fromUsername; }
    public String getFromDisplay()  { return fromDisplay; }
    public String getToUsername()   { return toUsername; }
    public String getContent()      { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + type + "] " + fromUsername + " → " + toUsername + ": " + content;
    }
}
