import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;

import java.util.ArrayList;

public class AdminRoomsController extends MainController implements SessionController {

    // ── FXML ─────────────────────────────────────────────────────────────────
    @FXML private Pane     sidebarPlaceholder;
    @FXML private FlowPane cardGrid;
    @FXML private Label    lblStatus;

    // Inline form
    @FXML private VBox   formPanel;
    @FXML private VBox   formBody;
    @FXML private Label  formTitle;
    @FXML private Button btnSave;

    // ── State ─────────────────────────────────────────────────────────────────
    private AppSession             session;
    private Admin                  admin;
    private AdminSidebarController sidebarController;
    private Room                   selectedRoom;   // card currently highlighted
    private Room                   editingRoom;    // room being added/edited in form

    // Form field references (built dynamically, kept for onSave)
    private TextField           tfNumber;
    private TextField           tfFloor;
    private ComboBox<Room.view> cbView;
    private ComboBox<RoomType>  cbType;
    private FlowPane            amenityTagsPane;

    // ── Init ──────────────────────────────────────────────────────────────────

    @Override
    public void initSession(AppSession session) {
        this.session = session;
        this.admin   = (Admin) session.getCurrentUser();
        injectSidebar();
        refresh();
    }

    private void injectSidebar() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AdminSidebar.fxml"));
            Node sidebar = loader.load();
            sidebarController = loader.getController();
            sidebarController.initSession(session);
            sidebarController.setActive(sidebarController.btnRooms);
            HBox parent = (HBox) sidebarPlaceholder.getParent();
            parent.getChildren().set(parent.getChildren().indexOf(sidebarPlaceholder), sidebar);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Card grid ─────────────────────────────────────────────────────────────

    private void refresh() {
        selectedRoom = null;
        cardGrid.getChildren().clear();
        for (Room room : HotelDataBase.getRooms())
            cardGrid.getChildren().add(buildCard(room));
        setStatus("");
    }

    private HBox buildCard(Room room) {
        HBox card = new HBox(0);
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #E0DAD0;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 0.5;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(15,33,96,0.06), 8, 0, 0, 2);"
        );

        // ── Left navy panel ───────────────────────────────────────────────────
        VBox leftPanel = new VBox(0);
        leftPanel.setMinWidth(160);
        leftPanel.setPrefWidth(160);
        leftPanel.setAlignment(Pos.TOP_LEFT);
        leftPanel.setStyle(
                "-fx-background-color: #0F2160;" +
                        "-fx-background-radius: 12 0 0 12;" +
                        "-fx-padding: 0 0 16 0;"
        );

        Pane accentBar = new Pane();
        accentBar.setPrefHeight(4);
        accentBar.setStyle("-fx-background-color: #C9A84C; -fx-background-radius: 12 0 0 0;");

        Label roomNo = new Label("#" + room.getRoomNumber());
        roomNo.setStyle(
                "-fx-text-fill: #C9A84C;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 34px;" +
                        "-fx-font-style: italic;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 16 0 16;"
        );

        Label titleLabel = new Label(room.getType().getSize() + " Room");
        titleLabel.setStyle(
                "-fx-text-fill: #FFFFFF;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 2 16 0 16;"
        );

        Label viewChip = new Label(room.getView() + " VIEW");
        viewChip.setStyle(
                "-fx-text-fill: #0F2160;" +
                        "-fx-background-color: #C9A84C;" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 0.10em;" +
                        "-fx-padding: 3 10;" +
                        "-fx-background-radius: 10;"
        );
        HBox chipRow = new HBox();
        chipRow.setStyle("-fx-padding: 6 16 0 16;");
        chipRow.getChildren().add(viewChip);

        leftPanel.getChildren().addAll(accentBar, roomNo, titleLabel, chipRow);

        // ── Right content panel ───────────────────────────────────────────────
        VBox rightPanel = new VBox(10);
        rightPanel.setStyle("-fx-padding: 14 16 14 16;");
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // Meta row
        HBox metaRow = new HBox(0);
        metaRow.setStyle(
                "-fx-background-color: #F8F5EF;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #EDE7D8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-width: 0.5;"
        );
        metaRow.getChildren().addAll(
                metaCell("CAPACITY", room.getType().getCapacity() + " Guests", true),
                metaCell("FLOOR",    ordinal(room.getFloor()), true),
                metaCell("RATE",     String.format("$%.0f / night", room.getType().getBasePrice()), false)
        );

