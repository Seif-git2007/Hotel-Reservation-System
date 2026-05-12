import java.time.LocalDate;

public abstract class Staff extends User {
    private int workingHours;
    enum Role { ADMIN, RECEPTIONIST }
    Role role;

    public Staff() {}

    public Staff(String name, String password, LocalDate date, User.Gender gender, String email) {
        super(name, password, date, gender, email);
    }

    public int getWorkingHours()        { return workingHours; }
    public void setWorkingHours(int h)  { workingHours = h; }

    public void viewReservations() {
        int cnt = 1;
        System.out.println("Reservations:");
        synchronized (HotelDataBase.reservations) {
            for (Reservation r : HotelDataBase.reservations)
                System.out.println(cnt++ + ". " + r);
        }
        System.out.println();
    }

    public void viewGuest() {
        int cnt = 1;
        for (Guest u : HotelDataBase.filterGuest())
            System.out.println(cnt++ + ". " + u);
        System.out.println();
    }
}
