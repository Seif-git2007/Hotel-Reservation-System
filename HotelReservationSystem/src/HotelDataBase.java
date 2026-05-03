import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HotelDataBase {

    static final List<User>users= Collections.synchronizedList(new ArrayList<>());
    static final List<Reservation>reservations= Collections.synchronizedList(new ArrayList<>());
    static final List<Invoice>invoices= Collections.synchronizedList(new ArrayList<>());
    static final List<Room>rooms= Collections.synchronizedList(new ArrayList<>());
    static final List<RoomType>roomTypes= Collections.synchronizedList(new ArrayList<>());
    static final List<Amenity>amenities= Collections.synchronizedList(new ArrayList<>());

    public static ArrayList<Room> getRooms() {
        synchronized (rooms) { return new ArrayList<>(rooms); }
    }
    public static ArrayList<RoomType> getRoomTypes() {
        synchronized (roomTypes) { return new ArrayList<>(roomTypes); }
    }
    public static ArrayList<Amenity> getAmenities() {
        synchronized (amenities) { return new ArrayList<>(amenities); }
    }
    public static ArrayList<User> getUsers() {
        synchronized (users) { return new ArrayList<>(users); }
    }
    public static void  seedDefaultData(){
        Admin admin = new Admin("1", "1", LocalDate.parse("2000-12-05"), 8, User.Gender.MALE, "admin123@gmail.com");
        users.add(admin);

        Receptionist receptionist = new Receptionist("2", "2", LocalDate.parse("2002-10-03"), 10, User.Gender.MALE, "recep123@gmail.com");
        users.add(receptionist);

        roomPreferences prefer  = new roomPreferences(5, Room.view.POOL);
        Guest guest  = new Guest("3", "3", LocalDate.parse("2007-12-16"), 500,   prefer,  "1st street", User.Gender.MALE,   "Seif Mahrous",    "seifmahrous2007@gmail.com");
        roomPreferences prefer1 = new roomPreferences(3, Room.view.SEA);
        Guest guest1 = new Guest("4", "4", LocalDate.parse("2007-10-29"), 50000, prefer1, "2nd street", User.Gender.FEMALE, "Norhan EL-Zahaby",  "norhanfawzy2007@gmail.com");
        roomPreferences prefer2 = new roomPreferences(3, Room.view.SEA);
        Guest guest2  = new Guest("9", "9", LocalDate.parse("2007-09-02"), 500,   prefer2,  "3rd street", User.Gender.MALE,   "Omar El-Gharably",    "omargharably2007@gmail.com");
        roomPreferences prefer3 = new roomPreferences(3, Room.view.SEA);
        Guest guest3  = new Guest("8", "8", LocalDate.parse("2008-08-01"), 500,   prefer3,  "4th street", User.Gender.FEMALE,   "Youstina Nagy",    "youstinanagy2008@gmail.com");
        roomPreferences prefer4 = new roomPreferences(3, Room.view.SEA);
        Guest guest4  = new Guest("5", "5", LocalDate.parse("2008-08-01"), 500,   prefer4,  "5th street", User.Gender.FEMALE,   "Rawan Hamada",    "rawanhamada2006@gmail.com");
        users.add(guest);
        users.add(guest1);
        users.add(guest2);
        users.add(guest3);
        users.add(guest4);
        RoomType type  = new RoomType("Single",200,  1);
        RoomType type1 = new RoomType("Double",300,  2);
        RoomType type2 = new RoomType("Triple",400,  3);
        RoomType type3 = new RoomType("Suite",600,  4);
        RoomType type4 = new RoomType("Pent-House",2000,6);
        RoomType type5 = new RoomType("Presidential Suite",1000,5);
        roomTypes.add(type);
        roomTypes.add(type1);
        roomTypes.add(type2);
        roomTypes.add(type3);
        roomTypes.add(type4);
        roomTypes.add(type5);

        Amenity amenity  = new Amenity("jaccuzi",400);
        Amenity amenity1 = new Amenity("spa",200);
        Amenity amenity2 = new Amenity("wifi",20);
        Amenity amenity3 = new Amenity("Mini-Fridge",100);
        amenities.add(amenity); amenities.add(amenity1);
        amenities.add(amenity2); amenities.add(amenity3);

        ArrayList<Amenity> a1 = new ArrayList<>();
        a1.add(amenity2);
        ArrayList<Amenity> a2 = new ArrayList<>();
        a2.add(amenity3);
        a2.add(amenity2);
        ArrayList<Amenity> a3 = new ArrayList<>();
        a3.add(amenity);
        a3.add(amenity1);
        a3.add(amenity2);
        ArrayList<Amenity> a4 = new ArrayList<>();
        a4.add(amenity1);
        a4.add(amenity2);
        a4.add(amenity3);
        ArrayList<Amenity> a5 = new ArrayList<>();
        a5.add(amenity);
        a5.add(amenity1);
        a5.add(amenity2);
        a5.add(amenity3);

        rooms.add(new Room(type,  a1, 911, 3, Room.view.SEA));
        rooms.add(new Room(type2, a2, 912, 3, Room.view.POOL));
        rooms.add(new Room(type3, a3, 913, 5, Room.view.CITY));
        rooms.add(new Room(type4, a4, 914, 5, Room.view.SEA));
        rooms.add(new Room(type5, a5, 915, 6, Room.view.POOL));

        Reservation r0 = new Reservation(guest, rooms.get(0), LocalDate.parse("2024-06-20"), LocalDate.parse("2024-06-25"));
        r0.setStatus(Reservation.Status.COMPLETED);
        Reservation r1 = new Reservation(guest, rooms.get(1), LocalDate.parse("2026-05-02"), LocalDate.parse("2026-05-02"));
        r1.setStatus(Reservation.Status.CONFIRMED);
        Reservation r2 = new Reservation(guest, rooms.get(2), LocalDate.parse("2024-06-20"), LocalDate.parse("2024-06-25"));
        r2.setStatus(Reservation.Status.PENDING);
        Reservation r3 = new Reservation(guest, rooms.get(3), LocalDate.parse("2024-06-20"), LocalDate.parse("2024-06-25"));
        r3.setStatus(Reservation.Status.PENDING);
        Reservation r4 = new Reservation(guest, rooms.get(4), LocalDate.parse("2024-06-20"), LocalDate.parse("2024-06-25"));
        r4.setStatus(Reservation.Status.PENDING);
        reservations.add(r0);
        reservations.add(r1);
        reservations.add(r2);
        reservations.add(r3);
        reservations.add(r4);
    }
    public static RoomType findRoomType(String size) {
        synchronized (roomTypes) {
            for (RoomType rt : roomTypes)
                if (rt.getSize().equals(size)) return rt;
        }
        return null;
    }

    public static Amenity findAmenity(String name) {
        synchronized (amenities) {
            for (Amenity a : amenities)
                if (a.getName().equals(name)) return a;
        }
        return null;
    }

    public static Room findRoom(int roomNumber) {
        synchronized (rooms) {
            for (Room r : rooms)
                if (r.getRoomNumber() == roomNumber) return r;
        }
        return null;
    }

    public static ArrayList<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        ArrayList<Room> available = new ArrayList<>();

            for (Room r : rooms){
                if (r.isAvailable(checkIn, checkOut)){
                    available.add(r);
                }
            }

        return available;
    }

    public static ArrayList<Reservation> getPendingReservations() {
        ArrayList<Reservation> pending = new ArrayList<>();
            for (Reservation r : reservations){
                if (r.getStatus() == Reservation.Status.PENDING) {
                    pending.add(r);
                }
            }

        return pending;
    }

    public static User searchUserByName(String name) {

            for (User u : users) {
                if (u.getUsername().equalsIgnoreCase(name)) {
                    return u;
                }
            }

        return null;
    }

    public static User searchUserByEmail(String email) {

            for (User u : users){
                if (u.getEmail().equals(email)){ return u;
                }
            }

        return null;
    }

    public static ArrayList<Guest> getPendingGuests() {
        ArrayList<Guest> guests = new ArrayList<>();

            for (Reservation r : reservations){
                if (r.getStatus() == Reservation.Status.PENDING && Receptionist.isToday(r.getCheckInDate()) && !guests.contains(r.getGuest())){
                    guests.add(r.getGuest());
                }
            }

        return guests;
    }

    public static ArrayList<Reservation> getGuestReservation(Guest guest) {
        ArrayList<Reservation> result = new ArrayList<>();
            for (Reservation r : reservations){
                if (r.getGuest() == guest){
                    result.add(r);
                }
            }

        return result;
    }

    public static ArrayList<Reservation> getGuestPendingReservation(Guest guest) {
        ArrayList<Reservation> result = new ArrayList<>();
            for (Reservation r : reservations){
                if (r.getGuest() == guest && r.getStatus() == Reservation.Status.PENDING){
                    result.add(r);
                }
            }

        return result;
    }

    public static ArrayList<Reservation> receptionistGetGuestPendingReservation(Guest guest) {
        ArrayList<Reservation> result = new ArrayList<>();
            for (Reservation r : reservations){
                if (r.getGuest() == guest && r.getStatus() == Reservation.Status.PENDING && Receptionist.isToday(r.getCheckInDate())){
                    result.add(r);}
            }

        return result;
    }

    public static ArrayList<Guest> checktodayinvoices() {
        ArrayList<Guest> guests = new ArrayList<>();
            for (Invoice inv : invoices){
                for (Reservation r : inv.getReservation()){
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
            for (User u : users){
                if (u instanceof Guest){
                    filtered.add((Guest) u);
                }
    }

        return filtered;
    }

    public static ArrayList<Receptionist> getReceptionists() {
        ArrayList<Receptionist> result = new ArrayList<>();
            for (User u : users){
                if (u instanceof Receptionist){ result.add((Receptionist) u);
                }
            }

        return result;
    }


    public static ArrayList<Room> filterRoomsByPreferences(ArrayList<Room> availableRooms, roomPreferences preferred) {
        ArrayList<Room> filtered = new ArrayList<>();
        for (Room r : availableRooms){
            if (r.getFloor() == preferred.getFloor() || r.getView() == preferred.getView()){
                filtered.add(r);
            }
        }
        return filtered;
    }

    public static ArrayList<Room> filterRoomsByPrice(double maxPrice) {
        ArrayList<Room> filtered = new ArrayList<>();
            for (Room r : rooms){
                if (r.getType().getBasePrice() <= maxPrice){ filtered.add(r);
                }
            }

        return filtered;
    }

    public static ArrayList<Room> filterRoomsByAmenities(ArrayList<Amenity> reqAmenities) {
        ArrayList<Room> filtered = new ArrayList<>();
            for (Room r : rooms){
                for (Amenity a : reqAmenities){
                    if (r.getAmenities().contains(a)) { filtered.add(r); break; }
                }
            }

        return filtered;
    }

    public static ArrayList<Room> filterRoomsByRoomType(ArrayList<RoomType> reqTypes) {
        ArrayList<Room> filtered = new ArrayList<>();
            for (Room r : rooms){
                if (reqTypes.contains(r.getType())) filtered.add(r);
            }
        return filtered;
    }
}
