import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class ReceptionistCheckInController implements SessionController {

    @FXML private ReceptionistSidebarController sidebarController;
    @FXML private VBox checkInContainer;

    private AppSession   session;
    private Receptionist receptionist;
    private final Runnable refreshListener = this::refresh;

    @Override
    public void initSession(AppSession session) {
        this.session      = session;
        this.receptionist = (Receptionist) session.getCurrentUser();

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.btnCheckIn.getStyleClass().add("sidebar-nav-btn-active");
        }
        refresh();
        EventBus.subscribe(EventBus.Event.RESERVATION_CHANGED, refreshListener);
        checkInContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                EventBus.unsubscribe(EventBus.Event.RESERVATION_CHANGED, refreshListener);
            }
        });

    }
    private void refresh() {
        checkInContainer.getChildren().clear();
        renderGuests();
    }
    private void renderGuests() {
        checkInContainer.getChildren().clear();

        ArrayList<Guest> guests = HotelDataBase.getPendingGuests();

        if (guests.isEmpty()) {
            Label empty = new Label("No guests are checking in today.");
            empty.getStyleClass().add("main-content-hint");
            empty.setStyle("-fx-padding: 40;");
            checkInContainer.getChildren().add(empty);
            return;
        }

        for (Guest guest : guests) {
            VBox card = new VBox(10);
            card.getStyleClass().add("room-card");

            // Header row — name left, email right
            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(guest.getUsername().toUpperCase());
            nameLabel.getStyleClass().add("room-card-title");

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label emailLabel = new Label(guest.getEmail());
            emailLabel.getStyleClass().add("room-card-status");

            header.getChildren().addAll(nameLabel, spacer, emailLabel);

            // Pending reservations for today
            ArrayList<Reservation> pending =
                    HotelDataBase.receptionistGetGuestPendingReservation(guest);

            for (Reservation res : pending) {
                long nights = java.time.temporal.ChronoUnit.DAYS.between(
                        res.getCheckInDate(), res.getCheckOutDate());
                Label resLabel = new Label(String.format(
                        "Room %d  •  %s  •  %s → %s  (%d Night%s)",
                        res.getRoom().getRoomNumber(),
                        res.getRoom().getType().getSize(),
                        res.getCheckInDate(), res.getCheckOutDate(),
                        nights, nights == 1 ? "" : "s"));
                resLabel.getStyleClass().add("room-card-price");
                resLabel.setStyle("-fx-text-fill: #2C2C2C; -fx-font-weight: bold;");
                card.getChildren().add(resLabel);
            }

            Button checkInBtn = new Button("\u2714  Confirm Check-In");
            checkInBtn.getStyleClass().add("btn-select-room");

            checkInBtn.setOnAction(e -> {
                try {
                    receptionist.checkIn(guest);
                    renderGuests();
                } catch (InvalidInputException ex) {
                    showAlert(ex.getMessage());
                }
            });

            HBox actions = new HBox(checkInBtn);
            actions.setAlignment(Pos.CENTER_RIGHT);

            card.getChildren().addAll(header, actions);
            checkInContainer.getChildren().add(card);
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.showAndWait();
    }
}
