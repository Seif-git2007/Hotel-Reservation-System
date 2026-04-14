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
    private boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }
    public ArrayList<Guest> checkIn() {
        ArrayList<Guest> guests = new ArrayList<>();
        for (Reservation r : HotelDataBase.reservations) {
            if (r.getStatus() == Reservation.Status.PENDING
                    && isToday(r.getCheckInDate())
            ) {
                guests.add(r.getGuest());
            }
        }
        return guests;
    }

    public ArrayList<Reservation> getGuestreservation(Guest guest) {
        ArrayList<Reservation> reservations = new ArrayList<>();
        for (Reservation r : HotelDataBase.reservations) {
            if (r.getGuest() == guest
                    && r.getStatus() == Reservation.Status.PENDING
                    && isToday(r.getCheckInDate())) {
                reservations.add(r);
            }
        }
        return reservations;
    }


    public void checkIn(Guest guest, int index) throws InvalidInputException {
        ArrayList<Reservation> reservations = getGuestreservation(guest);
        if (reservations.isEmpty())
            throw new InvalidInputException("no reservations today " );
        if (index < 1 || index > reservations.size())
            throw new InvalidInputException("Invalid selection.");

        reservations.get(index - 1).setStatus(Reservation.Status.CONFIRMED);
        System.out.println("the reservation is confirmed");
    }


// de func el checkout h loop 3la el invoice w ashof el guest saheb el invoice w akhaly el confirmed completed



public ArrayList<Guest> checktodayinvoices() {
    ArrayList<Guest> guests = new ArrayList<>();
// de hatshof lw el guest mawgowd lw msh mawgod add
    for (Invoice inv : HotelDataBase.invoices) {
        for(Reservation r : inv.getReservation())

        if (r.getStatus() == Reservation.Status.CONFIRMED
                && !guests.contains(inv.getGuest())) {
            guests.add(inv.getGuest());
        }


    }

    return guests;
    }

    public void checkOut(Guest guest) {
        for (Reservation r : HotelDataBase.reservations) {
            if (r.getGuest() == guest && r.getStatus() == Reservation.Status.CONFIRMED) {
                r.setStatus(Reservation.Status.COMPLETED);
                System.out.println("Checkout completed " );
                return;
            }
        }

    }









}
