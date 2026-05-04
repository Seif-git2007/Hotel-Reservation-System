import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class ViewReservationsController implements SessionController {

    @FXML private GuestSidebarController sidebarController;
    @FXML VBox reservationController;

    private AppSession session;
    private final Runnable refreshListener = this::refresh;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.btnViewReservations.getStyleClass().add("sidebar-nav-btn-active");
        }
        refresh();
        EventBus.subscribe(EventBus.Event.RESERVATION_CHANGED, refreshListener);
        reservationController.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                EventBus.unsubscribe(EventBus.Event.RESERVATION_CHANGED, refreshListener);
            }
        });

    }

    public void renderReservations(ArrayList<Reservation> reservations, VBox reservationContainer) {
        reservationContainer.getChildren().clear();

        if (reservations == null || reservations.isEmpty()) {
            Label empty = new Label("You have no active reservations.");
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
            statusBadge.getStyleClass().addAll("status-badge",
                "status-" + res.getStatus().toString().toLowerCase());

            header.getChildren().addAll(typeLabel, spacer, statusBadge);

            Label detailsLabel = new Label(String.format(
                "Room %d  •  Floor %d  •  Capacity: %d Guests",
                res.getRoom().getRoomNumber(), res.getRoom().getFloor(),
                res.getRoom().getType().getCapacity()));
            detailsLabel.getStyleClass().add("room-card-price");
            detailsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2C2C2C;");

            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                res.getCheckInDate(), res.getCheckOutDate());
            Label dateLabel = new Label(String.format(
                "📅 %s  →  %s  (%d Night%s)",
                res.getCheckInDate(), res.getCheckOutDate(), nights, nights == 1 ? "" : "s"));
            dateLabel.getStyleClass().add("room-card-price");

            double totalPrice =res.getRoom().calcTotal(res.getCheckInDate(), res.getCheckOutDate());
            Label totalLabel = new Label(String.format("Total Charged: $%.2f", totalPrice));
            totalLabel.getStyleClass().add("room-card-price");

            Label amenitiesLabel = new Label(
                "Includes: " + ViewRoomsController.formatAmenities(res.getRoom().getAmenities()));
            amenitiesLabel.getStyleClass().add("main-content-hint");

            card.getChildren().addAll(header, detailsLabel, dateLabel, totalLabel, amenitiesLabel);

            if (res.getStatus() == Reservation.Status.PENDING) {
                HBox actionsRow = new HBox();
                actionsRow.setAlignment(Pos.CENTER_RIGHT);
                actionsRow.setSpacing(10);

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

                Button confirmBtn = new Button("Confirm");
                confirmBtn.getStyleClass().add("btn-confirm-inline");

                Button abortBtn = new Button("Abort");
                abortBtn.getStyleClass().add("btn-abort-inline");

                cancelBtn.setOnAction(e -> {
                    actionsRow.getChildren().clear();
                    actionsRow.getChildren().addAll(abortBtn, confirmBtn);
                });
                abortBtn.setOnAction(e -> {
                    actionsRow.getChildren().clear();
                    actionsRow.getChildren().add(cancelBtn);
                });

                confirmBtn.setOnAction(e -> {
                    Alert confirmPopup = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmPopup.setTitle("Confirm Cancellation");
                    confirmPopup.setHeaderText("Wait!");
                    confirmPopup.setContentText("Are you sure you want to permanently cancel this reservation?");

                    if (confirmPopup.showAndWait().get() == ButtonType.OK) {
                        session.getCurrentGuest().cancelReservation(res);
                        refresh();
                        System.out.println("User confirmed they definitely want to cancel.");
                    }else {
                        refresh();
                    }
                });

                actionsRow.getChildren().add(cancelBtn);
                card.getChildren().add(actionsRow);
            }

                reservationContainer.getChildren().add(card);
        }
    }
    public void refresh(){
        reservationController.getChildren().clear();
        renderReservations(HotelDataBase.getGuestReservation((Guest) session.getCurrentUser()), reservationController);
    }
}

