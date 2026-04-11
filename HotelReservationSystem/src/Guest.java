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
}