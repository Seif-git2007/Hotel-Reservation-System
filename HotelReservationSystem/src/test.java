import java.time.LocalDate;
import java.util.Scanner;

public class test {
    public static void callRegister(){
        Scanner input = new Scanner(System.in);
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
                    //guest menu
                }
                else if(user instanceof Admin){
                    System.out.println("Welcome Admin "+user.getUsername());
                    //admin menu
                }
                else if(user instanceof Receptionist ){
                    System.out.println("Welcome Receptionist "+user.getUsername());
                    //Receptionist menu
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