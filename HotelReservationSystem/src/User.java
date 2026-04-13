import java.time.LocalDate;

public abstract class User {
    private String username;
    private String password;
    private LocalDate dateOfBirth;

    public enum Gender {MALE, FEMALE}

    ;
    Gender gender;

    User() {
    }

    public User(String username, String password, LocalDate dateOfBirth) {
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
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

    public static User Login(String username, String password) throws InvalidInputException {
        User u =HotelDataBase.searchUser(username);
        if (u!=null&&u.getPassword().equals(password)) {
            System.out.println("Login Successful");
            return u;
        }
        throw new InvalidInputException("Account not found");
    }
}