import java.time.LocalDate;

public class Receptionist extends Staff{
    public Receptionist() {
    }

    public Receptionist(String name, String password, LocalDate date,int hours) {
        super(name, password, date);
        super.setWorkingHours(hours);
    }
}
