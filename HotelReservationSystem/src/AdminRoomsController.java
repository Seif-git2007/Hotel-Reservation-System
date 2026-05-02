import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AdminRoomsController extends MainController implements SessionController {

    // ── FXML bindings ─────────────────────────────────────────────────────────
    @FXML private AdminSidebarController sidebarController;

    @FXML private TableView<Room>            table;
    @FXML private TableColumn<Room, Integer> colNo;
    @FXML private TableColumn<Room, String>  colType;
    @FXML private TableColumn<Room, Integer> colFloor;
    @FXML private TableColumn<Room, String>  colView;
    @FXML private TableColumn<Room, String>  colPrice;
    @FXML private TableColumn<Room, Integer> colCap;
    @FXML private TableColumn<Room, String>  colAmen;
    @FXML private Label                      lblStatus;

    // ── State ─────────────────────────────────────────────────────────────────
    private AppSession             session;
    private Admin                  admin;

    // ─────────────────────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initSession(AppSession session) {
        this.session = session;
        this.admin   = (Admin) session.getCurrentUser();

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.setActive(sidebarController.btnRooms);
        }
        wireColumns();
        refresh();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Table wiring
    // ─────────────────────────────────────────────────────────────────────────
    private void wireColumns() {
        colNo   .setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getRoomNumber()).asObject());
        colType .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().getSize()));
        colFloor.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getFloor()).asObject());
        colView .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getView().toString()));
        colPrice.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("$%.0f", d.getValue().getType().getBasePrice())));
        colCap  .setCellValueFactory(d -> new SimpleIntegerProperty(
                d.getValue().getType().getCapacity()).asObject());
        colAmen .setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getAmenities().stream()
                        .map(Amenity::getName)
                        .collect(Collectors.joining(", "))));
    }

    private void refresh() {
        table.getItems().setAll(HotelDataBase.getRooms());
        setStatus("");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Toolbar actions
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    private void onAdd() {
        RoomDialog dlg = new RoomDialog(null);
        dlg.showAndWait().ifPresent(data -> {
            try {
                admin.addRoom(data.roomNo, data.floor, data.view, data.type, data.amenities);
                refresh();
                setStatus("Room " + data.roomNo + " added.");
            } catch (InvalidInputException ex) {
                showError(ex.getMessage());
            }
        });
    }

    @FXML
    private void onEdit() {
        Room selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Select a room to edit."); return; }

        RoomDialog dlg = new RoomDialog(selected);
        dlg.showAndWait().ifPresent(data -> {
            try {
                int idx = HotelDataBase.getRooms().indexOf(selected);
                admin.updateRoom(data.roomNo, data.floor, data.view, data.type, data.amenities, idx);
                refresh();
                setStatus("Room " + data.roomNo + " updated.");
            } catch (InvalidInputException ex) {
                showError(ex.getMessage());
            }
        });
    }

    @FXML
    private void onDelete() {
        Room selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Select a room to delete."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Room");
        confirm.setHeaderText("Delete Room " + selected.getRoomNumber() + "?");
        confirm.setContentText("This cannot be undone.");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                int idx = HotelDataBase.getRooms().indexOf(selected);
                admin.removeRoom(idx);
                refresh();
                setStatus("Room deleted.");
            } catch (InvalidInputException ex) {
                showError(ex.getMessage());
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private void setStatus(String msg) {
        if (lblStatus != null) lblStatus.setText(msg);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner class: Add / Edit dialog
    // ─────────────────────────────────────────────────────────────────────────
    private static class RoomDialog extends Dialog<RoomDialog.RoomData> {

        record RoomData(int roomNo, int floor, Room.view view,
                        RoomType type, ArrayList<Amenity> amenities) {}

        RoomDialog(Room existing) {
            boolean editing = existing != null;
            setTitle(editing ? "Edit Room" : "Add Room");
            setHeaderText(editing
                    ? "Editing Room " + existing.getRoomNumber()
                    : "Enter new room details");

            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            TextField tfNumber = new TextField(editing ? String.valueOf(existing.getRoomNumber()) : "");
            tfNumber.setPromptText("e.g. 201");
            tfNumber.setDisable(editing);

            TextField tfFloor = new TextField(editing ? String.valueOf(existing.getFloor()) : "");
            tfFloor.setPromptText("e.g. 2");

            ComboBox<Room.view> cbView = new ComboBox<>();
            cbView.getItems().setAll(Room.view.values());
            cbView.setValue(editing ? existing.getView() : Room.view.SEA);

            ComboBox<RoomType> cbType = new ComboBox<>();
            cbType.getItems().setAll(HotelDataBase.getRoomTypes());
            cbType.setConverter(new javafx.util.StringConverter<>() {
                public String toString(RoomType rt) {
                    return rt == null ? "" : rt.getSize() + "  ($" + rt.getBasePrice() + "/night)";
                }
                public RoomType fromString(String s) { return null; }
            });
            cbType.setValue(editing ? existing.getType() : cbType.getItems().get(0));

            ListView<Amenity> lvAmen = new ListView<>();
            lvAmen.getItems().setAll(HotelDataBase.getAmenities());
            lvAmen.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            lvAmen.setCellFactory(lv -> new ListCell<>() {
                protected void updateItem(Amenity a, boolean empty) {
                    super.updateItem(a, empty);
                    setText(empty || a == null ? null : a.getName() + "  ($" + a.getPrice() + ")");
                }
            });
            lvAmen.setPrefHeight(120);
            if (editing)
                for (Amenity a : existing.getAmenities())
                    lvAmen.getSelectionModel().select(a);

            GridPane grid = new GridPane();
            grid.setHgap(12); grid.setVgap(10);
            grid.setPadding(new Insets(16));
            int row = 0;
            grid.add(new Label("Room Number:"), 0, row); grid.add(tfNumber, 1, row++);
            grid.add(new Label("Floor:"),        0, row); grid.add(tfFloor,  1, row++);
            grid.add(new Label("View:"),          0, row); grid.add(cbView,   1, row++);
            grid.add(new Label("Room Type:"),     0, row); grid.add(cbType,   1, row++);
            VBox amenBox = new VBox(4, lvAmen,
                    labelHint("Hold Ctrl / ⌘ to select multiple"));
            grid.add(new Label("Amenities:"), 0, row);
            grid.add(amenBox, 1, row);

            getDialogPane().setContent(grid);
            getDialogPane().setPrefWidth(420);

            setResultConverter(bt -> {
                if (bt != ButtonType.OK) return null;
                try {
                    int roomNo = Integer.parseInt(tfNumber.getText().trim());
                    int floor  = Integer.parseInt(tfFloor.getText().trim());
                    ArrayList<Amenity> selected =
                            new ArrayList<>(lvAmen.getSelectionModel().getSelectedItems());
                    return new RoomData(roomNo, floor, cbView.getValue(), cbType.getValue(), selected);
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Room number and floor must be whole numbers.")
                            .showAndWait();
                    return null;
                }
            });
        }

        private static Label labelHint(String text) {
            Label l = new Label(text);
            l.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
            return l;
        }
    }
}