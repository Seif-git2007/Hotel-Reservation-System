import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class ViewRoomsController {

    @FXML private GuestSidebarController sidebarController;

    @FXML private Button           btnToggleFilter;
    @FXML private HBox             filterBar;
    @FXML private ComboBox<String> filterCategory;
    @FXML private StackPane        filterToolsStack;
    @FXML private HBox             toolPrice;
    @FXML private FlowPane         toolAmenities;
    @FXML private FlowPane         toolRoomType;
    @FXML private Slider           priceSlider;
    @FXML private Label            lblPriceValue;
    @FXML private VBox             roomContainer;

    private boolean filterBarVisible = false;

    @FXML
    public void initialize() {
        ActionEvent event = new ActionEvent(btnToggleFilter, null);

        if (sidebarController != null) sidebarController.btnViewRooms.getStyleClass().add("sidebar-nav-btn-active");
        renderRooms(HotelDataBase.rooms,roomContainer,event);

        filterCategory.getItems().addAll("Price", "Amenities", "Room Type","Preferences");


        buildAmenityTags();
        buildRoomTypeTags();


        filterCategory.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    hideAllToolPanes();
                    if (newVal == null) return;
                    switch (newVal) {
                        case "Price"     -> { toolPrice.setVisible(true);     toolPrice.setManaged(true); }
                        case "Amenities" -> { toolAmenities.setVisible(true); toolAmenities.setManaged(true); }
                        case "Room Type" -> { toolRoomType.setVisible(true);  toolRoomType.setManaged(true); }

                    }
                }
        );


        priceSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                lblPriceValue.setText((int) newVal.doubleValue() + "$")
        );
    }


    private void buildAmenityTags() {
        toolAmenities.getChildren().clear();
        ArrayList<Amenity> allAmenities = new ArrayList<>();
        for (Room room : HotelDataBase.rooms) {
            for (Amenity amenity : room.getAmenities()) {

                boolean alreadyAdded = allAmenities.stream()
                        .anyMatch(a -> a.getName().equals(amenity.getName()));
                if (!alreadyAdded) {
                    allAmenities.add(amenity);
                }
            }
        }


        for (Amenity amenity : allAmenities) {
            Button tag = new Button(amenity.getName());
            tag.getStyleClass().add("filter-tag");

            tag.setUserData(amenity);

            tag.setOnAction(e -> toggleTag(tag));

            toolAmenities.getChildren().add(tag);
        }
    }


    private void buildRoomTypeTags() {
        toolRoomType.getChildren().clear();

        ArrayList<RoomType> allTypes = new ArrayList<>();
        for (Room room : HotelDataBase.rooms) {
            RoomType type = room.getType();
            boolean alreadyAdded = allTypes.stream()
                    .anyMatch(t -> t.getSize().equals(type.getSize()));
            if (!alreadyAdded) {
                allTypes.add(type);
            }
        }

        for (RoomType type : allTypes) {
            Button tag = new Button(type.getSize());
            tag.getStyleClass().add("filter-tag");

            tag.setUserData(type);

            tag.setOnAction(e -> toggleTag(tag));

            toolRoomType.getChildren().add(tag);
        }
    }

    private void toggleTag(Button tag) {
        if (tag.getStyleClass().contains("filter-tag-selected")) {

            tag.getStyleClass().remove("filter-tag-selected");
            tag.getStyleClass().add("filter-tag");
        } else {
            tag.getStyleClass().remove("filter-tag");
            tag.getStyleClass().add("filter-tag-selected");
        }
    }

    @FXML
    private void applyFilter(ActionEvent event) {
        String category = filterCategory.getValue();
        if (category == null) return;

        switch (category) {

            case "Price" -> {
                double maxPrice = priceSlider.getValue();

                renderRooms(HotelDataBase.filterRoomsByPrice(maxPrice),roomContainer,event);
            }

            case "Amenities" -> {

                ArrayList<Amenity> selectedAmenities = new ArrayList<>();

                for (var node : toolAmenities.getChildren()) {
                    Button tag = (Button) node;
                    if (tag.getStyleClass().contains("filter-tag-selected")) {

                        selectedAmenities.add((Amenity) tag.getUserData());
                    }
                }

                if (selectedAmenities.isEmpty()) {

                    renderRooms(HotelDataBase.rooms,roomContainer,event);
                } else {
                    renderRooms(HotelDataBase.filterRoomsByAmenities(selectedAmenities),roomContainer,event);
                }
            }

            case "Room Type" -> {

                ArrayList<RoomType> selectedTypes = new ArrayList<>();

                for (var node : toolRoomType.getChildren()) {
                    Button tag = (Button) node;
                    if (tag.getStyleClass().contains("filter-tag-selected")) {

                        selectedTypes.add((RoomType) tag.getUserData());
                    }
                }

                if (selectedTypes.isEmpty()) {
                    renderRooms(HotelDataBase.rooms,roomContainer,event);
                } else {

                    renderRooms(HotelDataBase.filterRoomsByRoomType(selectedTypes),roomContainer,event);
                }
            }
            case "Preferences"->{
                renderRooms(HotelDataBase.filterRoomsByPreferences(HotelDataBase.rooms,((Guest)MainController.getUser()).getPrefered()),roomContainer,event);
            }
        }
    }


    @FXML
    private void toggleFilterBar(ActionEvent event) {
        filterBarVisible = !filterBarVisible;
        filterBar.setVisible(filterBarVisible);
        filterBar.setManaged(filterBarVisible);
        btnToggleFilter.setText(filterBarVisible ? "✕  Close" : "⚙  Filters");
        if (!filterBarVisible) resetFilter(event);
    }


    @FXML
    private void handleResetFilter(ActionEvent event) {
        resetFilter(event);
    }

    private void resetFilter(ActionEvent event) {
        filterCategory.getSelectionModel().clearSelection();
        priceSlider.setValue(2500);
        lblPriceValue.setText("2500$");
        hideAllToolPanes();

        for (var node : toolAmenities.getChildren()) {
            Button tag = (Button) node;
            tag.getStyleClass().remove("filter-tag-selected");
            if (!tag.getStyleClass().contains("filter-tag"))
                tag.getStyleClass().add("filter-tag");
        }

        for (var node : toolRoomType.getChildren()) {
            Button tag = (Button) node;
            tag.getStyleClass().remove("filter-tag-selected");
            if (!tag.getStyleClass().contains("filter-tag"))
                tag.getStyleClass().add("filter-tag");
        }

        renderRooms(HotelDataBase.rooms,roomContainer,event);
    }

    private void hideAllToolPanes() {
        toolPrice.setVisible(false);
        toolPrice.setManaged(false);
        toolAmenities.setVisible(false);
        toolAmenities.setManaged(false);
        toolRoomType.setVisible(false);
        toolRoomType.setManaged(false);
    }

    public void renderRooms(ArrayList<Room> rooms,VBox roomContainer,ActionEvent event) {
        roomContainer.getChildren().clear();

        if (rooms.isEmpty()) {
            Label empty = new Label("No rooms match your filter.");
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

            Label amenitiesLabel = new Label("Includes: " + formatAmenities(room.getAmenities()));
            amenitiesLabel.getStyleClass().add("main-content-hint");

            Button selectBtn = new Button("Reserve This Room");
            selectBtn.getStyleClass().add("btn-select-room");
            selectBtn.setMaxWidth(Double.MAX_VALUE);
            selectBtn.setOnAction(e -> {
                System.out.println("Selected Room: " + room.getRoomNumber());
                MainController.setReservationContext(room,null,null);
                MainController.navigate(event,"Reservation_Form.fxml");
            });
            card.getChildren().addAll(header, detailsLabel, priceLabel, amenitiesLabel, selectBtn);
            roomContainer.getChildren().add(card);
        }
    }

    public static String formatAmenities(ArrayList<Amenity> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return "Standard Amenities";
        }
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < Math.min(amenities.size(), 3); i++) {
            temp.append(amenities.get(i).getName());
            if (i < 2 && i < amenities.size() - 1) temp.append(", ");
        }
        if (amenities.size() > 3) temp.append("...");
        return temp.toString();
    }
}