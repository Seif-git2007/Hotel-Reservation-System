import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Guest extends User {
    private double balance;
    private roomPreferences prefered;
    private String address;
    private String displayname;

    public Guest() {}

    public Guest(String username, String password, LocalDate dateOfBirth, double balance,
                 roomPreferences prefered, String address, User.Gender gender,
                 String displayName, String email) {
        super(username, password, dateOfBirth, gender, email);
        this.balance = balance;
        this.prefered = prefered;
        this.address = address;
        this.displayname = displayName;
    }

    public String getDisplayname()              { return displayname; }
    public void setDisplayname(String d)        { displayname = d; }
    public double getBalance()                  { return balance; }
    public void setBalance(double b)            { balance = b; }
    public roomPreferences getPrefered()        { return prefered; }
    public void setPrefered(roomPreferences p)  { prefered = p; }
    public String getAddress()                  { return address; }
    public void setAddress(String a)            { address = a; }

    public void Register(String name, String password, String gender, double balance,
                         LocalDate date, String address, roomPreferences r,
                         String displayname, String email) {
        setUsername(name);
        setPassword(password);
        this.gender = Gender.valueOf(gender.toUpperCase());
        this.balance = balance;
        setDateOfBirth(date);
        this.address = address;
        this.prefered = r;
        this.displayname = displayname;
        setEmail(email);
        HotelDataBase.users.add(this);
    }

    public void viewAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate) throws InvalidInputException {
        ArrayList<Room> available = HotelDataBase.getAvailableRooms(checkInDate, checkOutDate);
        if (available.isEmpty()) throw new RoomNotAvailableException("No available rooms in this duration");
        int cnt = 1;
        for (Room r : available) System.out.println(cnt++ + ". " + r);
    }

    public void viewAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate,
                                    roomPreferences preferred) throws InvalidInputException {
        ArrayList<Room> available = HotelDataBase.getAvailableRooms(checkInDate, checkOutDate);
        if (available.isEmpty()) throw new RoomNotAvailableException("No available rooms in this duration");
        ArrayList<Room> filtered = HotelDataBase.filterRoomsByPreferences(available, preferred);
        if (filtered.isEmpty()) throw new RoomNotAvailableException("No available rooms with your preferences");
        int cnt = 1;
        for (Room r : filtered) System.out.println(cnt++ + ". " + r);
    }

    public void makeReservation(Room room, LocalDate checkInDate, LocalDate checkOutDate, String specialRequests) {
        Reservation reservation = new Reservation(this, room, checkInDate, checkOutDate);
        reservation.setSpecialRequests(specialRequests);
        HotelDataBase.reservations.add(reservation);
        System.out.println("Reservation is made successfully");
    }

    public void viewReservations() {
        int cnt = 1;
        synchronized (HotelDataBase.reservations) {
            for (Reservation r : HotelDataBase.reservations)
                if (r.getGuest() == this) System.out.println(cnt++ + ". " + r);
        }
    }

    public ArrayList<Reservation> viewPendingReservations() {
        ArrayList<Reservation> pending = new ArrayList<>();
        int cnt = 1;
        for (Reservation r : HotelDataBase.getPendingReservations()) {
            if (r.getGuest() == this) {
                System.out.println(cnt++ + ". " + r);
                pending.add(r);
            }
        }
        return pending;
    }

    public void cancelReservation(Reservation r) {
        r.setStatus(Reservation.Status.CANCELLED);
        System.out.println("Reservation Cancelled");
    }

    public Invoice checkOut() throws InvalidInputException {
        ArrayList<Reservation> confirmed = new ArrayList<>();
        double total = 0;
            for (Reservation r : HotelDataBase.reservations) {
                if (r.getGuest() == this && r.getStatus() == Reservation.Status.CONFIRMED) {
                    confirmed.add(r);
                }
            }
        if (confirmed.isEmpty()) {
            throw new InvalidInputException("You are not checked in");
        }
        for (Reservation r : confirmed)
            if (!r.getCheckOutDate().equals(JumpInTime.now))
                throw new InvalidInputException("You can't check out before your check out date");

        for (Reservation r : confirmed){
            total += r.getRoom().calcTotal(r.getCheckInDate(), r.getCheckOutDate());
        }
        for (Reservation r : confirmed){
            r.setStatus(Reservation.Status.AWAITING_CONFIRMATION);
        }

        Invoice invoice = new Invoice(this, confirmed, total);
        HotelDataBase.invoices.add(invoice);
        System.out.println(invoice.toSummary());
        return invoice;
    }

    public void pay(Invoice invoice, Invoice.paymentMethod method,VisaCard cardinfo) throws InvalidInputException {
        if (method == Invoice.paymentMethod.ONLINE) {
            if (balance < invoice.getTotal())
                throw new InvalidInputException("Insufficient balance, please choose another method");
            this.balance -= invoice.getTotal();
        }
        if (method == Invoice.paymentMethod.CREDIT) {
            invoice.setCardInfo(cardinfo);
        }
        invoice.setPaymentDate(JumpInTime.now);
        invoice.setPaid(true);
        invoice.setMethod(method);
        System.out.println("Payment Done Successfully\nAwaiting Receptionist Confirmation");
        System.out.println(invoice);
    }

    @Override
    public String toString() {
        return "Guest: " + getUsername() + " | balance: " + balance + "$ | preferred: "
                + prefered + " | address: " + address + " | gender: " + gender;
    }
}
