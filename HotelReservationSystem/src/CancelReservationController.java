import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class CancelReservationController {

    @FXML private GuestSidebarController sidebarController;
    @FXML private VBox reservationController ;

    @FXML
    public void initialize() {
        if (sidebarController != null){
            sidebarController.btnCancelReservation.getStyleClass().add("sidebar-nav-btn-active");
        }
        renderReservations(HotelDataBase.getGuestPendingReservation((Guest) MainController.getUser()), reservationController);

    }
    public void renderReservations(ArrayList<Reservation> reservations, VBox reservationContainer) {
        reservationContainer.getChildren().clear();

        if (reservations == null || reservations.isEmpty()) {
            Label empty = new Label("You have no Pending reservations.");
            empty.setStyle("-fx-text-fill: #6B6B6B; -fx-font-size: 14px; -fx-padding: 40;");
            reservationContainer.getChildren().add(empty);
            return;
        }

        for (Reservation res : reservations) {
            VBox card = new VBox(10);
            card.getStyleClass().add("room-card");

            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);

            Label typeLabel = new Label(res.getRoom().getType().getSize().toUpperCase());
            typeLabel.getStyleClass().add("room-card-title");

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label statusBadge = new Label(res.getStatus().toString().toUpperCase());
            statusBadge.getStyleClass().addAll("status-badge", "status-" + res.getStatus().toString().toLowerCase());

            header.getChildren().addAll(typeLabel, spacer, statusBadge);

            Label detailsLabel = new Label(String.format(
                    "Room %d  •  Floor %d  •  Capacity: %d Guests",
                    res.getRoom().getRoomNumber(),
                    res.getRoom().getFloor(),
                    res.getRoom().getType().getCapacity()
            ));
            detailsLabel.getStyleClass().add("room-card-price");
            detailsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2C2C2C;");

            long nights = java.time.temporal.ChronoUnit.DAYS.between(res.getCheckInDate(), res.getCheckOutDate());
            Label dateLabel = new Label(String.format(
                    "📅 %s  →  %s  (%d Night%s)",
                    res.getCheckInDate(), res.getCheckOutDate(),
                    nights, nights == 1 ? "" : "s"
            ));
            dateLabel.getStyleClass().add("room-card-price");

            double totalPrice = nights * res.getRoom().getType().getBasePrice();
            Label totalLabel = new Label(String.format("Total Charged: $%.2f", totalPrice));
            totalLabel.getStyleClass().add("room-card-price");

            Label amenitiesLabel = new Label("Includes: " + ViewRoomsController.formatAmenities(res.getRoom().getAmenities()));
            amenitiesLabel.getStyleClass().add("main-content-hint");

            card.getChildren().addAll(header, detailsLabel, dateLabel, totalLabel, amenitiesLabel);


            Button cancelBtn = new Button("Request Cancellation");
            cancelBtn.getStyleClass().add("btn-cancel-reservation");

            cancelBtn.setOnMouseEntered(e -> {
                cancelBtn.getStyleClass().remove("btn-cancel-reservation");
                cancelBtn.getStyleClass().add("btn-cancel-reservation-hover");
            });
            cancelBtn.setOnMouseExited(e -> {
                cancelBtn.getStyleClass().remove("btn-cancel-reservation-hover");
                cancelBtn.getStyleClass().add("btn-cancel-reservation");
            });
            ActionEvent event = new ActionEvent(cancelBtn, null);
            cancelBtn.setOnAction(e -> {
                MainController.getReservationContext().setReservation(res);
                MainController.navigate(event,"CancelReservationForm.fxml");
            });

            HBox actionsRow = new HBox(cancelBtn);
            actionsRow.setAlignment(Pos.CENTER_RIGHT);
            card.getChildren().add(actionsRow);


            reservationContainer.getChildren().add(card);
        }
    }
}
