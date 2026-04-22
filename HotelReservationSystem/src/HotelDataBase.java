import java.time.LocalDate;
import java.util.ArrayList;

public class HotelDataBase {
     static ArrayList<User> users = new ArrayList<>();
     static ArrayList<Reservation> reservations = new ArrayList<>();
     static ArrayList<Invoice> invoices = new ArrayList<>();
     static ArrayList<Room> rooms = new ArrayList<>();
     static ArrayList<RoomType> roomTypes = new ArrayList<>();
     static ArrayList<Amenity> amenities = new ArrayList<>();//all available amenities

     static {
          Admin admin = new Admin("1", "1", LocalDate.parse("2000-12-05"), 8, User.Gender.MALE);
          users.add(admin);

          Receptionist receptionist = new Receptionist("2", "2", LocalDate.parse("2002-10-03"), 10, User.Gender.MALE);
          users.add(receptionist);

          roomPreferences prefer = new roomPreferences(5, Room.view.POOL);
          Guest guest = new Guest("3", "3", LocalDate.parse("2000-12-16"), 500, prefer, "1st street", User.Gender.MALE);
          users.add(guest);

          RoomType type = new RoomType("Single", 200, 1);
          RoomType type1 = new RoomType("Double", 300, 2);
          RoomType type2 = new RoomType("Suite", 600, 4);
          roomTypes.add(type);
          roomTypes.add(type1);
          roomTypes.add(type2);

          Amenity amenity = new Amenity("jaccuzi", 200);
          Amenity amenity1 = new Amenity("spa", 200);
          Amenity amenity2 = new Amenity("wifi", 20);
          amenities.add(amenity);
          HotelDataBase.amenities.add(amenity1);
          HotelDataBase.amenities.add(amenity2);
          ArrayList<Amenity> amenities1 = new ArrayList<Amenity>();
          amenities1.add(amenity2);
          Room room = new Room(type, amenities1, 911, 3, Room.view.SEA);
          ArrayList<Amenity> amenities2 = new ArrayList<Amenity>();
          amenities2.add(amenity1);
          amenities2.add(amenity2);
          Room room1 = new Room(type2, amenities2, 912, 3, Room.view.POOL);
          rooms.add(room);
          rooms.add(room1);


     }

     public static ArrayList<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate) {
          ArrayList<Room> available = new ArrayList<>();
          for (Room r : rooms) {
               if (r.isAvailable(checkInDate, checkOutDate)) {
                    available.add(r);
               }
          }
          return available;
     }

     public static ArrayList<Reservation> getPendingReservations() {
          ArrayList<Reservation> pending = new ArrayList<>();
          for (Reservation r : reservations) {
               if (r.getStatus() == Reservation.Status.PENDING) {
                    pending.add(r);
               }
          }
          return pending;
     }

     public static User searchUser(String name) {
          for (User u : users) {
               if (u.getUsername().equals(name)) {
                    return u;
               }
          }
          return null;
     }

     public static ArrayList<Guest> getPendingGuests() {// printed in main for receptionist to choose
          ArrayList<Guest> guests = new ArrayList<>();
          for (Reservation r : HotelDataBase.reservations) {
               if (r.getStatus() == Reservation.Status.PENDING && Receptionist.isToday(r.getCheckInDate()) && !guests.contains(r.getGuest())) {
                    guests.add(r.getGuest());
               }
          }
          return guests;
     }

     public static ArrayList<Reservation> getGuestReservation(Guest guest) {
          ArrayList<Reservation> reservations = new ArrayList<>();
          for (Reservation r : HotelDataBase.reservations) {
               if (r.getGuest() == guest
                       && r.getStatus() == Reservation.Status.PENDING
                       && Receptionist.isToday(r.getCheckInDate())) {
                    reservations.add(r);
               }
          }
          return reservations;
     }

     public static ArrayList<Guest> checktodayinvoices() {
          ArrayList<Guest> guests = new ArrayList<>();
// de hatshof lw el guest mawgowd lw msh mawgod add
          for (Invoice inv : HotelDataBase.invoices) {
               for (Reservation r : inv.getReservation()) {
                    if (Receptionist.isToday(r.getCheckOutDate()) && !guests.contains(inv.getGuest()) && r.getStatus() != Reservation.Status.COMPLETED) {
                         guests.add(inv.getGuest());
                         break;
                    }
               }
          }
          return guests;
     }

     public static ArrayList<Guest> filterGuest() {
          ArrayList<Guest> filtered = new ArrayList<>();
          for (User u : users) {
               if (u instanceof Guest) {
                    filtered.add((Guest) u);
               }
          }
          return filtered;
     }

     public static ArrayList<Room> filterRooms(ArrayList<Room> availableRooms, roomPreferences preferred) {
          ArrayList<Room> filteredRooms = new ArrayList<>();
          for (Room r : availableRooms) {
               if (r.getFloor() == preferred.getFloor() || r.getView() == preferred.getView()) {
                    filteredRooms.add(r);
               }
          }
          return filteredRooms;


     }

     public static ArrayList<Receptionist> getReceptionists() {
          ArrayList<Receptionist> receptionist = new ArrayList<>();
          for(User u : users){
               if (u instanceof Receptionist){
                    receptionist.add((Receptionist) u);
               }
          }
          return receptionist;
     }

}


