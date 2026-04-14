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
    public void viewReservations(){
        int cnt=1;
        for(Reservation r:HotelDataBase.reservations){
                System.out.println(cnt+"."+r);
                cnt++;
        }
    }
    public void viewGuest(){
        int cnt=1;
        for(Guest u:HotelDataBase.filterGuest()){
                System.out.println(cnt+"."+u);
                cnt++;
            }
        }



}
