import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Receptionist extends Staff{
    public Receptionist() {
    }

    public Receptionist(String name, String password, LocalDate date,int hours) {
        super(name, password, date);
        super.setWorkingHours(hours);
    }
    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
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

    public void checkOut(Guest guest) {
        for (Reservation r : HotelDataBase.reservations) {
            if (r.getGuest() == guest && r.getStatus() == Reservation.Status.CONFIRMED) {
                r.setStatus(Reservation.Status.COMPLETED);
            }
        }
        System.out.println("Checkout completed " );
    }









}