        // Amenity pills
        FlowPane pills = new FlowPane(5, 5);
        if (room.getAmenities().isEmpty()) {
            Label none = new Label("No amenities");
            none.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11px; -fx-font-style: italic;");
            pills.getChildren().add(none);
        } else {
            for (Amenity a : room.getAmenities())
                pills.getChildren().add(amenityPill(a.getName()));
        }

        // Footer: status badge + edit/delete buttons
        HBox footer = new HBox(8);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle(
                "-fx-border-color: #E8E3DA transparent transparent transparent;" +
                        "-fx-border-width: 0.5 0 0 0;" +
                        "-fx-padding: 10 0 0 0;"
        );

        boolean occupied = HotelDataBase.reservations.stream().anyMatch(r ->
                r.getRoom() != null &&
                        r.getRoom().getRoomNumber() == room.getRoomNumber() &&
                        (r.getStatus() == Reservation.Status.CONFIRMED || r.getStatus() == Reservation.Status.PENDING));
        footer.getChildren().add(statusBadge(occupied));


        Pane fSpacer = new Pane(); HBox.setHgrow(fSpacer, Priority.ALWAYS);

        Button btnEdit = new Button("✎  Edit");
        btnEdit.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #C9A84C; -fx-border-radius: 6; -fx-border-width: 1.5;" +
                        "-fx-text-fill: #C9A84C; -fx-font-size: 12px; -fx-font-weight: bold;" +
                        "-fx-padding: 7 16; -fx-cursor: hand;"
        );
        btnEdit.setOnMouseEntered(e -> btnEdit.setStyle(btnEdit.getStyle()
                .replace("-fx-background-color: transparent;", "-fx-background-color: rgba(201,168,76,0.10);")));
        btnEdit.setOnMouseExited(e -> btnEdit.setStyle(btnEdit.getStyle()
                .replace("-fx-background-color: rgba(201,168,76,0.10);", "-fx-background-color: transparent;")));
        btnEdit.setOnAction(e -> openForm(room));
        boolean roomEditLocked = HotelDataBase.reservations.stream()
                .anyMatch(r -> r.getRoom() != null
                        && r.getRoom().getRoomNumber() == room.getRoomNumber()
                        && (r.getStatus() == Reservation.Status.PENDING
                        ||  r.getStatus() == Reservation.Status.CONFIRMED));
        btnEdit.setDisable(roomEditLocked);
        btnEdit.setOpacity(roomEditLocked ? 0.3 : 1.0);

        Button btnDel = new Button("✕");
        btnDel.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: rgba(176,0,32,0.25); -fx-border-radius: 6; -fx-border-width: 1.5;" +
                        "-fx-text-fill: rgba(176,0,32,0.70); -fx-font-size: 12px; -fx-font-weight: bold;" +
                        "-fx-padding: 7 12; -fx-cursor: hand;"
        );
        btnDel.setOnAction(e -> onDeleteRoom(room));
        boolean delLocked = roomEditLocked || occupied;
        btnDel.setDisable(delLocked);
        btnDel.setOpacity(delLocked ? 0.3 : 1.0);

        footer.getChildren().addAll(fSpacer, btnEdit, btnDel);
        rightPanel.getChildren().addAll(metaRow, pills, footer);

        card.getChildren().addAll(leftPanel, rightPanel);
        card.setOnMouseClicked(e -> selectCard(card, room));
        return card;
    }

    private void selectCard(HBox card, Room room) {
        for (Node n : cardGrid.getChildren())
            n.setStyle(n.getStyle().replace("-fx-border-color: #C9A84C;", "-fx-border-color: #E0DAD0;"));
        card.setStyle(card.getStyle().replace("-fx-border-color: #E0DAD0;", "-fx-border-color: #C9A84C;"));
        selectedRoom = room;
    }

    // ── Inline form ───────────────────────────────────────────────────────────

    /** Opens the form panel pre-filled for editing, or blank for adding. */
    private void openForm(Room existing) {
        editingRoom = existing;
        boolean adding = existing == null;

        formTitle.setText(adding ? "Add Room" : "Edit Room #" + existing.getRoomNumber());
        formBody.getChildren().clear();

        // Room number
        tfNumber = new TextField(adding ? "" : String.valueOf(existing.getRoomNumber()));
        tfNumber.setPromptText("e.g. 201");
        tfNumber.setDisable(!adding);
        formBody.getChildren().addAll(fieldLabel("Room Number"), tfNumber);

        // Floor
        tfFloor = new TextField(adding ? "" : String.valueOf(existing.getFloor()));
        tfFloor.setPromptText("e.g. 2");
        formBody.getChildren().addAll(fieldLabel("Floor"), tfFloor);

        // View
        cbView = new ComboBox<>();
        cbView.getItems().setAll(Room.view.values());
        cbView.setValue(adding ? Room.view.SEA : existing.getView());
        cbView.setMaxWidth(Double.MAX_VALUE);
        cbView.getStyleClass().add("filter-combo");
        formBody.getChildren().addAll(fieldLabel("View"), cbView);

        // Room type
        cbType = new ComboBox<>();
        cbType.getItems().setAll(HotelDataBase.getRoomTypes());
        cbType.setConverter(new javafx.util.StringConverter<>() {
            public String toString(RoomType rt) { return rt == null ? "" : rt.getSize() + "  ·  $" + (int)rt.getBasePrice() + " / night"; }
            public RoomType fromString(String s) { return null; }
        });
        cbType.setValue(adding ? cbType.getItems().get(0) : existing.getType());
        cbType.setMaxWidth(Double.MAX_VALUE);
        cbType.getStyleClass().add("filter-combo");
        formBody.getChildren().addAll(fieldLabel("Room Type"), cbType);

        // Amenities — clickable toggle tags
        amenityTagsPane = new FlowPane(8, 8);
        amenityTagsPane.setStyle("-fx-padding: 4 0 0 0;");
        for (Amenity a : HotelDataBase.getAmenities()) {
            boolean preSelected = !adding && existing.getAmenities().stream()
                    .anyMatch(ea -> ea.getName().equals(a.getName()));
            Label tag = new Label(a.getName());
            tag.setUserData(a);
            applyAmenityTagStyle(tag, preSelected);
            tag.setOnMouseClicked(e -> {
                boolean nowSelected = !(boolean) tag.getProperties().getOrDefault("selected", false);
                tag.getProperties().put("selected", nowSelected);
                applyAmenityTagStyle(tag, nowSelected);
            });
            tag.getProperties().put("selected", preSelected);
            amenityTagsPane.getChildren().add(tag);
        }
        formBody.getChildren().addAll(fieldLabel("Amenities"), amenityTagsPane);

        // Show panel
        formPanel.setVisible(true);
        formPanel.setManaged(true);
    }

    @FXML private void onAdd() { openForm(null); }

    @FXML private void onCancelForm() {
        formPanel.setVisible(false);
        formPanel.setManaged(false);
        editingRoom = null;
    }

    @FXML private void onSave() {
        try {
            int roomNo = Integer.parseInt(tfNumber.getText().trim());
            int floor  = Integer.parseInt(tfFloor.getText().trim());
            Room.view view      = cbView.getValue();
            RoomType  type      = cbType.getValue();
            ArrayList<Amenity> amenities = new ArrayList<>();
            for (var node : amenityTagsPane.getChildren()) {
                Label tag = (Label) node;
                if (Boolean.TRUE.equals(tag.getProperties().get("selected")))
                    amenities.add((Amenity) tag.getUserData());
            }

            if (editingRoom == null) {
                admin.addRoom(roomNo, floor, view, type, amenities);
                onCancelForm();
                refresh();
                setStatus("✔  Room " + roomNo + " added successfully.");
            } else {
                int idx = HotelDataBase.getRooms().indexOf(editingRoom);
                admin.updateRoom(roomNo, floor, view, type, amenities, idx);
                onCancelForm();
                refresh();
                setStatus("✔  Room " + roomNo + " updated successfully.");
            }

        } catch (NumberFormatException ex) {
            showError("Room number and floor must be whole numbers.");
        } catch (InvalidInputException ex) {
            showError(ex.getMessage());
        }
    }

    private void onDeleteRoom(Room room) {
        boolean inUse = HotelDataBase.reservations.stream()
                .anyMatch(r -> r.getRoom() != null &&
                        r.getRoom().getRoomNumber() == room.getRoomNumber() &&
                        (r.getStatus() == Reservation.Status.PENDING ||
                                r.getStatus() == Reservation.Status.CONFIRMED));

        if (inUse) {
            setStatus("❌  Cannot delete Room " + room.getRoomNumber() + " — it has active reservations.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.NONE);
        confirm.setTitle("Delete Room");
        confirm.setHeaderText(null);

        DialogPane pane = confirm.getDialogPane();
        pane.setStyle(
                "-fx-background-color: #0F2160;" +
                        "-fx-border-color: #C9A84C;" +
                        "-fx-border-width: 1.5;"
        );

        Label msg = new Label(
                "You are about to delete Room " + room.getRoomNumber() + ".\n\nThis action cannot be undone."
        );
        msg.setStyle(
                "-fx-text-fill: #FFFFFF;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-padding: 10 16 10 16;"
        );
        pane.setContent(msg);

        ButtonType btnConfirm = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel  = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnConfirm, btnCancel);

        confirm.setOnShown(ev -> {
            Button deleteButton = (Button) pane.lookupButton(btnConfirm);
            deleteButton.setStyle(
                    "-fx-background-color: #B00020;" +
                            "-fx-text-fill: #FFFFFF;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 13px;" +
                            "-fx-padding: 8 20;" +
                            "-fx-background-radius: 6;" +
                            "-fx-cursor: hand;"
            );
            Button cancelButton = (Button) pane.lookupButton(btnCancel);
            cancelButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #C9A84C;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 13px;" +
                            "-fx-padding: 8 20;" +
                            "-fx-border-color: #C9A84C;" +
                            "-fx-border-radius: 6;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-cursor: hand;"
            );
        });

        confirm.showAndWait().ifPresent(btn -> {
            if (btn != btnConfirm) return;
            try {
                if (editingRoom != null && editingRoom.getRoomNumber() == room.getRoomNumber())
                    onCancelForm();
                admin.removeRoom(HotelDataBase.getRooms().indexOf(room));
                refresh();
                setStatus("Room deleted.");
            } catch (InvalidInputException ex) { showError(ex.getMessage()); }
        });
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private Label fieldLabel(String text) {
        Label l = new Label(text.toUpperCase());
        l.setStyle(
                "-fx-text-fill: #9B9589;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 0.12em;"
        );
        return l;
    }

    private void applyAmenityTagStyle(Label tag, boolean selected) {
        if (selected) {
            tag.setStyle(
                    "-fx-background-color: #0F2160;" +
                            "-fx-text-fill: #C9A84C;" +
                            "-fx-font-size: 11px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 5 12;" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: #0F2160;" +
                            "-fx-border-radius: 20;" +
                            "-fx-border-width: 1.2;" +
                            "-fx-cursor: hand;"
            );
        } else {
            tag.setStyle(
                    "-fx-background-color: #FFFFFF;" +
                            "-fx-text-fill: #0F2160;" +
                            "-fx-font-size: 11px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 5 12;" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-color: #C9A84C;" +
                            "-fx-border-radius: 20;" +
                            "-fx-border-width: 1.2;" +
                            "-fx-cursor: hand;"
            );
        }
    }

    private HBox metaCell(String label, String value, boolean rightBorder) {
        VBox cell = new VBox(3);
        cell.setAlignment(Pos.CENTER);
        cell.setPadding(new Insets(8, 10, 8, 10));
        cell.setStyle(rightBorder
                ? "-fx-border-color: transparent #E8E3DA transparent transparent; -fx-border-width: 0 0.5 0 0;"
                : "");
        HBox.setHgrow(cell, Priority.ALWAYS);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #9B9589; -fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 0.12em;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #0F2160; -fx-font-size: 14px; -fx-font-weight: bold;");
        cell.getChildren().addAll(lbl, val);
        HBox wrapper = new HBox(cell); HBox.setHgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }

    private Label amenityPill(String name) {
        Label pill = new Label(name);
        pill.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-text-fill: #0F2160;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 0.04em;" +
                        "-fx-padding: 4 12;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: #C9A84C;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 1.2;"
        );
        return pill;
    }

    private Label statusBadge(boolean occupied) {
        Label badge = new Label(occupied ? "OCCUPIED" : "AVAILABLE");
        badge.setStyle(occupied
                ? "-fx-background-color: rgba(176,0,32,0.07); -fx-text-fill: #7F0010; -fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 0.12em; -fx-padding: 4 10; -fx-background-radius: 20; -fx-border-color: rgba(176,0,32,0.20); -fx-border-radius: 20; -fx-border-width: 0.5;"
                : "-fx-background-color: rgba(46,125,50,0.08); -fx-text-fill: #1B5E20; -fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 0.12em; -fx-padding: 4 10; -fx-background-radius: 20; -fx-border-color: rgba(46,125,50,0.25); -fx-border-radius: 20; -fx-border-width: 0.5;"
        );
        return badge;
    }

    private String ordinal(int n) {
        if (n == 1) return "1st"; if (n == 2) return "2nd"; if (n == 3) return "3rd";
        return n + "th";
    }

    private void setStatus(String msg) { if (lblStatus != null) lblStatus.setText(msg); }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}