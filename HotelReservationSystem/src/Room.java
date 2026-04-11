import java.util.ArrayList;

public class Room implements Manageable{
    private RoomType type;
    private ArrayList<Amenity> amenities=new ArrayList<>(); // used amenities in current room
    private boolean isAvailable;
    private int roomNumber;
    private int floor;
    public enum view{SEA,POOL,CITY};
    private view View;

    public Room(RoomType type, ArrayList<Amenity> amenities, boolean isAvailable, int roomNumber, int floor, Room.view view) {
        this.type = type;
        this.amenities = amenities;
        this.isAvailable = isAvailable;
        this.roomNumber = roomNumber;
        this.floor = floor;
        View = view;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public Room.view getView() {
        return View;
    }

    public void setView(Room.view view) {
        View = view;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public ArrayList<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(ArrayList<Amenity> amenities) {
        this.amenities = amenities;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public void create() {

    }
    
    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }
}
