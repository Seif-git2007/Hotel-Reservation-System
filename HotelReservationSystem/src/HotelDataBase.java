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
          Admin admin = new Admin("1", "1", LocalDate.parse("2000-12-05"), 8, User.Gender.MALE,"admin123@gmail.com");
          users.add(admin);

          Receptionist receptionist = new Receptionist("2", "2", LocalDate.parse("2002-10-03"), 10, User.Gender.MALE,"recep123@gmail.com");
          users.add(receptionist);

          roomPreferences prefer = new roomPreferences(5, Room.view.POOL);
          Guest guest = new Guest("3", "3", LocalDate.parse("2000-12-16"), 500, prefer, "1st street", User.Gender.MALE,"Seif Mahrous","seifmahrous2007@gmail.com");
          users.add(guest);

          roomPreferences prefer1 = new roomPreferences(3, Room.view.SEA);
          Guest guest1 = new Guest("4", "4", LocalDate.parse("2003-10-12"), 50000, prefer1, "2nd street", User.Gender.FEMALE,"Norhan ELzahby","norhanfawzy2007@gmail.com");
          users.add(guest1);

          RoomType type = new RoomType("Single", 200, 1);
          RoomType type1 = new RoomType("Double", 300, 2);
          RoomType type2 = new RoomType("Triple", 400, 3);
          RoomType type3 = new RoomType("Suite", 600, 4);
          RoomType type4 =new RoomType("Pent-House", 2000, 6);
          RoomType type5 = new RoomType("Presidential Suite", 1000, 5);
          roomTypes.add(type);
          roomTypes.add(type1);
          roomTypes.add(type2);
          roomTypes.add(type3);
          roomTypes.add(type4);
          roomTypes.add(type5);

          Amenity amenity  = new Amenity("jaccuzi", 400);
          Amenity amenity1 = new Amenity("spa", 200);
          Amenity amenity2 = new Amenity("wifi", 20);
          Amenity amenity3 = new Amenity("Mini-Fridge", 100);

          amenities.add(amenity);
          amenities.add(amenity1);
          amenities.add(amenity2);
          amenities.add(amenity3);



          ArrayList<Amenity> amenities1 = new ArrayList<Amenity>();
          ArrayList<Amenity> amenities2 = new ArrayList<Amenity>();
          ArrayList<Amenity> amenities3 = new ArrayList<Amenity>();
          ArrayList<Amenity> amenities4 = new ArrayList<Amenity>();
          ArrayList<Amenity> amenities5 = new ArrayList<Amenity>();
          amenities1.add(amenity2);
          amenities2.add(amenity3);
          amenities2.add(amenity2);
          amenities3.add(amenity);
          amenities3.add(amenity1);
          amenities3.add(amenity2);
          amenities4.add(amenity1);
          amenities4.add(amenity2);
          amenities4.add(amenity3);
          amenities5.add(amenity);
          amenities5.add(amenity1);
          amenities5.add(amenity2);
          amenities5.add(amenity3);

          Room room = new Room(type, amenities1, 911, 3, Room.view.SEA);
          Room room1 = new Room(type2, amenities2, 912, 3, Room.view.POOL);
          Room room2 = new Room(type3, amenities3, 913, 5, Room.view.CITY);
          Room room3 = new Room(type4, amenities4, 914, 5, Room.view.SEA);
          Room room4 = new Room(type5, amenities5, 915, 6, Room.view.POOL);
          rooms.add(room);
          rooms.add(room1);
          rooms.add(room2);
          rooms.add(room3);
          rooms.add(room4);

          Reservation reservation =new Reservation(guest, room, LocalDate.parse("2024-06-20"), LocalDate.parse("2024-06-25"));
          reservation.setStatus(Reservation.Status.COMPLETED);
          reservations.add(reservation);
          Reservation reservation1 =new Reservation(guest, room, LocalDate.parse("2024-06-20"), LocalDate.parse("2024-06-25"));
          reservation1.setStatus(Reservation.Status.CONFIRMED);
          reservations.add(reservation1);
          Reservation reservation2 =new Reservation(guest, room, LocalDate.parse("2024-06-20"), LocalDate.parse("2024-06-25"));
          reservation2.setStatus(Reservation.Status.PENDING);
          reservations.add(reservation2);
          Reservation reservation3 =new Reservation(guest, room, LocalDate.parse("2024-06-20"), LocalDate.parse("2024-06-25"));
          reservation3.setStatus(Reservation.Status.PENDING);
          reservations.add(reservation3);
          Reservation reservation4 =new Reservation(guest, room, LocalDate.parse("2024-06-20"), LocalDate.parse("2024-06-25"));
          reservation4.setStatus(Reservation.Status.PENDING);
          reservations.add(reservation4);
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

     public static User searchUserByName(String name) {
          for (User u : users) {
               if (u.getUsername().toUpperCase().equals(name.toUpperCase())) {
                    return u;
               }
          }
          return null;
     }
     public static User searchUserByEmail(String email) {
          for (User u : users) {
               if (u.getEmail().equals(email)) {
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
               if (r.getGuest() == guest) {
                    reservations.add(r);
               }

          }
          return reservations;
     }
     public static ArrayList<Reservation> getGuestPendingReservation(Guest guest) {
          ArrayList<Reservation> reservations = new ArrayList<>();
          for (Reservation r : HotelDataBase.reservations) {
               if (r.getGuest() == guest && r.getStatus() == Reservation.Status.PENDING) {
                    reservations.add(r);
               }
          }
          return reservations;
     }
     public static ArrayList<Reservation> receptionistGetGuestPendingReservation(Guest guest) {
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

     public static ArrayList<Room> filterRoomsByPreferences(ArrayList<Room> availableRooms, roomPreferences preferred) {
          ArrayList<Room> filteredRooms = new ArrayList<>();
          for (Room r : availableRooms) {
               if (r.getFloor() == preferred.getFloor() || r.getView() == preferred.getView()) {
                    filteredRooms.add(r);
               }
          }
          return filteredRooms;
     }
     public static ArrayList<Room> filterRoomsByPrice(double price) {
          ArrayList<Room> filteredRooms = new ArrayList<>();
          for (Room r : rooms) {
               if (r.getType().getBasePrice()<=price) {
                    filteredRooms.add(r);
               }
          }
          return filteredRooms;
     }
     public static ArrayList<Room> filterRoomsByAmenities(ArrayList<Amenity> reqAmenities) {
          ArrayList<Room> filteredRooms = new ArrayList<>();
          for (Room r : rooms) {
               for(Amenity a :reqAmenities){
                    if(r.getAmenities().contains(a)){
                         filteredRooms.add(r);
                    }
               }
          }
          return filteredRooms;
     }
     public static ArrayList<Room> filterRoomsByRoomType(ArrayList<RoomType> reqRoomTypes) {
          ArrayList<Room> filteredRooms = new ArrayList<>();
          for (Room r : rooms) {
               if (reqRoomTypes.contains(r.getType())) {
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


