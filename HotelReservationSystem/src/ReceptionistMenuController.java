import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class ReceptionistMenuController implements SessionController {

    @FXML private ReceptionistSidebarController sidebarController;
    @FXML private Label lblCheckInCount;
    @FXML private Label lblCheckOutCount;
    @FXML private Label lblReservationCount;
    @FXML private VBox  reservationContainer;

    private AppSession   session;
    private Receptionist receptionist;

    @Override
    public void initSession(AppSession session) {
        this.session      = session;
        this.receptionist = (Receptionist) session.getCurrentUser();

        if (sidebarController != null) {
            sidebarController.initSession(session);
            // highlight Dashboard button as active
            sidebarController.btnDashboard.getStyleClass().add("sidebar-nav-btn-active");
        }

        refreshCounts();
        renderAllReservations();
    }

    private void refreshCounts() {
        lblCheckInCount.setText(String.valueOf(HotelDataBase.getPendingGuests().size()));
        lblCheckOutCount.setText(String.valueOf(HotelDataBase.checktodayinvoices().size()));
        lblReservationCount.setText(String.valueOf(HotelDataBase.reservations.size()));
    }

    // Renders every reservation as a room-card — same card style used across ViewReservations,
    // CancelReservation, MakeReservation etc.
    private void renderAllReservations() {
        reservationContainer.getChildren().clear();

        ArrayList<Reservation> all = new ArrayList<>(HotelDataBase.reservations);

        if (all.isEmpty()) {
            Label empty = new Label("No reservations on record.");
            empty.getStyleClass().add("main-content-hint");
            reservationContainer.getChildren().add(empty);
            return;
        }

        for (Reservation res : all) {
            VBox card = new VBox(10);
            card.getStyleClass().add("room-card");

            // Header: guest name (left) + status badge (right)
            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);

            Label guestLabel = new Label(res.getGuest().getUsername().toUpperCase());
            guestLabel.getStyleClass().add("room-card-title");

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label statusBadge = new Label(res.getStatus().toString().toUpperCase());
            statusBadge.getStyleClass().addAll("status-badge",
                    "status-" + res.getStatus().toString().toLowerCase());

            header.getChildren().addAll(guestLabel, spacer, statusBadge);

            // Room details row
            Label roomLabel = new Label(String.format(
                    "Room %d  •  %s  •  Floor %d  •  Capacity: %d",
                    res.getRoom().getRoomNumber(),
                    res.getRoom().getType().getSize(),
                    res.getRoom().getFloor(),
                    res.getRoom().getType().getCapacity()));
            roomLabel.getStyleClass().add("room-card-price");
            roomLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2C2C2C;");

            // Date + nights row
            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                    res.getCheckInDate(), res.getCheckOutDate());
            Label dateLabel = new Label(String.format(
                    "📅 %s  →  %s  (%d Night%s)",
                    res.getCheckInDate(), res.getCheckOutDate(),
                    nights, nights == 1 ? "" : "s"));
            dateLabel.getStyleClass().add("room-card-price");

            card.getChildren().addAll(header, roomLabel, dateLabel);
            reservationContainer.getChildren().add(card);
        }
    }
}
