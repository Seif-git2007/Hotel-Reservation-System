import java.time.LocalDate;
import java.util.Scanner;

public class test {
    public static void callRegister(){
        Scanner input = new Scanner(System.in);
        Guest guest = new Guest();
        String name;
        String password;
        String gender;
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
        System.out.println("enter preferred view: ");
        System.out.println("1.SEA\n2.POOL\n3.CITY");
        int choice = input.nextInt();
        while (choice<1||choice>3){
            System.out.println("invalid choice, please enter a valid number: ");
            perfer.setView(choice);
        }
        guest.Register(name,password,gender,balance,date, address, perfer);
        System.out.println("Registration Successful!");
    }



    public static void main(String[] args) {
        callRegister();
    }

}