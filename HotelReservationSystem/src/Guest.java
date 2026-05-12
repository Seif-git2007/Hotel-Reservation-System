import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Guest extends User {
    private double balance;
    private roomPreferences prefered;
    private String address;
    private String displayname;
    private boolean overDue;
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

    public boolean isOverDue() {
        return overDue;
    }

    public void setOverDue(boolean overDue) {
        this.overDue = overDue;
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
        DataBaseManager.runAsync(() -> {
            DataBaseManager.saveUser(this);
            EventBus.fire(EventBus.Event.USER_CHANGED);
        });
    }

    public void viewAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate) throws InvalidInputException {
        ArrayList<Room> available = HotelDataBase.getAvailableRooms(checkInDate, checkOutDate);
        if (available.isEmpty()){
            throw new RoomNotAvailableException("No available rooms in this duration");
        }
        int cnt = 1;
        for (Room r : available){
            System.out.println(cnt++ + ". " + r);
        }
    }

    public void viewAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate,
                                    roomPreferences preferred) throws InvalidInputException {
        ArrayList<Room> available = HotelDataBase.getAvailableRooms(checkInDate, checkOutDate);
        if (available.isEmpty()){
            throw new RoomNotAvailableException("No available rooms in this duration");
        }
        ArrayList<Room> filtered = HotelDataBase.filterRoomsByPreferences(available, preferred);
        if (filtered.isEmpty()){
            throw new RoomNotAvailableException("No available rooms with your preferences");
        }
        int cnt = 1;
        for (Room r : filtered){
            System.out.println(cnt++ + ". " + r);
        }
    }

    public void makeReservation(Room room, LocalDate checkInDate, LocalDate checkOutDate, String specialRequests) {
        Reservation reservation = new Reservation(this, room, checkInDate, checkOutDate);
        reservation.setSpecialRequests(specialRequests);
        HotelDataBase.reservations.add(reservation);
        DataBaseManager.runAsync(() -> {
            DataBaseManager.saveReservation(reservation);
            EventBus.fire(EventBus.Event.RESERVATION_CHANGED);
        });
        System.out.println("Reservation is made successfully");
    }

    public void viewReservations() {
        int cnt = 1;
        synchronized (HotelDataBase.reservations) {
            for (Reservation r : HotelDataBase.reservations){
                if (r.getGuest() == this){
                    System.out.println(cnt++ + ". " + r);
                }
            }
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
        DataBaseManager.runAsync(() -> {
            DataBaseManager.updateReservationStatus(r);
            EventBus.fire(EventBus.Event.RESERVATION_CHANGED);
        });
        System.out.println("Reservation Cancelled");
    }

    public Invoice checkOut() throws InvalidInputException {
        ArrayList<Reservation> confirmed = new ArrayList<>();
        boolean flag=false;
        for (Reservation r : HotelDataBase.reservations) {
            if (r.getGuest() == this && r.getStatus() == Reservation.Status.CONFIRMED) {
                if(!r.getCheckOutDate().isAfter(JumpInTime.now)){
                    confirmed.add(r);
                    System.out.println(r);
                }
            }
        }
        if (confirmed.isEmpty()) throw new InvalidInputException("You Can't Check out now");


        double total = 0;
        for (Reservation r : confirmed) {
            long nights = ChronoUnit.DAYS.between(r.getCheckInDate(), r.getCheckOutDate());
            if (nights == 0) nights = 1;
            double basePrice    = r.getRoom().getType().getBasePrice();
            double amenityTotal = 0;
            for (Amenity a : r.getRoom().getAmenities()) amenityTotal += a.getPrice();
            double normalCharge = nights * basePrice;
            long   daysLate     = ChronoUnit.DAYS.between(r.getCheckOutDate(), JumpInTime.now);
            double lateFee      = daysLate > 0 ? (normalCharge + amenityTotal) * 0.20 : 0;
            total += normalCharge + amenityTotal + lateFee;
        }

        Invoice invoice = new Invoice(this, confirmed, total);
        System.out.println(invoice.toSummary());
        return invoice;
    }

    public void pay(Invoice invoice, Invoice.paymentMethod method, VisaCard cardinfo) throws InvalidInputException {
        if (method == Invoice.paymentMethod.ONLINE) {
            if (balance < invoice.getTotal()){
                throw new InvalidInputException("Insufficient balance, please choose another method");
            }
            this.balance -= invoice.getTotal();
            DataBaseManager.runAsync(() -> {
                DataBaseManager.saveUser(this);
                EventBus.fire(EventBus.Event.USER_CHANGED);
            });

        }
        if (method == Invoice.paymentMethod.CREDIT) {
            invoice.setCardInfo(cardinfo);
        }
        ArrayList<Reservation> confirmed = new ArrayList<>();
        for (Reservation r : HotelDataBase.reservations) {
            if (r.getGuest() == this && r.getStatus() == Reservation.Status.CONFIRMED) {
                confirmed.add(r);
            }
        }
        for (Reservation r : confirmed){
            r.setStatus(Reservation.Status.AWAITING_CONFIRMATION);
            DataBaseManager.runAsync(() -> {
                DataBaseManager.updateReservationStatus(r);
                EventBus.fire(EventBus.Event.RESERVATION_CHANGED);
            });
        }

        invoice.setPaymentDate(JumpInTime.now);
        invoice.setPaid(true);
        invoice.setMethod(method);
        HotelDataBase.invoices.add(invoice);
        DataBaseManager.runAsync(() -> {
            DataBaseManager.saveInvoice(invoice);
            EventBus.fire(EventBus.Event.INVOICE_CHANGED);
        });
        System.out.println("Payment Done Successfully\nAwaiting Receptionist Confirmation");
        System.out.println(invoice);
    }

    @Override
    public String toString() {
        return "Guest: " + getUsername() + " | balance: " + balance + "$ | preferred: "
                + prefered + " | address: " + address + " | gender: " + gender;
    }
}
