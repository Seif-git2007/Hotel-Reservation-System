import java.time.LocalDate;
import java.util.ArrayList;

public class HotelDataBase {
     static ArrayList<User> users =new ArrayList<>();
     static ArrayList<Reservation> reservations =new ArrayList<>();
     static ArrayList<Invoice> invoices =new ArrayList<>();
     static ArrayList<Room> rooms=new ArrayList<>();
     static ArrayList<RoomType> roomTypes =new ArrayList<>();
     public static void dummyData(){
          Admin admin=new Admin("1","1", LocalDate.parse("2000-12-05"),8);
          users.add(admin);
          Receptionist receptionist=new Receptionist("2","2",LocalDate.parse("2002-10-03"),10);
          users.add(receptionist);
     }



}
