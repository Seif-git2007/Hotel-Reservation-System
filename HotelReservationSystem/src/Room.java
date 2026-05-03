import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

public class Room {
    private RoomType type;
    private ArrayList<Amenity> amenities = new ArrayList<>();
    private int roomNumber;
    private int floor;
    public enum view { SEA, POOL, CITY }
    private view View;

    public Room(RoomType type, ArrayList<Amenity> amenities, int roomNumber, int floor, Room.view view) {
        this.type = type;
        this.amenities = amenities;
        this.roomNumber = roomNumber;
        this.floor = floor;
        View = view;
    }

    public int getRoomNumber()              { return roomNumber; }
    public void setRoomNumber(int n)        { roomNumber = n; }
    public int getFloor()                   { return floor; }
    public void setFloor(int f)             { floor = f; }
    public Room.view getView()              { return View; }
    public void setView(Room.view v)        { View = v; }
    public RoomType getType()               { return type; }
    public void setType(RoomType t)         { type = t; }
    public ArrayList<Amenity> getAmenities(){ return amenities; }
    public void setAmenities(ArrayList<Amenity> a) { amenities = a; }

    public boolean isAvailable(LocalDate checkInDate, LocalDate checkOutDate) {
        for (Reservation r : HotelDataBase.reservations) {
            if (r.getRoom() == this && (r.getStatus() == Reservation.Status.PENDING || r.getStatus() == Reservation.Status.CONFIRMED)) {
                if (checkInDate.equals(checkOutDate)) {
                    if (!checkInDate.isBefore(r.getCheckInDate()) && !checkInDate.isAfter(r.getCheckOutDate())){
                        return false;
                    }
                } else {
                    if (!(checkOutDate.isBefore(r.getCheckInDate()) || checkOutDate.equals(r.getCheckInDate()) || checkInDate.isAfter(r.getCheckOutDate()) || checkInDate.equals(r.getCheckOutDate()))){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void create(Room newRoom) throws InvalidInputException {
        for (Room R : HotelDataBase.rooms) {
            if (R.equals(newRoom)){
                throw new RoomInUseException("Room Already Exists");
            }
            if (R.getRoomNumber() == newRoom.getRoomNumber()){
                throw new RoomInUseException("Room Number Already Exists");
            }
        }
        HotelDataBase.rooms.add(newRoom);
        DataBaseManager.runAsync(() -> {
            DataBaseManager.saveRoom(newRoom);
            EventBus.fire(EventBus.Event.ROOM_CHANGED);
        });
        System.out.println("Room Has Been Created Successfully");
    }

    public void update(Room modifiedRoom) throws InvalidInputException {
        for (Room R : HotelDataBase.rooms){
            if (R.equals(modifiedRoom)){
                throw new RoomInUseException("No Modifications Are Performed");
            }
        }
        for (Reservation res : HotelDataBase.reservations){
            if (res.getRoom().equals(this) && (res.getStatus() == Reservation.Status.PENDING || res.getStatus() == Reservation.Status.CONFIRMED)){
                throw new RoomInUseException("Can't Modify Room While It's In Use");
            }
        }
        this.type = modifiedRoom.getType();
        this.floor = modifiedRoom.getFloor();
        this.View = modifiedRoom.getView();
        this.amenities = modifiedRoom.getAmenities();
        DataBaseManager.runAsync(() -> {
            DataBaseManager.saveRoom(this);
            EventBus.fire(EventBus.Event.ROOM_CHANGED);
        });
        System.out.println("Room Has Been Modified Successfully");
    }

    public void delete(int index) throws InvalidInputException {
        synchronized (HotelDataBase.rooms) {
            synchronized (HotelDataBase.reservations) {
                Room target = HotelDataBase.rooms.get(index);
                for (Reservation res : HotelDataBase.reservations){
                    if (res.getRoom().equals(target) && (res.getStatus() == Reservation.Status.PENDING || res.getStatus() == Reservation.Status.CONFIRMED)){
                        throw new RoomInUseException("Can't Delete Room While It's In Use");
                    }
                }
                HotelDataBase.rooms.remove(index);
                DataBaseManager.runAsync(() -> {
                    DataBaseManager.deleteRoom(target);
                    EventBus.fire(EventBus.Event.ROOM_CHANGED);
                });
            }
        }
        System.out.println("Room Has Been Deleted Successfully");
    }

    public double calcTotal(LocalDate checkInDate, LocalDate checkOutDate) {
        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (days == 0){
            days = 1;
        }
        double amenityTotal = 0;
        for (Amenity a : amenities){
            amenityTotal += a.getPrice();
        }
        return (days * type.getBasePrice()) + amenityTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Room room)){
            return false;
        }
        return roomNumber == room.roomNumber && floor == room.floor
                && Objects.equals(type, room.type)
                && Objects.equals(amenities, room.amenities)
                && View == room.View;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " | " + type.getSize() + " | Floor " + floor
                + " | $" + type.getBasePrice() + "/night | View: " + View
                + " | Amenities: " + amenities;
    }
}
