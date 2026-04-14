import java.time.LocalDate;

public abstract class Staff extends User{
    private int workingHours;
    enum Role {ADMIN ,RECEPTIONIST}
    Role role;
    public Staff(){}
    public Staff(String name, String password, LocalDate date,User.Gender gender){
        super(name,password,date,gender);
    }

    public int getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }
}
