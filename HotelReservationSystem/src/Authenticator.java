import java.time.LocalDate;

public class Authenticator {
    public static String validateName(String name) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty.");
        }
        User u = HotelDataBase.searchUser(name);
        if(u==null){
            return name;
        }
        if (u.getUsername().equals(name)) {
                throw new InvalidInputException("Username already exists.");
            }
        return name;
    }

    public static String validatePassword(String password) throws InvalidInputException {
        if (password == null || password.length() <= 8) {
            throw new InvalidInputException("Password must be longer than 8 characters.");
        }
        return password;
    }

    public static String validateGender(String g) throws InvalidInputException {
        String gender= g.toUpperCase();
        if (gender.equals(User.Gender.MALE.toString()) || gender.equals(User.Gender.FEMALE.toString())) {
            return gender;
        } else {
            throw new InvalidInputException("Gender must be exactly MALE or FEMALE.");
        }
    }
    public static String validateView(String v) throws InvalidInputException {
        String view= v.toUpperCase();
        if (view.equals(Room.view.CITY.toString()) || view.equals(Room.view.SEA.toString())||view.equals(Room.view.POOL.toString())) {
            return view;
        } else {
            throw new InvalidInputException("View can only be CITY,SEA and POOL");
        }
    }

    public static double  validateBalance(double balance) throws InvalidInputException {
        if (balance < 0) {
            throw new InvalidInputException("Balance cannot be negative.");
        }
        return balance;
    }
    public static int validateInteger(String input) throws InvalidInputException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Please enter a valid number");
        }
    }
    public static LocalDate validateDate(String dateStr) throws InvalidInputException {
        if (!dateStr.matches("\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])")) {
            throw new InvalidInputException("Invalid date format, please use YYYY-MM-DD");
        }
        return LocalDate.parse(dateStr);
    }
    public static void validateReservationDates(LocalDate checkInDate,LocalDate checkOutDate)throws InvalidInputException{
        if(checkOutDate.isBefore(checkInDate)){
            throw new InvalidInputException("Check out date can't be before Check in date");
        }

    }
}
