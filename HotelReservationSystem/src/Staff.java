import java.time.LocalDate;

public abstract class Staff extends User{
    private int workingHours;
    public Staff(){}
    public Staff(String name, String password, LocalDate date){
        super(name,password,date);
    }

    public int getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }
}
