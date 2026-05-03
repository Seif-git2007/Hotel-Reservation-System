import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class AdminMenuController extends MainController implements SessionController {

    // ── Stat card labels (fx:id must match Admin_Menu.fxml) ──────────────────
    @FXML private Label labelUserCount;
    @FXML private Label labelBookingCount;
    @FXML private Label labelRevenue;

    // ── Sidebar ──────────────────────────────────────────────────────────────
    @FXML private AdminSidebarController sidebarController;

    // ── Main content area ────────────────────────────────────────────────────
    @FXML private VBox mainContentCard;

    // ── Internal state ───────────────────────────────────────────────────────
    private AppSession session;
    private Admin      admin;

    // ─────────────────────────────────────────────────────────────────────────
    // SessionController entry point
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initSession(AppSession session) {
        this.session = session;
        this.admin   = (Admin) session.getCurrentUser();

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.setActive(sidebarController.btnDashboard);
        }

        refreshStats();
        buildMainContent();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Stats cards
    // ─────────────────────────────────────────────────────────────────────────
    private void refreshStats() {
        // Users card
        int userCount = HotelDataBase.getUsers().size();
        if (labelUserCount != null)
            labelUserCount.setText(String.valueOf(userCount));

        // Bookings card — active (PENDING + CONFIRMED)
        long activeBookings = HotelDataBase.reservations.stream()
                .filter(r -> r.getStatus() == Reservation.Status.PENDING
                        || r.getStatus() == Reservation.Status.CONFIRMED)
                .count();
        if (labelBookingCount != null)
            labelBookingCount.setText(String.valueOf(activeBookings));

        // Revenue card — sum of completed reservations
        double totalRevenue = HotelDataBase.reservations.stream()
                .filter(r -> r.getStatus() == Reservation.Status.COMPLETED
                        || r.getStatus() == Reservation.Status.CONFIRMED)
                .mapToDouble(r -> {
                    long nights = ChronoUnit.DAYS.between(
                            r.getCheckInDate(), r.getCheckOutDate());
                    if (nights == 0) nights = 1;
                    return nights * r.getRoom().getType().getBasePrice();
                })
                .sum();
        if (labelRevenue != null)
            labelRevenue.setText(String.format("$%.0f", totalRevenue));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Main content — TabPane with four management tables
    // ─────────────────────────────────────────────────────────────────────────
    private void buildMainContent() {
        if (mainContentCard == null) return;
        mainContentCard.getChildren().clear();
        mainContentCard.setAlignment(Pos.TOP_LEFT);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        tabs.getTabs().addAll(
                buildRoomsTab(),
                buildRoomTypesTab(),
                buildAmenitiesTab(),
                buildStaffTab()
        );

        mainContentCard.getChildren().add(tabs);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tab: Rooms
    // ─────────────────────────────────────────────────────────────────────────
    private Tab buildRoomsTab() {
        Tab tab = new Tab("Rooms");

        // ── Scrollable card grid ──────────────────────────────────────────────
        VBox cardContainer = new VBox(14);
        cardContainer.setPadding(new Insets(4, 8, 16, 8));

        ScrollPane scroll = new ScrollPane(cardContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // ── Inline form panel (hidden by default) ─────────────────────────────
        VBox formPanel = new VBox(14);
        formPanel.setVisible(false);
        formPanel.setManaged(false);

        // ── Top toolbar ───────────────────────────────────────────────────────
        Button btnCreate = new Button("＋  Create Room");
        btnCreate.getStyleClass().add("nav-action-button");
        btnCreate.setOnAction(e -> {
            formPanel.getChildren().clear();
            // Build a fresh form that treats formPanel itself as the panel
            populateRoomForm(null, formPanel, cardContainer, scroll);
            formPanel.setVisible(true);
            formPanel.setManaged(true);
            scroll.setVisible(false);
            scroll.setManaged(false);
        });

        HBox topBar = new HBox(btnCreate);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 12, 6, 12));

        // ── Root layout ───────────────────────────────────────────────────────
        VBox root = new VBox(0, topBar, formPanel, scroll);
        VBox.setVgrow(root, Priority.ALWAYS);
        root.setStyle("-fx-background-color: transparent;");

        // Populate cards initially
        renderRoomCards(cardContainer, formPanel, scroll);

        tab.setContent(root);
        return tab;
    }

    /** Renders all rooms as cards into cardContainer. */
    private void renderRoomCards(VBox cardContainer, VBox formPanel, ScrollPane scroll) {
        cardContainer.getChildren().clear();

        if (HotelDataBase.getRooms().isEmpty()) {
            Label empty = new Label("No rooms found.");
            empty.setStyle("-fx-text-fill: #6B6B6B; -fx-font-size: 14px; -fx-padding: 40;");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            cardContainer.getChildren().add(empty);
            return;
        }

        for (Room room : HotelDataBase.getRooms()) {
            VBox card = new VBox(10);
            card.getStyleClass().add("room-card");

            // ── Card header: type badge + view tag ───────────────────────────
            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);

            Label lblType = new Label(room.getType().getSize().toUpperCase());
            lblType.getStyleClass().add("room-card-title");

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblView = new Label("✨ " + room.getView() + " VIEW");
            lblView.getStyleClass().add("room-card-status");

            header.getChildren().addAll(lblType, spacer, lblView);

            // ── Card body ────────────────────────────────────────────────────
            Label lblDetails = new Label(String.format(
                    "Room %d  •  Floor %d  •  Capacity: %d Guests",
                    room.getRoomNumber(), room.getFloor(), room.getType().getCapacity()));
            lblDetails.getStyleClass().add("room-card-price");
            lblDetails.setStyle("-fx-font-weight: bold; -fx-text-fill: #2C2C2C;");

            Label lblPrice = new Label(String.format("$%.2f per night", room.getType().getBasePrice()));
            lblPrice.getStyleClass().add("room-card-price");

            Label lblAmen = new Label("Includes: " + ViewRoomsController.formatAmenities(room.getAmenities()));
            lblAmen.getStyleClass().add("main-content-hint");

            // ── Card action buttons ──────────────────────────────────────────
            Button btnEdit   = new Button("✎  Edit");
            Button btnDelete = new Button("✕  Delete");
            btnEdit  .getStyleClass().add("nav-action-button");
            btnDelete.getStyleClass().add("nav-action-button");
            btnDelete.setStyle("-fx-text-fill: #C0392B;");

            btnEdit.setOnAction(e -> {
                formPanel.getChildren().clear();
                populateRoomForm(room, formPanel, cardContainer, scroll);
                formPanel.setVisible(true);
                formPanel.setManaged(true);
                scroll.setVisible(false);
                scroll.setManaged(false);
            });

            btnDelete.setOnAction(e -> {
                int idx = HotelDataBase.getRooms().indexOf(room);
                try {
                    admin.removeRoom(idx);
                    refreshStats();
                    renderRoomCards(cardContainer, formPanel, scroll);
                } catch (InvalidInputException ex) {
                    showAlert(ex.getMessage());
                }
            });

            HBox actions = new HBox(10, btnEdit, btnDelete);
            actions.setAlignment(Pos.CENTER_RIGHT);

            card.getChildren().addAll(header, lblDetails, lblPrice, lblAmen, actions);
            cardContainer.getChildren().add(card);
        }
    }

    /**
     * Populates {@code panel} with create/edit form controls in-place.
     * Pass {@code room = null} for create mode, or a Room for edit mode.
     */
    private void populateRoomForm(Room room, VBox panel,
                                  VBox cardContainer, ScrollPane scroll) {
        boolean isEdit = (room != null);

        // ── Form fields ───────────────────────────────────────────────────────
        TextField tfNumber = new TextField(isEdit ? String.valueOf(room.getRoomNumber()) : "");
        tfNumber.setPromptText("Room number");
        tfNumber.setEditable(!isEdit);

        TextField tfFloor = new TextField(isEdit ? String.valueOf(room.getFloor()) : "");
        tfFloor.setPromptText("Floor");

        ComboBox<Room.view> cbView = new ComboBox<>();
        cbView.getItems().setAll(Room.view.values());
        cbView.setValue(isEdit ? room.getView() : Room.view.SEA);

        ComboBox<RoomType> cbType = new ComboBox<>();
        cbType.getItems().setAll(HotelDataBase.getRoomTypes());
        if (isEdit) cbType.setValue(room.getType());
        else        cbType.getSelectionModel().selectFirst();

        ListView<Amenity> lvAmen = new ListView<>();
        lvAmen.getItems().setAll(HotelDataBase.getAmenities());
        lvAmen.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvAmen.setPrefHeight(110);
        if (isEdit)
            for (Amenity a : room.getAmenities())
                lvAmen.getSelectionModel().select(a);

        // ── Field grid ────────────────────────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(10);
        ColumnConstraints cc0 = new ColumnConstraints(); cc0.setPrefWidth(110);
        ColumnConstraints cc1 = new ColumnConstraints(); cc1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(cc0, cc1);

        String[] labels = { "Room Number:", "Floor:", "View:", "Room Type:", "Amenities:" };
        javafx.scene.Node[] controls = { tfNumber, tfFloor, cbView, cbType, lvAmen };

        for (int i = 0; i < controls.length; i++) {
            Label lbl = new Label(labels[i]);
            lbl.setStyle("-fx-text-fill: #2C2C2C; -fx-font-size: 13px;");
            grid.add(lbl, 0, i);
            grid.add(controls[i], 1, i);
            if (controls[i] instanceof TextField tf) tf.setMaxWidth(Double.MAX_VALUE);
            if (controls[i] instanceof ComboBox<?> cb) cb.setMaxWidth(Double.MAX_VALUE);
        }

        // ── Buttons ───────────────────────────────────────────────────────────
        Button btnSave   = new Button(isEdit ? "💾  Save Changes" : "＋  Add Room");
        Button btnCancel = new Button("Cancel");
        btnSave  .getStyleClass().add("nav-action-button");
        btnCancel.getStyleClass().add("nav-action-button");

        btnCancel.setOnAction(e -> formPanel_hide(panel, scroll));

        btnSave.setOnAction(e -> {
            try {
                int floor = Integer.parseInt(tfFloor.getText().trim());
                ArrayList<Amenity> amenities = new ArrayList<>(lvAmen.getSelectionModel().getSelectedItems());
                if (isEdit) {
                    int idx = HotelDataBase.getRooms().indexOf(room);
                    admin.updateRoom(room.getRoomNumber(), floor,
                            cbView.getValue(), cbType.getValue(), amenities, idx);
                } else {
                    int roomNo = Integer.parseInt(tfNumber.getText().trim());
                    admin.addRoom(roomNo, floor, cbView.getValue(), cbType.getValue(), amenities);
                }
                refreshStats();
                renderRoomCards(cardContainer, panel, scroll);
                formPanel_hide(panel, scroll);
            } catch (NumberFormatException ex) {
                showAlert("Room number and floor must be integers.");
            } catch (InvalidInputException ex) {
                showAlert(ex.getMessage());
            }
        });

        HBox btnRow = new HBox(10, btnSave, btnCancel);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        // ── Header ────────────────────────────────────────────────────────────
        Label title = new Label(isEdit ? "Edit Room #" + room.getRoomNumber() : "Create New Room");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1C1C2E;");

        Pane accent = new Pane();
        accent.setPrefSize(40, 3);
        accent.setStyle("-fx-background-color: #C9A84C; -fx-background-radius: 2;");

        panel.setSpacing(14);
        panel.setPadding(new Insets(16, 20, 16, 20));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 10;"
                + "-fx-border-color: #E0D9CC; -fx-border-radius: 10; -fx-border-width: 1;");
        panel.getChildren().addAll(title, accent, grid, btnRow);
    }

    // ── Helper: hide the form and show the scroll pane ────────────────────────
    private void formPanel_hide(VBox panel, ScrollPane scroll) {
        panel.setVisible(false);
        panel.setManaged(false);
        scroll.setVisible(true);
        scroll.setManaged(true);
    }


    // ─────────────────────────────────────────────────────────────────────────
    // Tab: Room Types
    // ─────────────────────────────────────────────────────────────────────────
    private Tab buildRoomTypesTab() {
        Tab tab = new Tab("Room Types");

        TableView<RoomType> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<RoomType, String> colSize     = new TableColumn<>("Size");
        TableColumn<RoomType, String> colPrice    = new TableColumn<>("Base Price / Night");
        TableColumn<RoomType, Integer> colCap     = new TableColumn<>("Capacity");

        colSize .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getSize()));
        colPrice.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                "$" + d.getValue().getBasePrice()));
        colCap  .setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getCapacity()).asObject());

        table.getColumns().addAll(colSize, colPrice, colCap);
        table.getItems().setAll(HotelDataBase.getRoomTypes());

        Button btnAdd    = new Button("+ Add Type");
        Button btnEdit   = new Button("Edit");
        Button btnDelete = new Button("Delete");
        styleActionButtons(btnAdd, btnEdit, btnDelete);

        btnAdd.setOnAction(e -> showAddRoomTypeDialog(table));
        btnEdit.setOnAction(e -> {
            RoomType selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Select a room type to edit."); return; }
            showEditRoomTypeDialog(selected, table);
        });
        btnDelete.setOnAction(e -> {
            RoomType selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Select a room type to delete."); return; }
            int idx = HotelDataBase.getRoomTypes().indexOf(selected);
            try {
                admin.removeRoomType(idx);
                table.getItems().setAll(HotelDataBase.getRoomTypes());
            } catch (InvalidInputException ex) {
                showAlert(ex.getMessage());
            }
        });

        HBox toolbar = toolbar(btnAdd, btnEdit, btnDelete);
        VBox content = new VBox(8, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        content.setPadding(new Insets(12));
        tab.setContent(content);
        return tab;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tab: Amenities
    // ─────────────────────────────────────────────────────────────────────────
    private Tab buildAmenitiesTab() {
        Tab tab = new Tab("Amenities");

        TableView<Amenity> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Amenity, String> colName  = new TableColumn<>("Name");
        TableColumn<Amenity, String> colPrice = new TableColumn<>("Price");

        colName .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));
        colPrice.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                "$" + d.getValue().getPrice()));

        table.getColumns().addAll(colName, colPrice);
        table.getItems().setAll(HotelDataBase.getAmenities());

        Button btnAdd    = new Button("+ Add Amenity");
        Button btnEdit   = new Button("Edit");
        Button btnDelete = new Button("Delete");
        styleActionButtons(btnAdd, btnEdit, btnDelete);

        btnAdd.setOnAction(e -> showAddAmenityDialog(table));
        btnEdit.setOnAction(e -> {
            Amenity selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Select an amenity to edit."); return; }
            showEditAmenityDialog(selected, table);
        });
        btnDelete.setOnAction(e -> {
            Amenity selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Select an amenity to delete."); return; }
            int idx = HotelDataBase.getAmenities().indexOf(selected);
            try {
                admin.removeAmenity(idx);
                table.getItems().setAll(HotelDataBase.getAmenities());
            } catch (InvalidInputException ex) {
                showAlert(ex.getMessage());
            }
        });

        HBox toolbar = toolbar(btnAdd, btnEdit, btnDelete);
        VBox content = new VBox(8, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        content.setPadding(new Insets(12));
        tab.setContent(content);
        return tab;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tab: Staff (Receptionists)
    // ─────────────────────────────────────────────────────────────────────────
    private Tab buildStaffTab() {
        Tab tab = new Tab("Staff");

        TableView<Receptionist> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Receptionist, String> colName   = new TableColumn<>("Name");
        TableColumn<Receptionist, String> colEmail  = new TableColumn<>("Email");
        TableColumn<Receptionist, String> colGender = new TableColumn<>("Gender");
        TableColumn<Receptionist, String> colHours  = new TableColumn<>("Working Hours");
        TableColumn<Receptionist, String> colDob    = new TableColumn<>("Date of Birth");

        colName  .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getUsername()));
        colEmail .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEmail()));
        colGender.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getGender().toString()));
        colHours .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getWorkingHours() + " hrs"));
        colDob   .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDateOfBirth().toString()));

        table.getColumns().addAll(colName, colEmail, colGender, colHours, colDob);
        table.getItems().setAll(HotelDataBase.getReceptionists());

        Button btnAdd = new Button("+ Add Receptionist");
        styleActionButtons(btnAdd);

        btnAdd.setOnAction(e -> showAddReceptionistDialog(table));

        HBox toolbar = toolbar(btnAdd);
        VBox content = new VBox(8, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        content.setPadding(new Insets(12));
        tab.setContent(content);
        return tab;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Add / Edit dialogs — Rooms
    // ─────────────────────────────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────────────
    // Add / Edit dialogs — Room Types
    // ─────────────────────────────────────────────────────────────────────────
    private void showAddRoomTypeDialog(TableView<RoomType> table) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Add Room Type");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfSize     = new TextField(); tfSize    .setPromptText("e.g. Deluxe");
        TextField tfPrice    = new TextField(); tfPrice   .setPromptText("Base price / night");
        TextField tfCapacity = new TextField(); tfCapacity.setPromptText("Capacity");

        dlg.getDialogPane().setContent(formGrid(
                "Size:", tfSize, "Base Price:", tfPrice, "Capacity:", tfCapacity));

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                admin.addRoomType(tfSize.getText().trim(),
                        Double.parseDouble(tfPrice.getText().trim()),
                        Integer.parseInt(tfCapacity.getText().trim()));
                table.getItems().setAll(HotelDataBase.getRoomTypes());
            } catch (NumberFormatException ex) {
                showAlert("Price and capacity must be numbers.");
            } catch (InvalidInputException ex) {
                showAlert(ex.getMessage());
            }
        });
    }

    private void showEditRoomTypeDialog(RoomType rt, TableView<RoomType> table) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Edit Room Type");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfSize     = new TextField(rt.getSize());
        TextField tfPrice    = new TextField(String.valueOf(rt.getBasePrice()));
        TextField tfCapacity = new TextField(String.valueOf(rt.getCapacity()));

        dlg.getDialogPane().setContent(formGrid(
                "Size:", tfSize, "Base Price:", tfPrice, "Capacity:", tfCapacity));

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                int idx = HotelDataBase.getRoomTypes().indexOf(rt);
                admin.updateRoomTypes(tfSize.getText().trim(),
                        Double.parseDouble(tfPrice.getText().trim()),
                        Integer.parseInt(tfCapacity.getText().trim()), idx);
                table.getItems().setAll(HotelDataBase.getRoomTypes());
            } catch (NumberFormatException ex) {
                showAlert("Price and capacity must be numbers.");
            } catch (InvalidInputException ex) {
                showAlert(ex.getMessage());
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Add / Edit dialogs — Amenities
    // ─────────────────────────────────────────────────────────────────────────
    private void showAddAmenityDialog(TableView<Amenity> table) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Add Amenity");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfName  = new TextField(); tfName .setPromptText("Name");
        TextField tfPrice = new TextField(); tfPrice.setPromptText("Price");

        dlg.getDialogPane().setContent(formGrid("Name:", tfName, "Price:", tfPrice));

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                admin.addAmenity(tfName.getText().trim(),
                        Double.parseDouble(tfPrice.getText().trim()));
                table.getItems().setAll(HotelDataBase.getAmenities());
            } catch (NumberFormatException ex) {
                showAlert("Price must be a number.");
            } catch (InvalidInputException ex) {
                showAlert(ex.getMessage());
            }
        });
    }

    private void showEditAmenityDialog(Amenity amenity, TableView<Amenity> table) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Edit Amenity");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfName  = new TextField(amenity.getName());
        TextField tfPrice = new TextField(String.valueOf(amenity.getPrice()));

        dlg.getDialogPane().setContent(formGrid("Name:", tfName, "Price:", tfPrice));

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                int idx = HotelDataBase.getAmenities().indexOf(amenity);
                admin.updateAmenityPrice(tfName.getText().trim(),
                        Double.parseDouble(tfPrice.getText().trim()), idx);
                table.getItems().setAll(HotelDataBase.getAmenities());
            } catch (NumberFormatException ex) {
                showAlert("Price must be a number.");
            } catch (InvalidInputException ex) {
                showAlert(ex.getMessage());
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Add dialog — Receptionists
    // ─────────────────────────────────────────────────────────────────────────
    private void showAddReceptionistDialog(TableView<Receptionist> table) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Add Receptionist");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfName   = new TextField(); tfName  .setPromptText("Username");
        TextField tfPass   = new TextField(); tfPass  .setPromptText("Password");
        TextField tfEmail  = new TextField(); tfEmail .setPromptText("Email");
        TextField tfHours  = new TextField(); tfHours .setPromptText("Working hours");
        TextField tfDob    = new TextField(); tfDob   .setPromptText("DOB (yyyy-mm-dd)");
        ComboBox<String> cbGender = new ComboBox<>();
        cbGender.getItems().addAll("MALE", "FEMALE");
        cbGender.setValue("MALE");

        dlg.getDialogPane().setContent(formGrid(
                "Name:", tfName, "Password:", tfPass, "Email:", tfEmail,
                "Gender:", cbGender, "Hours:", tfHours, "Date of Birth:", tfDob));

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                admin.addReceptionists(
                        tfName.getText().trim(),
                        tfPass.getText().trim(),
                        java.time.LocalDate.parse(tfDob.getText().trim()),
                        Integer.parseInt(tfHours.getText().trim()),
                        cbGender.getValue(),
                        tfEmail.getText().trim()
                );
                table.getItems().setAll(HotelDataBase.getReceptionists());
                refreshStats();
            } catch (Exception ex) {
                showAlert("Check all fields. DOB format: yyyy-mm-dd");
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Builds a two-column GridPane from alternating label/control pairs. */
    private GridPane formGrid(Object... pairs) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));
        for (int i = 0; i < pairs.length; i += 2) {
            grid.add(new Label(pairs[i].toString()), 0, i / 2);
            grid.add((javafx.scene.Node) pairs[i + 1], 1, i / 2);
        }
        return grid;
    }

    private HBox toolbar(Button... buttons) {
        HBox bar = new HBox(8, buttons);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    @SafeVarargs
    private <T extends Button> void styleActionButtons(T... buttons) {
        for (Button b : buttons) {
            b.getStyleClass().add("nav-action-button");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private String formatList(java.util.List<String> items) {
        if (items == null || items.isEmpty()) return "None";
        return String.join(", ", items);
    }
}