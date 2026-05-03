import java.time.LocalDate;
import java.util.ArrayList;

public class Admin extends Staff {

    public Admin(String name, String password, LocalDate date, int hours, Gender gender, String email) {
        super(name, password, date, gender, email);
        super.setWorkingHours(hours);
    }

    public void addRoom(int roomNo, int floor, Room.view view, RoomType type, ArrayList<Amenity> amenities) throws InvalidInputException {
        Room r = new Room(type, amenities, roomNo, floor, view);
        Room.create(r);
    }

    public void removeRoom(int index) throws InvalidInputException {
        HotelDataBase.rooms.get(index).delete(index);
    }

    public void updateRoom(int roomNo, int floor, Room.view view, RoomType type, ArrayList<Amenity> amenities, int index) throws InvalidInputException {
        Room r = new Room(type, amenities, roomNo, floor, view);
        HotelDataBase.rooms.get(index).update(r);
    }

    public void viewRoomTypes() {
        RoomType.read();
    }

    public void addRoomType(String size, double basePrice, int capacity) throws InvalidInputException {
        RoomType rt = new RoomType(size, basePrice, capacity);
        RoomType.create(rt);
    }

    public void removeRoomType(int index) throws InvalidInputException {
        HotelDataBase.roomTypes.get(index).delete(index);
    }

    public void updateRoomTypes(String size, double basePrice, int capacity, int index) throws InvalidInputException {
        RoomType rt = new RoomType(size, basePrice, capacity);
        HotelDataBase.roomTypes.get(index).update(rt);
    }

    public void viewAmenities() {
        Amenity.read();
    }

    public void addAmenity(String name, double price) throws InvalidInputException {
        Amenity am = new Amenity(name, price);
        Amenity.create(am);
    }

    public void removeAmenity(int index) throws InvalidInputException {
        HotelDataBase.amenities.get(index).delete(index);
    }

    public void updateAmenityPrice(String name, double newPrice, int index) throws InvalidInputException {
        Amenity am = new Amenity(name, newPrice);
        HotelDataBase.amenities.get(index).update(am);
    }

    public void viewReceptionists() {
        int count = 1;
        for (Receptionist r : HotelDataBase.getReceptionists()) {
            System.out.println(count + ". " + r);
            count++;
        }
    }

    public void addReceptionists(String name, String pass, LocalDate date, int hours, String gender, String email) throws InvalidInputException {
        Receptionist r = new Receptionist(name, pass, date, hours, Gender.valueOf(gender.toUpperCase()), email);
        HotelDataBase.users.add(r);
        DataBaseManager.runAsync(() -> {
            DataBaseManager.saveUser(r);
            EventBus.fire(EventBus.Event.USER_CHANGED);
        });
    }
}
