import java.time.LocalDate;
import java.util.ArrayList;

public class Room implements Manageable{
    private RoomType type;
    private ArrayList<Amenity> amenities=new ArrayList<>(); // used amenities in current room
    private int roomNumber;
    private int floor;
    public enum view{SEA,POOL,CITY};
    private view View;

    public Room(RoomType type, ArrayList<Amenity> amenities, int roomNumber, int floor, Room.view view) {
        this.type = type;
        this.amenities = amenities;
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

    public boolean isAvailable(LocalDate checkInDate,LocalDate checkOutDate) {
        for(Reservation r:HotelDataBase.reservations){
            if(r.getRoom()==this&&(r.getStatus()== Reservation.Status.PENDING||r.getStatus()== Reservation.Status.CONFIRMED)){
                if((checkInDate.isAfter(r.getCheckInDate())&&checkInDate.isBefore(r.getCheckOutDate()))||(checkOutDate.isAfter(r.getCheckInDate())&&checkOutDate.isBefore(r.getCheckOutDate()))){
                    return false;
                }
            }

        }
        return true;
    }
    //hi




    public void create(RoomType roomType, int roomNumber, int floor, view View, Amenity amenities) {


    }
    @Override
    public void read(){

    }

    public void update() {

    }

    @Override
    public void delete(int index) {

    }
    @Override
    public String toString() {
        return "Room " + roomNumber + " | " + type.getSize() + " | Floor " + floor + " | $" + type.getBasePrice() + "/night" + " | View: " + View + " | Amenities: " + amenities;
    }
}
