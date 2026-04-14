import java.time.LocalDate;
import java.util.Scanner;

public class testseif {
    static Scanner input = new Scanner(System.in);
    public static void callRegister(){
        Guest guest = new Guest();
        String name;
        String password;
        String gender;
        String view;
        double balance;
        while (true) {
            try {
                System.out.println("enter name:");
                name=Authenticator.validateName(input.nextLine());
                break;
            }
            catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                System.out.println("enter password:");
                password=Authenticator.validatePassword(input.nextLine());

                break;
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }


        while (true) {
            try {
                System.out.println("enter gender:");
                gender=Authenticator.validateGender(input.nextLine());
                break;
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }


        while (true) {
            try {
                System.out.println("enter balance:");
                double b = input.nextDouble();
                input.nextLine();
                balance=Authenticator.validateBalance(b);
                break;
            } catch (Exception e) {
                System.out.println("Invalid balance.");

            }
        }
        System.out.println("enter date (YYYY-MM-DD):");
        LocalDate date = LocalDate.parse(input.nextLine());
        System.out.println("enter address:");
        String address = input.nextLine();
        roomPreferences perfer = new roomPreferences();
        System.out.println("enter preferred floor: ");
        perfer.setFloor(input.nextInt());
        input.nextLine();
        while (true){
            try{
                System.out.println("enter preferred view(SEA - POOL - CITY): ");

                view=Authenticator.validateView(input.nextLine());
                perfer.setView(view);
                break;
            }
            catch (InvalidInputException e){
                System.out.println(e.getMessage());
            }
        }
        guest.Register(name,password,gender,balance,date, address, perfer);
        System.out.println("Registration Successful!");
    }
    public static User callLogin(User user){
        Scanner input = new Scanner(System.in);

        while(true){
            try{
                System.out.println("Enter username:");
                String name =input.nextLine();
                System.out.println("Enter password:");
                String password=input.nextLine();
                user= User.Login(name,password);
                break;
            }
            catch(InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
        return user;
    }
    public static void guestMenu(Guest guest) {
        int choice;
        Invoice invoice = null;
        Invoice.paymentMethod method = null;
        System.out.println("Welcome Guest " + guest.getUsername());
        while (true) {
            System.out.println("1.View Rooms\n2.Make Reservation\n3.View Reservations\n4.Cancel Reservation\n5.CheckOut\n0.Log Out");
            while (true) {
                try {
                    System.out.println("Enter choice: ");
                    choice = Authenticator.validateInteger(input.nextLine());
                    if (choice < 0 || choice > 5) {
                        throw new InvalidInputException("Invalid choice");
                    }
                    break;
                } catch (InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (choice == 1) {
                guest.viewRooms();
            }
            if (choice == 2) {
                LocalDate checkInDate;
                LocalDate checkOutDate;
                while (true) {
                    try {
                        while (true) {
                            try {
                                System.out.println("Enter check in date: ");
                                checkInDate = Authenticator.validateDate(input.nextLine());
                                break;
                            } catch (InvalidInputException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        while (true) {
                            try {
                                System.out.println("Enter check out date");
                                checkOutDate = Authenticator.validateDate(input.nextLine());
                                break;
                            } catch (InvalidInputException e) {
                                System.out.println(e.getMessage());
                            }
                        }

                        guest.viewAvailableRooms(checkInDate, checkOutDate);
                        System.out.println("Choose a Room");
                        while (true) {
                            try {
                                System.out.println("Enter choice: ");
                                choice = Authenticator.validateInteger(input.nextLine());
                                if (choice <= 0 || choice > HotelDataBase.getAvailableRooms(checkInDate, checkOutDate).size()) {
                                    throw new InvalidInputException("Invalid choice");
                                }
                                break;
                            } catch (InvalidInputException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        guest.makeReservation(HotelDataBase.getAvailableRooms(checkInDate, checkOutDate).get(choice - 1), checkInDate, checkOutDate);
                        break;
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                    }

                }
            }
            if (choice == 3) {
                guest.viewReservations();
            }
            if (choice == 4) {
                guest.viewPendingReservations();
                System.out.println("Choose a Room");
                while (true) {
                    try {
                        System.out.println("Enter choice: ");
                        choice = Authenticator.validateInteger(input.nextLine());
                        if (choice <= 0 || choice > HotelDataBase.getPendingReservations().size()) {
                            throw new InvalidInputException("Invalid choice");
                        }
                        break;
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                    }
                }
                guest.cancelReservation(HotelDataBase.getPendingReservations().get(choice - 1));

            }
            if (choice == 5) {

                while (true) {
                    try {
                        invoice = guest.checkOut();
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    System.out.println("Enter payment Method\n1.Cash\n2.Credit Card\n3.Online Balance");
                    try {
                        System.out.println("Enter choice: ");
                        choice = Authenticator.validateInteger(input.nextLine());
                        if (choice < 1 || choice > 3) {
                            throw new InvalidInputException("Invalid choice");
                        }
                        if (choice == 1) {
                            method = Invoice.paymentMethod.CASH;
                        }
                        if (choice == 2) {
                            method = Invoice.paymentMethod.CREDIT;
                        }
                        if (choice == 3) {
                            method = Invoice.paymentMethod.ONLINE;
                        }

                        guest.pay(invoice, method);
                        break;
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            if (choice == 0) {
                break;
            }
        }

    }
    public static void main(String[] args) {
        User user=null;
        Scanner input=new Scanner(System.in);
        System.out.println("Welcome to Kempinski Hotel");
        while(true) {
            System.out.println("1.Register\n2.Login\n0.Exit");
            int choice;
            while (true){//validate choice
                try {
                    System.out.println("Enter choice: ");
                    choice =input.nextInt();
                    if(choice<0||choice>2){
                        throw new InvalidInputException("Invalid choice");
                    }
                    break;

                }catch (InvalidInputException e){
                    System.out.println(e.getMessage());
                }
            }
            if(choice==2){
                user=callLogin(user);
                if(user instanceof Guest){
                    System.out.println("Welcome Guest "+user.getUsername());
                    guestMenu((Guest) user);

                }
                else if(user instanceof Admin){
                    System.out.println("Welcome Admin "+user.getUsername());
                    //admin menu called here (DON'T DELETE THIS COMMENT)
                }
                else if(user instanceof Receptionist ){
                    System.out.println("Welcome Receptionist "+user.getUsername());

                    //Receptionist menu called here (DON'T DELETE THIS COMMENT)
                }
            }
            if(choice==1){
                callRegister();
            }
            if(choice==0){
                break;
            }
        }
    }
}