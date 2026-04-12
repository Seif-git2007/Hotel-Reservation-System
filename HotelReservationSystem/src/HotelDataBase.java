import java.time.LocalDate;
import java.util.ArrayList;

public class HotelDataBase {
     static ArrayList<User> users =new ArrayList<>();
     static ArrayList<Reservation> reservations =new ArrayList<>();
     static ArrayList<Invoice> invoices =new ArrayList<>();
     static ArrayList<Room> rooms=new ArrayList<>();
     static ArrayList<RoomType> roomTypes =new ArrayList<>();
     static ArrayList<Amenity> amenities =new ArrayList<>(); //all available amenities

     static {
          Admin admin=new Admin("1","1", LocalDate.parse("2000-12-05"),8);
          users.add(admin);
          Receptionist receptionist=new Receptionist("2","2",LocalDate.parse("2002-10-03"),10);
          users.add(receptionist);
          roomPreferences prefer=new roomPreferences(5,Room.view.POOL);
          Guest guest =new Guest("3","3",LocalDate.parse("2000-12-16"),100000,prefer,"1st street");
          users.add(guest);
          RoomType type=new RoomType("Single",200,2);
          roomTypes.add(type);
          Room room=new Room(type,amenities,true,911,3, Room.view.SEA);
          rooms.add(room);
     }

     public static ArrayList<Room> getAvailableRooms() {
          ArrayList<Room> available = new ArrayList<>();
          for (Room r : rooms) {
               if (r.isAvailable()) {
                    available.add(r);
               }
          }
          return available;
     }
     public static ArrayList<Reservation> getPendingReservations(){
          ArrayList<Reservation> pending=new ArrayList<>();
          for(Reservation r:reservations){
               if(r.getStatus()== Reservation.Status.PENDING){
                    pending.add(r);
               }
          }
          return pending;
     }

}
