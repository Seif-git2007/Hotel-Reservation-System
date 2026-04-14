import java.time.LocalDate;
import java.util.ArrayList;

public class Admin extends Staff{



    public Admin(String name, String password, LocalDate date,int hours,Gender gender) {
        super(name, password, date, gender);
        super.setWorkingHours(hours);
    }


    public void viewRooms(){
        Room.read();
    }

       public void viewRoomTypes(){
        RoomType.read();
    }

    public void addRoom(int roomNo, int floor, Room.view view, RoomType type, ArrayList<Amenity> amenities) throws InvalidInputException {
        Room r = new Room(type, amenities,roomNo,floor,view);
        try {
            Room.create(r);
        }
        catch (Exception e){
            throw new InvalidInputException("Can't Add This Room");
        }
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
