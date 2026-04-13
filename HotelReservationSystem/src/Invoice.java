import java.time.LocalDate;
import java.util.ArrayList;

public class Invoice {
    private Guest guest;
    private ArrayList<Reservation> reservation;
    private double total;
    private LocalDate paymentDate;
    public enum paymentMethod{CREDIT,CASH,ONLINE}
    private paymentMethod method;
    private boolean isPaid;


    public Invoice(Guest guest, ArrayList<Reservation> reservation, double total) {
        this.guest = guest;
        this.reservation = reservation;
        this.total = total;

    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public ArrayList<Reservation> getReservation() {
        return reservation;
    }

    public void setReservation(ArrayList<Reservation> reservation) {
        this.reservation = reservation;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public paymentMethod getMethod() {
        return method;
    }

    public void setMethod(paymentMethod method) {
        this.method = method;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

}
