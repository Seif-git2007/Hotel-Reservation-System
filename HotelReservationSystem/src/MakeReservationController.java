import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class MakeReservationController {

    @FXML private GuestSidebarController sidebarController;
    @FXML DatePicker checkInDate;
    @FXML DatePicker checkOutDate;
    @FXML VBox roomContainer;
    @FXML Label errorLabel;

    @FXML
    public void initialize() {
        if (sidebarController != null)
            sidebarController.btnMakeReservation.getStyleClass().add("sidebar-nav-btn-active");
    }
    public void renderRooms(ArrayList<Room> rooms, VBox roomContainer, ActionEvent event) {
        roomContainer.getChildren().clear();

        if (rooms.isEmpty()) {
            Label empty = new Label("No Available rooms in this duration");
            empty.setStyle("-fx-text-fill: #6B6B6B; -fx-font-size: 14px; -fx-padding: 40;");
            roomContainer.getChildren().add(empty);
            return;
        }

        for (Room room : rooms) {
            VBox card = new VBox(10);
            card.getStyleClass().add("room-card");

            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);

            Label typeLabel = new Label(room.getType().getSize().toUpperCase());
            typeLabel.getStyleClass().add("room-card-title");

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label viewLabel = new Label("✨ " + room.getView() + " VIEW");
            viewLabel.getStyleClass().add("room-card-status");

            header.getChildren().addAll(typeLabel, spacer, viewLabel);

            Label detailsLabel = new Label(String.format(
                    "Room %d • Floor %d • Capacity: %d Guests",
                    room.getRoomNumber(), room.getFloor(), room.getType().getCapacity()
            ));
            detailsLabel.getStyleClass().add("room-card-price");
            detailsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2C2C2C;");

            Label priceLabel = new Label(String.format("$%.2f per night", room.getType().getBasePrice()));
            priceLabel.getStyleClass().add("room-card-price");

            Label amenitiesLabel = new Label("Includes: " + ViewRoomsController.formatAmenities(room.getAmenities()));
            amenitiesLabel.getStyleClass().add("main-content-hint");

            Button selectBtn = new Button("Reserve This Room");
            selectBtn.getStyleClass().add("btn-select-room");
            selectBtn.setMaxWidth(Double.MAX_VALUE);
            selectBtn.setOnAction(e -> {
                System.out.println("Selected Room: " + room.getRoomNumber());
                MainController.setReservationContext(room, checkInDate.getValue(), checkOutDate.getValue());
                MainController.navigate(event,"Reservation_Form.fxml");
            });
            card.getChildren().addAll(header, detailsLabel, priceLabel, amenitiesLabel, selectBtn);
            roomContainer.getChildren().add(card);
        }
    }
    public void search(ActionEvent event) {
        MainController.clearErrors(errorLabel);

        try{
            Authenticator.validateReservationDates(checkInDate.getValue(),checkOutDate.getValue());
            renderRooms(HotelDataBase.getAvailableRooms(checkInDate.getValue(), checkOutDate.getValue()), roomContainer,event);

        } catch (InvalidInputException e) {
            MainController.setFieldError(errorLabel,e.getMessage());
        }
    }
}
