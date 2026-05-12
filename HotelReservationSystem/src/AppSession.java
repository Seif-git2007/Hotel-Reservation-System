import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class AppSession {

    private User currentUser;
    private final ReservationContext reservationContext = new ReservationContext();
    private Guest selectedGuest;
    private Room      selectedRoom;
    private RoomType  selectedRoomType;
    private Amenity   selectedAmenity;
    private Invoice dueInvoice;

    private static ChatClient sharedChatClient;
    public static ChatClient getChatClient()       { return sharedChatClient; }
    public static void setChatClient(ChatClient c) { sharedChatClient = c; }

    private ChatClient chatClient;
    private final List<Node>         guestChatMessages  = new ArrayList<>();
    private String                   receptionistUsername = null;
    private final List<String>       pendingMessages    = new ArrayList<>();
    private final Map<String, VBox>  conversationsCache = new HashMap<>();
    private String                   activeChatGuest    = null;
    private final List<String>       guestListItems     = new ArrayList<>();

    public ChatClient getChatClientLocal()                        { return chatClient; }
    public void       setChatClientLocal(ChatClient c)           { chatClient = c; }
    public List<Node> getGuestChatMessages()                     { return guestChatMessages; }
    public String     getReceptionistUsername()                  { return receptionistUsername; }
    public void       setReceptionistUsername(String r)          { receptionistUsername = r; }
    public List<String> getPendingMessages()                     { return pendingMessages; }
    public Map<String, VBox> getConversationsCache()             { return conversationsCache; }
    public String     getActiveChatGuest()                       { return activeChatGuest; }
    public void       setActiveChatGuest(String g)               { activeChatGuest = g; }
    public List<String> getGuestListItems()                      { return guestListItems; }

    public Invoice getDueInvoice()              { return dueInvoice; }
    public void setDueInvoice(Invoice d)        { dueInvoice = d; }
    public User getCurrentUser()                { return currentUser; }
    public void setCurrentUser(User u)          { currentUser = u; }
    public ReservationContext getReservationContext() { return reservationContext; }
    public Guest getCurrentGuest()              { return (Guest) currentUser; }
    public Receptionist getCurrentReceptionist(){ return (Receptionist) currentUser; }
    public Guest getSelectedGuest()             { return selectedGuest; }
    public void  setSelectedGuest(Guest g)      { selectedGuest = g; }
    public Room getSelectedRoom()               { return selectedRoom; }
    public void setSelectedRoom(Room r)         { selectedRoom = r; }
    public RoomType getSelectedRoomType()       { return selectedRoomType; }
    public void setSelectedRoomType(RoomType t) { selectedRoomType = t; }
    public Amenity getSelectedAmenity()         { return selectedAmenity; }
    public void setSelectedAmenity(Amenity a)   { selectedAmenity = a; }

    Stack<String> history = new Stack<>();

    public void logout() {
        history.clear();
        currentUser.setLoggedIn(false);
        currentUser      = null;
        selectedGuest    = null;
        selectedRoom     = null;
        selectedRoomType = null;
        selectedAmenity  = null;
        reservationContext.clear();
        if (chatClient != null) {
            chatClient.disconnect();
            chatClient = null;
        }
        guestChatMessages.clear();
        pendingMessages.clear();
        conversationsCache.clear();
        receptionistUsername = null;
        activeChatGuest = null;
        guestListItems.clear();
        System.out.println("i logged out");
    }
}