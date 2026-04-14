import java.time.LocalDate;
import java.util.ArrayList;

public class Admin extends Staff{



    public Admin(String name, String password, LocalDate date,int hours,Gender gender) {
        super(name, password, date, gender);
        super.setWorkingHours(hours);
    }

    // Rooms Methods
    public void viewRooms(){
        Room.read();
    }

    public void addRoom(int roomNo, int floor, Room.view view, RoomType type, ArrayList<Amenity> amenities) throws InvalidInputException {
        Room r = new Room(type, amenities,roomNo,floor,view);
        try {
            Room.create(r);
        }
        catch (Exception e){
            throw e;
        }

    }

    public void removeRoom(int index) throws InvalidInputException{
        try{
            HotelDataBase.rooms.get(index).delete(index);
        }
        catch (Exception e){
            throw e;
        }

    }

    public void updateRoom(int roomNo, int floor, Room.view view, RoomType type, ArrayList<Amenity> amenities, int index) throws InvalidInputException{
        Room r = new Room(type, amenities,roomNo,floor,view);
        try {
            HotelDataBase.rooms.get(index).update(r);
        }
        catch (Exception e){
            throw e;
        }
    }


    //RoomTypes Methods

    public void viewRoomTypes(){
        RoomType.read();
    }

    public void addRoomType(String size, double basePrice, int capacity) throws InvalidInputException{
        RoomType rt = new RoomType(size, basePrice, capacity);
        try {
            RoomType.create(rt);
        }
        catch (Exception e){
            throw e;
        }
    }

    public void removeRoomType(int index) throws InvalidInputException{
        try{
            HotelDataBase.roomTypes.get(index).delete(index);
        }
        catch (Exception e){
            throw e;
        }

    }

    public void updateRoomTypes(String size , double basePrice, int capacity, int index) throws InvalidInputException{
         RoomType rt = new RoomType(size, basePrice,capacity);
         try{
             HotelDataBase.roomTypes.get(index).update(rt);
         }
         catch(Exception e){
             throw e;
         }

    }


    //Amenities methods

    public void viewAmenities() {

    }

    public void addAmenity(String name, double price) {

    }

    public void removeAmenity(String name) {

    }

    public void updateAmenityPrice(String name, double newPrice) {

    }
}
