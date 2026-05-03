import java.time.LocalDate;
import java.util.ArrayList;

public class Receptionist extends Staff {
    public Receptionist() {}

    public Receptionist(String name, String password, LocalDate date,
                        int hours, User.Gender gender, String email) {
        super(name, password, date, gender, email);
        super.setWorkingHours(hours);
    }

    public static boolean isToday(LocalDate date) {
        return date.equals(JumpInTime.now);
    }

    public void viewCheckingInGuests() throws InvalidInputException {
        ArrayList<Guest> guests = HotelDataBase.getPendingGuests();
        if (guests.isEmpty()){
            throw new InvalidInputException("No guests are checking in today.");
        }
        int cnt = 1;
        for (Guest g : guests){
            System.out.println(cnt++ + ". " + g);
        }
    }

    public void checkIn(Guest guest) throws InvalidInputException {
        ArrayList<Reservation> reservations = HotelDataBase.receptionistGetGuestPendingReservation(guest);
        if (reservations.isEmpty()){
            throw new InvalidInputException("No reservations today");
        }
        for (Reservation r : reservations){
            r.setStatus(Reservation.Status.CONFIRMED);
            DataBaseManager.runAsync(() -> {
                DataBaseManager.updateReservationStatus(r);
                EventBus.fire(EventBus.Event.RESERVATION_CHANGED);
            });
        }
        System.out.println("Guest checked in successfully");
    }

    public void viewCheckingOutGuests() throws InvalidInputException {
        ArrayList<Guest> guests = HotelDataBase.checktodayinvoices();
        if (guests.isEmpty()){
            throw new InvalidInputException("No guests are checking out today.");
        }
        int cnt = 1;
        for (Guest g : guests){
            System.out.println(cnt++ + ". " + g);
        }
    }

    public void checkOut(Guest guest) {
        synchronized (HotelDataBase.reservations) {
            for (Reservation r : HotelDataBase.reservations){
                if (r.getGuest() == guest && r.getStatus() == Reservation.Status.AWAITING_CONFIRMATION){
                    r.setStatus(Reservation.Status.COMPLETED);
                    DataBaseManager.runAsync(() -> {
                        DataBaseManager.updateReservationStatus(r);
                        EventBus.fire(EventBus.Event.RESERVATION_CHANGED);
                    });
                }
            }
        }
        System.out.println("Checkout completed");
    }

    @Override
    public String toString() {
        return "Name: " + getUsername() + " | Birthday: " + getDateOfBirth()
                + " | Gender: " + getGender() + " | Working hours: " + getWorkingHours();
    }
}
