public class Authenticator {
    public static String validateName(String name) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty.");
        }
        for (User u : HotelDataBase.users) {
            if (u.getUsername().equals(name)) {
                throw new InvalidInputException("Username already exists.");
            }
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

    public static double  validateBalance(double balance) throws InvalidInputException {
        if (balance < 0) {
            throw new InvalidInputException("Balance cannot be negative.");
        }
        return balance;
    }
}
