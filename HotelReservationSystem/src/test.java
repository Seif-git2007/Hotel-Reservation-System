import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class test {
    static Scanner input = new Scanner(System.in);
    public static void callRegister(){
        Guest guest = new Guest();
        String name;
        String password;
        String gender;
        String view;
        double balance;
        LocalDate date;
        roomPreferences perfer = new roomPreferences();
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

        System.out.println("enter address:");
        String address = input.nextLine();
        while (true) {
            try {
                System.out.println("enter date (YYYY-MM-DD):");
                date = Authenticator.validateDate(input.nextLine());
                break;
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                System.out.println("enter preferred floor: ");
                perfer.setFloor(Authenticator.validateInteger(input.nextLine()));
                if(perfer.getFloor()<0){
                    throw new InvalidInputException("Floor cannot be negative");
                }
                break;
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
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
                boolean reserved = false;
                while (!reserved) {
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
                        Authenticator.validateReservationDates(checkInDate, checkOutDate);

                        System.out.println("Would you like to filter by your room preferences(1.Yes , 2.No , 0.Back)");
                        ArrayList<Room> rooms = new ArrayList<>();
                        int filterChoice;
                        while (true) {
                            try {
                                System.out.println("Enter choice: ");
                                filterChoice = Authenticator.validateInteger(input.nextLine());
                                if (filterChoice < 0 || filterChoice > 2) {
                                    throw new InvalidInputException("Invalid choice");
                                }
                                break;
                            } catch (InvalidInputException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        if (filterChoice == 0) break;

                        if (filterChoice == 1) {
                            guest.viewAvailableRooms(checkInDate, checkOutDate, guest.getPrefered());
                            rooms = HotelDataBase.filterRooms(HotelDataBase.getAvailableRooms(checkInDate, checkOutDate), guest.getPrefered());
                        }
                        if (filterChoice == 2) {
                            guest.viewAvailableRooms(checkInDate, checkOutDate);
                            rooms = HotelDataBase.getAvailableRooms(checkInDate, checkOutDate);
                        }

                        if (rooms.isEmpty()) {
                            System.out.println("No available rooms for these dates, please try different dates.");
                            continue;
                        }

                        System.out.println("Choose a Room");
                        while (true) {
                            try {
                                System.out.println("Enter choice: ");
                                choice = Authenticator.validateInteger(input.nextLine());
                                if (choice <= 0 || choice > rooms.size()) {
                                    throw new InvalidInputException("Invalid choice");
                                }
                                break;
                            } catch (InvalidInputException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        guest.makeReservation(rooms.get(choice - 1), checkInDate, checkOutDate);
                        reserved = true;

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
    public static void receptionistMenu(Receptionist receptionist) {
        int choice;
        while (true) {
            System.out.println("1.View Guests\n2.View Reservations\n3.View Rooms\n4.Check In Guest\n5.Check Out Guest\n0.Log Out");
            while (true) {
                try {
                    System.out.println("Enter choice: ");
                    choice = Authenticator.validateInteger(input.nextLine());
                    if (choice < -1 || choice > 5) {
                        throw new InvalidInputException("Invalid choice");
                    }
                    break;
                } catch (InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
            }
            if(choice==1){
                if (HotelDataBase.filterGuest().isEmpty()) {
                    System.out.println("No Guests available!");
                    continue;
                }
                receptionist.viewGuest();
            }
            if(choice==2){
                if (HotelDataBase.reservations.isEmpty()) {
                    System.out.println("No Reservations available!");
                    continue;
                }
                receptionist.viewReservations();
            }
            if(choice==3){
                if (HotelDataBase.rooms.isEmpty()) {
                    System.out.println("No rooms available!");
                    continue;
                }
                receptionist.viewRooms();
            }
            if (choice == 4) {
                while (true) {
                    try {
                        receptionist.viewCheckingInGuests();
                        while (true) {
                            try {
                                System.out.println("Enter choice: ");
                                choice = Authenticator.validateInteger(input.nextLine());
                                if (choice <= 0 || choice > HotelDataBase.getPendingGuests().size()) {
                                    throw new InvalidInputException("Invalid choice");
                                }
                                break;
                            } catch (InvalidInputException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        receptionist.checkIn(HotelDataBase.getPendingGuests().get(choice - 1));
                        break;
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                }
            }
            if (choice == 5) {
                while (true) {
                    try {
                        receptionist.viewCheckingOutGuests();
                        while (true) {
                            try {
                                System.out.println("Enter choice: ");
                                choice = Authenticator.validateInteger(input.nextLine());
                                if (choice <= 0 || choice > HotelDataBase.checktodayinvoices().size()) {
                                    throw new InvalidInputException("Invalid choice");
                                }
                                break;
                            } catch (InvalidInputException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        receptionist.checkOut(HotelDataBase.checktodayinvoices().get(choice - 1));
                        break;
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                }
            }
            if(choice==-1) {
                while (true){
                    try {
                        System.out.println("Enter simulated date (YYYY-MM-DD):");
                        JumpInTime.now = Authenticator.validateDate(input.nextLine());
                        System.out.println("Time set to: " + JumpInTime.now);
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
    public static void adminMenu(Admin admin) {
        int choice;

        while (true) {
            System.out.println("Admin menu\n 1. View all rooms\n 2. Add room\n 3. Remove room\n 4. Update room\n" +
                    " 5. View all room types \n 6. Add room type \n 7. Remove room type \n " +
                    "8. Update room type \n 9. View all amenities \n10. Add amenity \n11. Remove amenity\n" +
                    "12. Update amenity\n13. View all guests\n14. View all reservations\n0. logout");
            while (true) {
                try {
                    System.out.println("Enter choice: ");
                    choice = Authenticator.validateInteger(input.nextLine());
                    if (choice < 0 || choice > 14) {
                        throw new InvalidInputException("Invalid choice! Please enter a number between 0 and 14.");
                    }
                    break;
                } catch (InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
            }
            switch (choice) {
                case 0 -> {
                    System.out.println("Logging out!");
                    return;
                }
                case 1 -> {
                    if (HotelDataBase.rooms.isEmpty()) {
                        System.out.println("No rooms available!");
                        continue;
                    }
                    admin.viewRooms();
                }
                case 2 -> {
                    int roomNo, floor;
                    Room.view view;
                    RoomType type;
                    ArrayList<Amenity> amenities = new ArrayList<>();
                    while (true) {
                        try {
                            System.out.print("Please enter room number ");
                            roomNo = Authenticator.validateInteger(input.nextLine());
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    while (true) {
                        try {
                            System.out.print("Please enter floor number");
                            floor = Authenticator.validateInteger(input.nextLine());
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    while (true) {
                        try {
                            System.out.print("Enter view (SEA / POOL / CITY): ");
                            String validatingView = Authenticator.validateView(input.nextLine());
                            view = Room.view.valueOf(validatingView);//converting string to enum to use authenticator
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    System.out.println("Available room types:");
                    for (int i = 0; i < HotelDataBase.roomTypes.size(); i++) {
                        System.out.println((i + 1) + ". " + HotelDataBase.roomTypes.get(i));
                    }
                    int typeChoice;
                    while (true) {
                        try {
                            System.out.print("Choose room type number: ");
                            typeChoice = Authenticator.validateInteger(input.nextLine());
                            if (typeChoice < 1 || typeChoice > HotelDataBase.roomTypes.size())
                                throw new InvalidInputException("Invalid choice.");
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    type = HotelDataBase.roomTypes.get(typeChoice - 1);
                    System.out.println("Available Amenities: ");
                    ArrayList<Amenity> currentamenities = new ArrayList<>(HotelDataBase.amenities);
                    ;
                    while (true) {
                        int cnt = 1;
                        for (Amenity a : currentamenities) {
                            System.out.println(cnt + ". " + a.toString());
                            cnt++;
                        }
                        System.out.println("0. Don't Add");
                        while (true) {
                            try {
                                System.out.print("Choose Amenity number: ");
                                typeChoice = Authenticator.validateInteger(input.nextLine());
                                if (typeChoice < 0 || typeChoice > currentamenities.size())
                                    throw new InvalidInputException("Invalid choice.");
                                break;
                            } catch (InvalidInputException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        if (typeChoice == 0) {
                            break;
                        }
                        amenities.add(currentamenities.get(typeChoice - 1));
                        currentamenities.remove(typeChoice - 1);
                        if (currentamenities.isEmpty()) {
                            break;
                        }
                    }


                    try {
                        admin.addRoom(roomNo, floor, view, type, amenities);
                        System.out.println("Room added successfully!");
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                    }


                }
                case 3 -> {

                    admin.viewRooms();
                    int roomNo = 0;

                    while (true) {
                        try {
                            System.out.print("Please enter room you would like to remove(0 to exit) ");
                            roomNo = Authenticator.validateInteger(input.nextLine());
                            if (roomNo < 0 || roomNo > HotelDataBase.rooms.size()) {
                                throw new InvalidInputException("Invalid Choice");
                            }
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                        if (roomNo == 0) {
                            break;
                        }
                        try {
                            admin.removeRoom(roomNo - 1);
                            System.out.println("Room removed successfully!");
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }


                }

                case 4 -> {
                    int roomNo, floor;
                    Room.view view;
                    RoomType type;
                    ArrayList<Amenity> amenities = new ArrayList<>();
                    int roomchoice = 0;
                    admin.viewRooms();
                    while (true) {
                        try {
                            System.out.print("Please enter room you would like to update(0 to exit) ");
                            roomchoice = Authenticator.validateInteger(input.nextLine());
                            if (roomchoice < 0 || roomchoice > HotelDataBase.rooms.size()) {
                                throw new InvalidInputException("Invalid Choice");
                            }
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                        if (roomchoice == 0) {
                            break;
                        }

                    }


                    while (true) {
                        try {
                            System.out.print("Please enter floor number");
                            floor = Authenticator.validateInteger(input.nextLine());
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    while (true) {
                        try {
                            System.out.print("Enter view (SEA / POOL / CITY): ");
                            String validatingView = Authenticator.validateView(input.nextLine());
                            view = Room.view.valueOf(validatingView);//converting string to enum to use authenticator
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    System.out.println("Available room types:");
                    for (int i = 0; i < HotelDataBase.roomTypes.size(); i++) {
                        System.out.println((i + 1) + ". " + HotelDataBase.roomTypes.get(i));
                    }
                    int typeChoice;
                    while (true) {
                        try {
                            System.out.print("Choose room type number: ");
                            typeChoice = Authenticator.validateInteger(input.nextLine());
                            if (typeChoice < 1 || typeChoice > HotelDataBase.roomTypes.size())
                                throw new InvalidInputException("Invalid choice.");
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    type = HotelDataBase.roomTypes.get(typeChoice - 1);
                    System.out.println("Available Amenities: ");
                    ArrayList<Amenity> currentamenities = new ArrayList<>(HotelDataBase.amenities);
                    ;
                    while (true) {
                        int cnt = 1;
                        for (Amenity a : currentamenities) {
                            System.out.println(cnt + ". " + a.toString());
                            cnt++;
                        }
                        System.out.println("0. Don't Add");
                        while (true) {
                            try {
                                System.out.print("Choose Amenity number: ");
                                typeChoice = Authenticator.validateInteger(input.nextLine());
                                if (typeChoice < 0 || typeChoice > currentamenities.size())
                                    throw new InvalidInputException("Invalid choice.");
                                break;
                            } catch (InvalidInputException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        if (typeChoice == 0) {
                            break;
                        }
                        amenities.add(currentamenities.get(typeChoice - 1));
                        currentamenities.remove(typeChoice - 1);
                        if (currentamenities.isEmpty()) {
                            break;
                        }
                    }


                    try {
                        admin.updateRoom(HotelDataBase.rooms.get(roomchoice - 1).getRoomNumber(), floor, view, type, amenities, roomchoice - 1);
                        System.out.println("Room Updated successfully!");
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case 5 -> {
                    if (HotelDataBase.amenities.isEmpty()) {
                        System.out.println("No room types available!");
                        continue;
                    }
                    admin.viewRoomTypes();
                }
                case 6 -> {
                    String size;
                    int capacity;
                    double basePrice;
                    while (true) {
                        try {
                            System.out.print("Please enter room Capacity ");
                            capacity = Authenticator.validateInteger(input.nextLine());
                            if (capacity < 0 || capacity > 5) {
                                throw new InvalidInputException("Invalid room Capacity , Max is 4");
                            }
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    while (true) {
                        try {
                            System.out.println("enter room's base Price:");
                            double b = input.nextDouble();
                            input.nextLine();
                            basePrice = Authenticator.validateBalance(b);
                            break;
                        } catch (Exception e) {
                            System.out.println("Invalid base price");

                        }
                    }
                    System.out.println("enter room size(Single,Double,etc..)");
                    size = input.nextLine();
                    try {
                        admin.addRoomType(size, basePrice, capacity);
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                    }

                }
                case 7 -> {
                    admin.viewRoomTypes();

                    int roomTypeNo = 0;

                    while (true) {
                        try {
                            System.out.print("Please enter room type you would like to remove(0 to exit) ");
                            roomTypeNo = Authenticator.validateInteger(input.nextLine());
                            if (roomTypeNo < 0 || roomTypeNo > HotelDataBase.roomTypes.size()) {
                                throw new InvalidInputException("Invalid Choice");
                            }

                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                            continue;
                        }
                        if (roomTypeNo == 0) {
                            break;
                        }
                        try {
                            admin.removeRoomType(roomTypeNo - 1);
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }


                }
                case 8 ->{
                    String size;
                    int capacity;
                    double basePrice;
                    int roomTypechoice=0;
                    while (true) {
                        admin.viewRoomTypes();
                        try {
                            System.out.print("Please enter room type you would like to update(0 to exit) ");
                            roomTypechoice = Authenticator.validateInteger(input.nextLine());
                            if (roomTypechoice < 0 || roomTypechoice > HotelDataBase.roomTypes.size()) {
                                throw new InvalidInputException("Invalid Choice");
                            }
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                        if (roomTypechoice == 0) {
                            break;
                        }
                    }
                    while (true){
                        try {
                            System.out.print("Please enter room Capacity ");
                            capacity = Authenticator.validateInteger(input.nextLine());
                            if (capacity < 0 || capacity > 5) {
                                throw new InvalidInputException("Invalid room Capacity , Max is 4");
                            }
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }}
                    while (true) {
                        try {
                            System.out.println("enter room's base Price:");
                            double b = input.nextDouble();
                            input.nextLine();
                            basePrice = Authenticator.validateBalance(b);
                            break;
                        } catch (Exception e) {
                            System.out.println("Invalid base price");

                        }
                    }
                    System.out.println("enter room size(Single,Double,etc..)");
                    size = input.nextLine();
                    try {
                        admin.updateRoomTypes(size, basePrice, capacity,roomTypechoice-1);
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case 9 -> {
                    if (HotelDataBase.amenities.isEmpty()) {
                        System.out.println("No amenties available!");
                        continue;
                    }
                    admin.viewAmenities();
                }
                case 10 -> {
                    System.out.println("Enter Amenity Name :");
                    String name = input.nextLine();
                    double price = 0;
                    while (true) {
                        try {
                            System.out.println("enter Amenity Price:");
                            double b = input.nextDouble();
                            input.nextLine();
                            price = Authenticator.validateBalance(b);
                            break;
                        } catch (Exception e) {
                            System.out.println("Invalid base price");

                        }
                    }
                    try {
                        admin.addAmenity(name, price);
                    }
                    catch (InvalidInputException e){
                        System.out.println(e.getMessage());
                    }

                }

                case 11 -> {


                    int roomTypeNo = 0;

                    while (true) {
                        try {
                            admin.viewAmenities();
                            System.out.print("Please enter room type you would like to remove(0 to exit) ");
                            roomTypeNo = Authenticator.validateInteger(input.nextLine());
                            if (roomTypeNo < 0 || roomTypeNo > HotelDataBase.roomTypes.size()) {
                                throw new InvalidInputException("Invalid Choice");
                            }

                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                            continue;
                        }
                        if (roomTypeNo == 0) {
                            break;
                        }
                        try {
                            admin.removeAmenity(roomTypeNo - 1);
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                }

                case 12 -> {
                    String name;
                    double price=0;
                    int amenitChoice=0;
                    while (true) {
                        admin.viewAmenities();
                        try {
                            System.out.print("Please enter amenity you would like to update(0 to exit) ");
                            amenitChoice = Authenticator.validateInteger(input.nextLine());
                            if (amenitChoice< 0 || amenitChoice> HotelDataBase.amenities.size()) {
                                throw new InvalidInputException("Invalid Choice");
                            }
                            break;
                        } catch (InvalidInputException e) {
                            System.out.println(e.getMessage());
                        }
                        if (amenitChoice == 0) {
                            break;
                        }
                    }


                    System.out.println("enter Amenity name:");
                    name = input.nextLine();

                    while (true) {
                        try {
                            System.out.println("enter Amenity Price:");
                            double b = input.nextDouble();
                            input.nextLine();
                            price = Authenticator.validateBalance(b);
                            break;
                        } catch (Exception e) {
                            System.out.println("Invalid price");

                        }
                    }
                    try {
                        admin.updateAmenityPrice(name, price,amenitChoice-1);
                    } catch (InvalidInputException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case 13 -> {

                    if (HotelDataBase.filterGuest().isEmpty()) {
                        System.out.println("No Guests available!");
                        continue;
                    }
                    admin.viewGuest();
                }
                case 14 -> {
                    if (HotelDataBase.reservations.isEmpty()) {
                        System.out.println("No Reservations available!");
                        continue;
                    }
                    admin.viewReservations();
                }
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
                    choice = Authenticator.validateInteger(input.nextLine());
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
                    adminMenu((Admin) user);
                    //admin menu called here (DON'T DELETE THIS COMMENT)
                }
                else if(user instanceof Receptionist ){
                    System.out.println("Welcome Receptionist "+user.getUsername());
                    receptionistMenu((Receptionist) user);
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