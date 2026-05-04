import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.temporal.ChronoUnit;

public class AdminMenuController extends MainController implements SessionController {

    @FXML private Label labelUserCount;
    @FXML private Label labelBookingCount;
    @FXML private Label labelRevenue;
    @FXML private Label labelAvailableRooms;

    @FXML private AdminSidebarController sidebarController;

    private AppSession session;
    private Admin      admin;
    private final Runnable refreshListener = this::refresh;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
        this.admin   = (Admin) session.getCurrentUser();

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.setActive(sidebarController.btnDashboard);
        }

        refresh();
        EventBus.subscribe(EventBus.Event.RESERVATION_CHANGED, refreshListener);
        EventBus.subscribe(EventBus.Event.USER_CHANGED, refreshListener);

        labelBookingCount.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                EventBus.unsubscribe(EventBus.Event.RESERVATION_CHANGED, refreshListener);
                EventBus.unsubscribe(EventBus.Event.USER_CHANGED, refreshListener);

            }
        });
    }


    private void refresh() {
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