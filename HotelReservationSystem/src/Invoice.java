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
    public String toSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════╗\n");
        sb.append("║           CHECKOUT SUMMARY                   ║\n");
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Guest     : %-31s ║\n", guest.getUsername()));
        sb.append("╠══════════════════════════════════════════════╣\n");
        for (Reservation r : reservation) {
            sb.append(String.format("║  Room      : %-31s ║\n", r.getRoom().getRoomNumber()));
            sb.append(String.format("║  Type      : %-31s ║\n", r.getRoom().getType().getSize()));
            sb.append(String.format("║  Check-in  : %-31s ║\n", r.getCheckInDate()));
            sb.append(String.format("║  Check-out : %-31s ║\n", r.getCheckOutDate()));
            long days = java.time.temporal.ChronoUnit.DAYS.between(r.getCheckInDate(), r.getCheckOutDate());
            sb.append(String.format("║  Nights    : %-31s ║\n", days));
            sb.append(String.format("║  Rate      : $%-30s ║\n", r.getRoom().getType().getBasePrice() + "/night"));
            if (!r.getRoom().getAmenities().isEmpty()) {
                sb.append("║  Amenities :                                 ║\n");
                for (Amenity a : r.getRoom().getAmenities()) {
                    sb.append(String.format("║    - %-39s ║\n", a.getName() + " $" + a.getPrice()));
                }
            }
            sb.append("╠══════════════════════════════════════════════╣\n");
        }
        sb.append(String.format("║  TOTAL DUE : $%-30.2f ║\n", total));
        sb.append("╚══════════════════════════════════════════════╝");
        return sb.toString();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════╗\n");
        sb.append("║                   INVOICE                    ║\n");
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Guest     : %-31s ║\n", guest.getUsername()));
        sb.append(String.format("║  Date      : %-31s ║\n", paymentDate));
        sb.append(String.format("║  Method    : %-31s ║\n", method));
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append("║  RESERVATIONS                                ║\n");
        sb.append("╠══════════════════════════════════════════════╣\n");
        for (Reservation r : reservation) {
            sb.append(String.format("║  Room      : %-31s ║\n", r.getRoom().getRoomNumber()));
            sb.append(String.format("║  Type      : %-31s ║\n", r.getRoom().getType().getSize()));
            sb.append(String.format("║  Check-in  : %-31s ║\n", r.getCheckInDate()));
            sb.append(String.format("║  Check-out : %-31s ║\n", r.getCheckOutDate()));
            long days = java.time.temporal.ChronoUnit.DAYS.between(r.getCheckInDate(), r.getCheckOutDate());
            sb.append(String.format("║  Nights    : %-31s ║\n", days));
            sb.append(String.format("║  Rate      : $%-30s ║\n", r.getRoom().getType().getBasePrice() + "/night"));
            if (!r.getRoom().getAmenities().isEmpty()) {
                sb.append("║  Amenities :                                 ║\n");
                for (Amenity a : r.getRoom().getAmenities()) {
                    sb.append(String.format("║    - %-39s ║\n", a.getName() + " $" + a.getPrice()));
                }
            }
            sb.append("╠══════════════════════════════════════════════╣\n");
        }
        sb.append(String.format("║  TOTAL     : $%-30.2f ║\n", total));
        sb.append(String.format("║  Status    : %-31s ║\n", isPaid ? "PAID " : "UNPAID "));
        sb.append("╚══════════════════════════════════════════════╝");
        return sb.toString();
    }
}
