import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Receptionist extends Staff{
    public Receptionist() {
    }

    public Receptionist(String name, String password, LocalDate date,int hours,User.Gender gender) {
        super(name, password, date,gender);
        super.setWorkingHours(hours);
    }
    public static boolean isToday(LocalDate date) {
        return date.equals(JumpInTime.now);
    }
    public void viewCheckingInGuests()throws InvalidInputException{
        List<Guest> guests = HotelDataBase.getPendingGuests();
        if (guests.isEmpty()) {
            throw new InvalidInputException("No guests are checking In today.");
        }
        int cnt=1;
        for (Guest g: guests) {
            System.out.println(cnt+". " + g);
            cnt++;
        }

    }
    public void checkIn(Guest guest) throws InvalidInputException {
        ArrayList<Reservation> reservations = HotelDataBase.getGuestReservation(guest);
        if (reservations.isEmpty())
            throw new InvalidInputException("no reservations today " );
        for (Reservation r:reservations) {
            r.setStatus(Reservation.Status.CONFIRMED);
        }
        System.out.println("the reservation is confirmed");
    }
    public void viewCheckingOutGuests()throws InvalidInputException{
        List<Guest> guests = HotelDataBase.checktodayinvoices();
        if (guests.isEmpty()) {
            throw new InvalidInputException("No guests are checking out today.");
        }
        int cnt=1;
        for (Guest g: guests) {
            System.out.println(cnt+". " + g);
            cnt++;
        }
    }
    public void checkOut(Guest guest) {

        for (Reservation r : HotelDataBase.reservations) {
            if (r.getGuest() == guest && r.getStatus() == Reservation.Status.CONFIRMED) {
                r.setStatus(Reservation.Status.COMPLETED);
            }
        }
        System.out.println("Checkout completed " );
    }

}
