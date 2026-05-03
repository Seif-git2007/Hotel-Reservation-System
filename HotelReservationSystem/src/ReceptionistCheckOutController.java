import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class ReceptionistCheckOutController implements SessionController {

    @FXML private ReceptionistSidebarController sidebarController;
    @FXML private VBox checkOutContainer;

    private AppSession   session;
    private Receptionist receptionist;
    private final Runnable refreshListener = this::refresh;
    @Override
    public void initSession(AppSession session) {
        this.session      = session;
        this.receptionist = (Receptionist) session.getCurrentUser();

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.btnCheckOut.getStyleClass().add("sidebar-nav-btn-active");
        }
        refresh();
        EventBus.subscribe(EventBus.Event.USER_CHANGED, refreshListener);
        EventBus.subscribe(EventBus.Event.INVOICE_CHANGED, refreshListener);
        checkOutContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                EventBus.unsubscribe(EventBus.Event.USER_CHANGED, refreshListener);
                EventBus.unsubscribe(EventBus.Event.INVOICE_CHANGED, refreshListener);
            }
        });
    }
    public void refresh(){
        renderGuests();
    }
    private void renderGuests() {
        checkOutContainer.getChildren().clear();

        ArrayList<Guest> guests = HotelDataBase.checktodayinvoices();

        if (guests.isEmpty()) {
            Label empty = new Label("No guests are checking out today.");
            empty.getStyleClass().add("main-content-hint");
            empty.setStyle("-fx-padding: 40;");
            checkOutContainer.getChildren().add(empty);
            return;
        }

        for (Guest guest : guests) {
            VBox card = new VBox(10);
            card.getStyleClass().add("room-card");

            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(guest.getUsername().toUpperCase());
            nameLabel.getStyleClass().add("room-card-title");

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label emailLabel = new Label(guest.getEmail());
            emailLabel.getStyleClass().add("room-card-status");

            header.getChildren().addAll(nameLabel, spacer, emailLabel);

            // Show the reservations about to complete
            for (Reservation res : HotelDataBase.getGuestReservation(guest)) {
                if (res.getStatus().equals(Reservation.Status.AWAITING_CONFIRMATION) ) {
                    System.out.println("i looped");
                    long nights = java.time.temporal.ChronoUnit.DAYS.between(
                            res.getCheckInDate(), res.getCheckOutDate());
                    double total = res.getRoom().calcTotal(res.getCheckInDate(), res.getCheckOutDate());
                    Label resLabel = new Label(String.format(
                            "Room %d  •  %s  •  %d Night%s  •  Total: $%.2f",
                            res.getRoom().getRoomNumber(),
                            res.getRoom().getType().getSize(),
                            nights, nights == 1 ? "" : "s",
                            total));
                    resLabel.getStyleClass().add("room-card-price");
                    resLabel.setStyle("-fx-text-fill: #2C2C2C; -fx-font-weight: bold;");
                    card.getChildren().add(resLabel);
                }
            }

            Button checkOutBtn = new Button("✔  Confirm Check-Out");
            checkOutBtn.getStyleClass().add("btn-select-room");
            checkOutBtn.setOnAction(e -> {
                receptionist.checkOut(guest);
                renderGuests();
            });

            HBox actions = new HBox(checkOutBtn);
            actions.setAlignment(Pos.CENTER_RIGHT);

            card.getChildren().addAll(header, actions);
            checkOutContainer.getChildren().add(card);
        }
    }
}
