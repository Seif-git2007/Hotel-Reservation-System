import java.time.LocalDate;

public abstract class User {
    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private String email;
    public enum Gender {MALE, FEMALE}

    ;
    Gender gender;

    User() {
    }

    public User(String username, String password, LocalDate dateOfBirth,Gender gender,String email) {
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender=gender;
        this.email=email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(Gender gender){
        this.gender = gender;
    }
    public Gender getGender(){
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static User Login(String username, String password) throws InvalidInputException {
        User u =HotelDataBase.searchUserByName(username);
        if (u!=null&&u.getPassword().equals(password)) {
            System.out.println("Login Successful");
            return u;
        }
        throw new AuthenticationException("Account not found");
    }
    public void viewRooms(){
        int cnt=1;
        for (Room r:HotelDataBase.rooms){
            System.out.println(cnt+". "+r);
            cnt++;
        }
        System.out.println();
    }
}