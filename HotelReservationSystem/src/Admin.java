import java.time.LocalDate;
import java.util.ArrayList;

public class Admin extends Staff{



    public Admin(String name, String password, LocalDate date,int hours,Gender gender) {
        super(name, password, date, gender);
        super.setWorkingHours(hours);
    }


    public void viewRooms(ArrayList<Amenity> amenities , RoomType roomType, int roomNumber, int floor, Room.view View){
        Room r = new Room(roomType, amenities,roomNumber,floor, View);
        r.read();
    }

       public void viewRoomTypes(){


    }

    public void addRoom(int roomNo, int floor, Room.view view, RoomType type) throws InvalidInputException {

    }

    public void removeRoom(int roomNo){

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
