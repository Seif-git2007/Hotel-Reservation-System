import java.time.LocalDate;

public class Admin extends Staff{



    public Admin(String name, String password, LocalDate date,int hours,Gender gender) {
        super(name, password, date, gender);
        super.setWorkingHours(hours);
    }


    public void viewRooms(){
        if (HotelDataBase.rooms.isEmpty()){
            System.out.println("Currently, there are no available rooms types.");
            return;
        } else{
            for (Room r : HotelDataBase.rooms){
                System.out.println("Current available rooms types :");
                System.out.println(r.toString());
            }
        }

    }

       public void viewRoomTypes(){
        if (HotelDataBase.roomTypes.isEmpty()){
            System.out.println("Currently, there are no available rooms types.");
            return;
        } else{
            for (RoomType r : HotelDataBase.roomTypes){
                System.out.println("Current available rooms types :");
                System.out.println(r.toString());
            }
        }

    }

    public void addRoom(int roomNo, int floor, Room.view view, RoomType type) throws InvalidInputException {
        for(Room r : HotelDataBase.rooms){
            if(r.getRoomNumber()==roomNo){
                throw new InvalidInputException("Room " +roomNo +"already exists.");
            }
            Room newRoom = new Room(type, new java.util.ArrayList<>(), roomNo, floor, view);
            HotelDataBase.rooms.add(newRoom);
        }
    }

    public void removeRoom(int roomNo){

    }


    //Amenities methods
    public void viewAmenities() {
        if (HotelDataBase.amenities.isEmpty()) {
            System.out.println("There are no amenities currently in the system.");
            return;
        }
        System.out.println("--- List of All Amenities ---");

        int count = 1; // Our manual counter
        for (Amenity a : HotelDataBase.amenities) {
            System.out.println(count + ". " + a);
            count++;
        }

        System.out.println("-----------------------------");
    }

    public void addAmenity(String name, double price) {
        // Check if it already exists to prevent duplicates
        for (Amenity a : HotelDataBase.amenities) {
            if (a.getName().equalsIgnoreCase(name)) {
                System.out.println("Error: Amenity '" + name + "' already exists!");
                return;
            }
        }

        Amenity newAmenity = new Amenity(name, price);
        HotelDataBase.amenities.add(newAmenity);
        System.out.println("Amenity '" + name + "' added successfully!");
    }

    public void removeAmenity(String name) {
        Amenity toRemove = null;
        for (Amenity a : HotelDataBase.amenities) {
            if (a.getName().equalsIgnoreCase(name)) {
                toRemove = a;
                break;
            }
        }

        if (toRemove != null) {
            HotelDataBase.amenities.remove(toRemove);
            System.out.println("Amenity '" + name + "' removed successfully.");
        } else {
            System.out.println("Error: Amenity '" + name + "' not found.");
        }
    }

    public void updateAmenityPrice(String name, double newPrice) {
        for (Amenity a : HotelDataBase.amenities) {
            if (a.getName().equalsIgnoreCase(name)) {
                a.setPrice(newPrice);
                System.out.println("Amenity '" + name + "' price updated to $" + newPrice);
                return;
            }
        }
        System.out.println("Error: Amenity '" + name + "' not found.");
    }
}
