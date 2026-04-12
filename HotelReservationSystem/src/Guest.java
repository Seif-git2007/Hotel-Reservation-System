import java.time.LocalDate;

public class Guest extends User {
    private double balance;
    private roomPreferences prefered;
    private String address;

    public Guest() {}

    public Guest(String username, String password, LocalDate dateOfBirth, double balance, roomPreferences prefered, String address) {
        super(username, password, dateOfBirth);
        this.balance = balance;
        this.prefered = prefered;
        this.address = address;
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

    public void Register(String name,String password,String gender,double balance,LocalDate date, String address, roomPreferences r) {
        this.setUsername(name);
        this.setPassword(password);
        this.gender= Gender.valueOf(gender.toUpperCase());
        this.balance=balance;
        this.setDateOfBirth(date);
        this.address = address;
        this.prefered = r;
        HotelDataBase.users.add(this);
    }
    public int viewAvailableRooms(){

        if(HotelDataBase.getAvailableRooms().isEmpty()){
            System.out.println("No rooms available\nHotel is fully Booked");
            return -1;
        }
        int cnt=1;
        for (Room r:HotelDataBase.getAvailableRooms()){
            System.out.println(cnt+". "+r);//waiting until override toString() in Room class to be done
            cnt++;
        }
        return 0;

    }
    public void makeReservation(Room room,LocalDate checkInDate,LocalDate checkOutDate)throws InvalidInputException{
        if(checkOutDate.isBefore(checkInDate)){
            throw new InvalidInputException("Check out date can't be before Check in date");
        }
        for(Reservation r:HotelDataBase.reservations){
            if(r.getGuest()==this&&checkInDate.isBefore(r.getCheckOutDate())&&checkOutDate.isAfter(r.getCheckInDate())){
                throw new InvalidInputException("Required duration overlaps with an existing reservation from "+r.getCheckInDate()+" to "+r.getCheckOutDate());
            }
        }
        Reservation reservation=new Reservation(this,room,checkInDate,checkOutDate);
        room.setAvailable(false);
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
    public void viewPendingReservations(){
        int cnt=1;
        for(Reservation r:HotelDataBase.getPendingReservations()){
            if(r.getGuest()==this){
                System.out.println(cnt+". "+r);
            }
        }
    }
    public void cancelReservation(Reservation r){
        r.setStatus(Reservation.Status.CANCELLED);
        r.getRoom().setAvailable(true);
        System.out.println("Reservation Cancelled");
    }
//    public Invoice checkOut(){
//    }
    public void pay(Invoice invoice){

    }
    @Override
    public String toString() {
        return "Guest| " +
                "name: "+getUsername()+
                "balance: " + balance +
                "| prefered: " + prefered +
                "| address: " + address  +
                "| gender=" + gender ;
    }
}