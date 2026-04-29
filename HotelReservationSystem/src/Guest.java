import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Guest extends User {
    private double balance;
    private roomPreferences prefered;
    private String address;
    private String displayname;

    public Guest() {}

    public Guest(String username, String password, LocalDate dateOfBirth, double balance, roomPreferences prefered, String address,User.Gender gender,String displayName,String email) {
        super(username, password, dateOfBirth,gender,email);
        this.balance = balance;
        this.prefered = prefered;
        this.address = address;
        this.displayname=displayName;


    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public roomPreferences getPrefered() {
        return prefered;
    }

    public void setPrefered(roomPreferences prefered) {
        this.prefered = prefered;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public void Register(String name, String password, String gender, double balance, LocalDate date, String address, roomPreferences r, String displayname, String email) {
        this.setUsername(name);
        this.setPassword(password);
        this.gender= Gender.valueOf(gender.toUpperCase());
        this.balance=balance;
        this.setDateOfBirth(date);
        this.address = address;
        this.prefered = r;
        this.displayname=displayname;
        this.setEmail(email);
        HotelDataBase.users.add(this);
    }

    public void viewAvailableRooms(LocalDate checkInDate,LocalDate checkOutDate)throws InvalidInputException{
        if(HotelDataBase.getAvailableRooms(checkInDate,checkOutDate).isEmpty()){
            throw new RoomNotAvailableException("No available rooms in this duration");
        }
        int cnt=1;
        for (Room r:HotelDataBase.getAvailableRooms(checkInDate,checkOutDate)){
            System.out.println(cnt+". "+r);
            cnt++;
        }
    }
    public void viewAvailableRooms(LocalDate checkInDate,LocalDate checkOutDate,roomPreferences preferred)throws InvalidInputException{
        if(HotelDataBase.getAvailableRooms(checkInDate,checkOutDate).isEmpty()){
            throw new RoomNotAvailableException("No available rooms in this duration");
        }
        if(HotelDataBase.filterRoomsByPreferences(HotelDataBase.getAvailableRooms(checkInDate,checkOutDate),preferred).isEmpty()){
            throw new RoomNotAvailableException("No available rooms with your preferences in this duration");
        }
        int cnt=1;
        for (Room r:HotelDataBase.filterRoomsByPreferences(HotelDataBase.getAvailableRooms(checkInDate,checkOutDate),preferred)){
            System.out.println(cnt+". "+r);
            cnt++;
        }
    }
    public void makeReservation(Room room,LocalDate checkInDate,LocalDate checkOutDate){

        Reservation reservation=new Reservation(this,room,checkInDate,checkOutDate);
//        reservation.setStatus(Reservation.Status.CONFIRMED); //will be deleted when Receptionist check in function is made
        HotelDataBase.reservations.add(reservation);
        System.out.println("Reservation is made successfully");
    }
    public void viewReservations(){
        int cnt=1;
        for (Reservation r:HotelDataBase.reservations){
            if(r.getGuest()==this){
                System.out.println(cnt+". "+r);
                cnt++;
            }
        }
    }
    public ArrayList<Reservation> viewPendingReservations(){
        int cnt=1;
        ArrayList<Reservation> pending=new ArrayList<>();
        for(Reservation r:HotelDataBase.getPendingReservations()){
            if(r.getGuest()==this){
                System.out.println(cnt+". "+r);
                pending.add(r);
                cnt++;
            }
        }
        return pending;
    }
    public void cancelReservation(Reservation r){
        r.setStatus(Reservation.Status.CANCELLED);
        System.out.println("Reservation Cancelled");
    }
    public Invoice checkOut()throws InvalidInputException{
         ArrayList<Reservation> confirmed=new ArrayList<>();
        double total=0;
        double amenityTotal=0;
        long daysStayed=0;

        for (Reservation r:HotelDataBase.reservations){
            if(r.getGuest()==this&&r.getStatus()== Reservation.Status.CONFIRMED){
                confirmed.add(r);

            }
        }
        if(confirmed.isEmpty()){
            throw new InvalidPaymentException("You are not checked in");
        }
        for(Reservation r:confirmed){
            if(r.getCheckOutDate().isAfter(JumpInTime.now)){
                throw new InvalidPaymentException("You can't check out before your check out date");
            }
        }
        for(Reservation r:confirmed) {
            amenityTotal=0;
            for (Amenity a : r.getRoom().getAmenities()) {
                amenityTotal += a.getPrice();
            }
            daysStayed=ChronoUnit.DAYS.between(r.getCheckInDate(),r.getCheckOutDate())==0?1:ChronoUnit.DAYS.between(r.getCheckInDate(),r.getCheckOutDate());
            total+= ((daysStayed*r.getRoom().getType().getBasePrice())+amenityTotal);
        }
        for(Reservation r:confirmed){
            r.setStatus(Reservation.Status.AWAITING_CONFIRMATION);
        }
        Invoice invoice=new Invoice(this,confirmed,total);
        HotelDataBase.invoices.add(invoice);
        System.out.println(invoice.toSummary());
        return invoice;
    }
    public void pay(Invoice invoice,Invoice.paymentMethod method) throws InvalidInputException{
        if(method==Invoice.paymentMethod.ONLINE){
            if(balance<invoice.getTotal()){
                throw new InvalidPaymentException("Insufficient balance , Please choose another method");
            }
            this.balance-=invoice.getTotal();
        }
        invoice.setPaymentDate(JumpInTime.now);
        invoice.setPaid(true);
        invoice.setMethod(method);
        System.out.println("Payment Done Successfully");
        System.out.println("Awaiting Receptionist Confirmation");
        System.out.println(invoice);
    }
    @Override
    public String toString() {
        return "Guest: " + getUsername()+
                " | balance: " + balance +"$"+
                " | prefered: " + prefered +
                " | address: " + address  +
                " | gender:" + gender ;
    }
}