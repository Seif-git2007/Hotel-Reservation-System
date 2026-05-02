import java.time.LocalDate;

public abstract class User {
    private String    username;
    private String    password;
    private LocalDate dateOfBirth;
    private String    email;
    public enum Gender { MALE, FEMALE }
    Gender gender;
    boolean loggedIn=false;
    User() {}

    public User(String username, String password, LocalDate dateOfBirth, Gender gender, String email) {
        this.username    = username;
        this.password    = password;
        this.dateOfBirth = dateOfBirth;
        this.gender      = gender;
        this.email       = email;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String    getUsername()                { return username; }
    public void      setUsername(String u)        { username = u; }
    public String    getPassword()                { return password; }
    public void      setPassword(String p)        { password = p; }
    public LocalDate getDateOfBirth()             { return dateOfBirth; }
    public void      setDateOfBirth(LocalDate d)  { dateOfBirth = d; }
    public void      setGender(Gender g)          { gender = g; }
    public Gender    getGender()                  { return gender; }
    public String    getEmail()                   { return email; }
    public void      setEmail(String e)           { email = e; }

    public static User Login(String username, String password) throws InvalidInputException {
        User u = HotelDataBase.searchUserByName(username);
        if (u != null && u.getPassword().equals(password)) {
            System.out.println("Login Successful");
            return u;
        }
        throw new AuthenticationException("Account not found");
    }

    public void viewRooms() {
        int cnt = 1;
        synchronized (HotelDataBase.rooms) {
            for (Room r : HotelDataBase.rooms)
                System.out.println(cnt++ + ". " + r);
        }
        System.out.println();
    }
}
