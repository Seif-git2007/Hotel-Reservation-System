import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class Room {
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
                if(!( checkOutDate.isBefore(r.getCheckInDate()) || checkOutDate.equals(r.getCheckInDate())
                        || checkInDate.isAfter(r.getCheckOutDate()) || checkInDate.equals(r.getCheckOutDate()) )){
                    return false;
                }

            }

        }
        return true;
    }
    //hi 5elo


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Room room)) return false;
        return roomNumber == room.roomNumber && floor == room.floor && Objects.equals(type, room.type) && Objects.equals(amenities, room.amenities) && View == room.View;
    }
    @Override
    public String toString() {
        return "Room " + roomNumber + " | " + type.getSize() + " | Floor " + floor + " | $" + type.getBasePrice() + "/night" + " | View: " + View + " | Amenities: " + amenities;
    }

    public static void create(Room newRoom) throws InvalidInputException {
        for(Room R : HotelDataBase.rooms){
            if(R.equals(newRoom)){
                throw new InvalidInputException("Room Already Exists");
            }
            if(R.getRoomNumber()==newRoom.getRoomNumber()){
                throw new InvalidInputException("Room Number Already Exists");
            }
        }
        HotelDataBase.rooms.add(newRoom);
        System.out.println("Room Has Been Created Successfully");
    }

    public void update(Room modifiedRoom) throws InvalidInputException {
        for(Room R : HotelDataBase.rooms){
            if(R.equals(modifiedRoom)){
                throw new InvalidInputException("No Modifications Are Preformed");
            }
        }
        for(Reservation res : HotelDataBase.reservations){
            if(res.getRoom().equals(this) && (res.getStatus() == Reservation.Status.PENDING || res.getStatus() == Reservation.Status.CONFIRMED )){
                throw new InvalidInputException("Can't Modify Room While It's In Use");
            }
        }
        this.type = modifiedRoom.getType();
        this.floor = modifiedRoom.getFloor();
        this.View = modifiedRoom.getView();
        this.amenities = modifiedRoom.getAmenities();
        System.out.println("Room Has Been Modified Successfully");

    }

    public void delete(int index) throws InvalidInputException {
        for(Reservation res : HotelDataBase.reservations){
            if(res.getRoom().equals(HotelDataBase.rooms.get(index)) && (res.getStatus() == Reservation.Status.PENDING || res.getStatus() == Reservation.Status.CONFIRMED )){
                throw new InvalidInputException("Can't Delete Room While It's In Use");
            }
        }
        HotelDataBase.rooms.remove(index);
        System.out.println("Room Has Been Deleted Successfully");
    }

}
