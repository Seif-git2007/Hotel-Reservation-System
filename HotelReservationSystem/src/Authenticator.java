import java.time.LocalDate;

public class Authenticator {
    public static String validateName(String name) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new AuthenticationException("Please Enter User name.");
        }
        User u = HotelDataBase.searchUserByName(name);
        if(u==null){
            return name;
        }
        if (u.getUsername().toUpperCase().equals(name.toUpperCase())) {
                throw new AuthenticationException("Username already exists.");
            }
        return name;
    }
    public static String validateEmail(String email) throws InvalidInputException {
        if (email == null || email.trim().isEmpty()) {
            throw new AuthenticationException("Please Enter Email");
        }
        User u = HotelDataBase.searchUserByEmail(email);
        if(u==null){
            return email;
        }
        if (u.getUsername().toUpperCase().equals(email.toUpperCase())) {
            throw new AuthenticationException("Username already exists.");
        }
        return email;
    }

    public static String validatePassword(String password) throws InvalidInputException {
        if (password == null || password.length() <= 8) {
            throw new AuthenticationException("Password must be longer than 8 characters.");
        }
        return password;
    }

    public static String validateGender(String g) throws InvalidInputException {
        String gender= g.toUpperCase();
        if (gender.equals(User.Gender.MALE.toString()) || gender.equals(User.Gender.FEMALE.toString())) {
            return gender;
        } else {
            throw new AuthenticationException("Gender must be exactly MALE or FEMALE.");
        }
    }
    public static String validateView(String v) throws InvalidInputException {
        String view= v.toUpperCase();
        if (view.equals(Room.view.CITY.toString()) || view.equals(Room.view.SEA.toString())||view.equals(Room.view.POOL.toString())) {
            return view;
        } else {
            throw new AuthenticationException("View can only be CITY,SEA and POOL");
        }
    }

    public static double  validateBalance(double balance) throws InvalidInputException {
        if (balance < 0) {
            throw new AuthenticationException("Balance cannot be negative.");
        }
        return balance;
    }
    public static int validateInteger(String input) throws InvalidInputException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new AuthenticationException("Please enter a valid number");
        }
    }
    public static LocalDate validateDate(String dateStr) throws InvalidInputException {
        if (!dateStr.matches("\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])")) {
            throw new AuthenticationException("Invalid date format, please use YYYY-MM-DD");
        }
        return LocalDate.parse(dateStr);
    }
    public static LocalDate validateBirthDate(LocalDate date) throws InvalidInputException {
        if(date==null){
            throw new AuthenticationException("Please enter your birth date");
        }
        if(date.isAfter(JumpInTime.now.minusYears(17))){
            throw new AuthenticationException("You must be at least 17 years old to register.");
        }
        if (date.isBefore(JumpInTime.now)) {
            throw new AuthenticationException("Date can't be before current date");
        }
        return date;
    }
    public static void validateReservationDates(LocalDate checkInDate,LocalDate checkOutDate)throws InvalidInputException{
        if(checkInDate==null||checkOutDate==null){
            throw new AuthenticationException("Please fill in both dates");
        }
        if(checkOutDate.isBefore(JumpInTime.now)||checkInDate.isBefore(JumpInTime.now)){
            throw new AuthenticationException("Date can't be before current date");
        }
        if(checkOutDate.isBefore(checkInDate)){
            throw new AuthenticationException("Check out date can't be before Check in date");
        }

    }
}
