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
