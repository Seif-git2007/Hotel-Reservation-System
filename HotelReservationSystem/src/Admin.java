import java.time.LocalDate;

public class Admin extends Staff{
    public Admin() {
    }

    public Admin(String name, String password, LocalDate date,int hours) {
        super(name, password, date);
        super.setWorkingHours(hours);
    }
}
