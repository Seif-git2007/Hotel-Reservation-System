import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.temporal.ChronoUnit;

public class AdminMenuController extends MainController implements SessionController {

    // ── Stat labels (fx:id must match Admin_Menu.fxml) ───────────────────────
    @FXML private Label labelUserCount;
    @FXML private Label labelBookingCount;
    @FXML private Label labelRevenue;
    @FXML private Label labelAvailableRooms;

    // ── Sidebar ──────────────────────────────────────────────────────────────
    @FXML private AdminSidebarController sidebarController;

    // ── Internal state ───────────────────────────────────────────────────────
    private AppSession session;
    private Admin      admin;

    // ─────────────────────────────────────────────────────────────────────────
    // SessionController entry point
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initSession(AppSession session) {
        this.session = session;
        this.admin   = (Admin) session.getCurrentUser();

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.setActive(sidebarController.btnDashboard);
        }

        refreshStats();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Stats
    // ─────────────────────────────────────────────────────────────────────────
    private void refreshStats() {
        // Total users
        if (labelUserCount != null)
            labelUserCount.setText(String.valueOf(HotelDataBase.getUsers().size()));

        // Active bookings (PENDING + CONFIRMED)
        long activeBookings = HotelDataBase.reservations.stream()
                .filter(r -> r.getStatus() == Reservation.Status.PENDING
                        || r.getStatus() == Reservation.Status.CONFIRMED)
                .count();
        if (labelBookingCount != null)
            labelBookingCount.setText(String.valueOf(activeBookings));

        // Total revenue (CONFIRMED + COMPLETED)
        double totalRevenue = HotelDataBase.reservations.stream()
                .filter(r -> r.getStatus() == Reservation.Status.COMPLETED
                        || r.getStatus() == Reservation.Status.CONFIRMED)
                .mapToDouble(r -> {
                    long nights = ChronoUnit.DAYS.between(
                            r.getCheckInDate(), r.getCheckOutDate());
                    if (nights == 0) nights = 1;
                    return nights * r.getRoom().getType().getBasePrice();
                }).sum();
        if (labelRevenue != null)
            labelRevenue.setText(String.format("$%.0f", totalRevenue));

        // Available rooms
        long occupied = HotelDataBase.reservations.stream()
                .filter(r -> r.getStatus() == Reservation.Status.CONFIRMED
                        || r.getStatus() == Reservation.Status.PENDING)
                .map(r -> r.getRoom().getRoomNumber())
                .distinct().count();
        long available = HotelDataBase.getRooms().size() - occupied;
        if (labelAvailableRooms != null)
            labelAvailableRooms.setText(String.valueOf(Math.max(0, available)));
    }
}