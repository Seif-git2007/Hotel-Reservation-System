import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class ReceptionistReservationsController implements SessionController {

    @FXML private ReceptionistSidebarController sidebarController;
    @FXML private VBox reservationContainer;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.btnReservations.getStyleClass().add("sidebar-nav-btn-active");
        }

        renderReservations();
    }

    // This is the same card-render logic used in ViewReservationsController and
    // CancelReservationController — kept consistent on purpose
    private void renderReservations() {
        reservationContainer.getChildren().clear();

        ArrayList<Reservation> all = new ArrayList<>(HotelDataBase.reservations);

        if (all.isEmpty()) {
            Label empty = new Label("No reservations on record.");
            empty.getStyleClass().add("main-content-hint");
            empty.setStyle("-fx-padding: 40;");
            reservationContainer.getChildren().add(empty);
            return;
        }

        for (Reservation res : all) {
            VBox card = new VBox(10);
            card.getStyleClass().add("room-card");

            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);

            // Guest name as title
            Label guestLabel = new Label(res.getGuest().getDisplayname());
            guestLabel.getStyleClass().add("room-card-title");

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Status badge — reuses the existing status-* CSS classes from style.css
            Label statusBadge = new Label(res.getStatus().toString().toUpperCase());
            statusBadge.getStyleClass().addAll("status-badge",
                    "status-" + res.getStatus().toString().toLowerCase());

            header.getChildren().addAll(guestLabel, spacer, statusBadge);

            Label roomLabel = new Label(String.format(
                    "Room %d  •  %s  •  Floor %d  •  Capacity: %d Guests",
                    res.getRoom().getRoomNumber(),
                    res.getRoom().getType().getSize(),
                    res.getRoom().getFloor(),
                    res.getRoom().getType().getCapacity()));
            roomLabel.getStyleClass().add("room-card-price");
            roomLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2C2C2C;");

            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                    res.getCheckInDate(), res.getCheckOutDate());
            Label dateLabel = new Label(String.format(
                    "📅 %s  →  %s  (%d Night%s)",
                    res.getCheckInDate(), res.getCheckOutDate(),
                    nights, nights == 1 ? "" : "s"));
            dateLabel.getStyleClass().add("room-card-price");

            double total = res.getRoom().calcTotal(res.getCheckInDate(), res.getCheckOutDate());
            Label totalLabel = new Label(String.format("Total: $%.2f", total));
            totalLabel.getStyleClass().add("room-card-price");

            Label amenitiesLabel = new Label(
                    "Includes: " + ViewRoomsController.formatAmenities(res.getRoom().getAmenities()));
            amenitiesLabel.getStyleClass().add("main-content-hint");

            card.getChildren().addAll(header, roomLabel, dateLabel, totalLabel, amenitiesLabel);
            reservationContainer.getChildren().add(card);
        }
    }
}
