import java.util.Stack;

public class AppSession {

    private User currentUser;
    private final ReservationContext reservationContext = new ReservationContext();
    private Guest selectedGuest;
    private Room      selectedRoom;
    private RoomType  selectedRoomType;
    private Amenity   selectedAmenity;

    //for all dashboards
    public User getCurrentUser()          { return currentUser; }
    public void setCurrentUser(User u)    { currentUser = u; }
    //for guest only
    public ReservationContext getReservationContext() { return reservationContext; }
    public Guest getCurrentGuest()               { return (Guest) currentUser; }
    // for receptionist
    public Guest getSelectedGuest()               { return selectedGuest; }
    public void  setSelectedGuest(Guest g)        { selectedGuest = g; }
    //for admin
    public Room getSelectedRoom()                 { return selectedRoom; }
    public void setSelectedRoom(Room r)           { selectedRoom = r; }
    public RoomType getSelectedRoomType()         { return selectedRoomType; }
    public void     setSelectedRoomType(RoomType t){ selectedRoomType = t; }
    public Amenity getSelectedAmenity()           { return selectedAmenity; }
    public void    setSelectedAmenity(Amenity a)  { selectedAmenity = a; }
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
        System.out.println("i logged out");
    }
}
