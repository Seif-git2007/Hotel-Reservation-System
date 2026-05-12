import java.time.LocalDate;

public class ReservationContext {
    private Room        selectedRoom;
    private LocalDate   checkInDate;
    private LocalDate   checkOutDate;
    private Reservation reservation;

    public Reservation getReservation()              { return reservation; }
    public void        setReservation(Reservation r) { reservation = r; }

    public Room getSelectedRoom()                    { return selectedRoom; }
    public void setSelectedRoom(Room r)              { selectedRoom = r; }

    public LocalDate getCheckInDate()                { return checkInDate; }
    public void      setCheckInDate(LocalDate d)     { checkInDate = d; }

    public LocalDate getCheckOutDate()               { return checkOutDate; }
    public void      setCheckOutDate(LocalDate d)    { checkOutDate = d; }

    public void clear() {
        selectedRoom = null;
        checkInDate  = null;
        checkOutDate = null;
        reservation  = null;
    }
}
