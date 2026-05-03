import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.ButtonBar;

import java.util.ArrayList;

public class AdminRoomTypesController implements SessionController {

    // ── FXML injections ──────────────────────────────────────────────────────
    @FXML private AdminSidebarController sidebarController;

    @FXML private Label    feedbackLabel;
    @FXML private VBox     formPanel;
    @FXML private Label    formTitle;
    @FXML private TextField fieldSize;
    @FXML private TextField fieldPrice;
    @FXML private TextField fieldCapacity;
    @FXML private Label    sizeError;
    @FXML private Label    priceError;
    @FXML private Label    capacityError;
    @FXML private Button   btnFormSubmit;
    @FXML private FlowPane roomTypeGrid;

    // ── State ────────────────────────────────────────────────────────────────
    private AppSession session;
    private Admin      admin;

    /** null = Add mode, non-null = Edit mode */
    private RoomType editingRoomType = null;

    // ── SessionController ────────────────────────────────────────────────────
    @Override
    public void initSession(AppSession session) {
        this.session = session;
        this.admin   = (Admin) session.getCurrentUser();

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.setActive(sidebarController.btnRoomTypes);
        }
        renderCards();
    }

    // ── Card rendering ───────────────────────────────────────────────────────

    private void renderCards() {
        roomTypeGrid.getChildren().clear();

        ArrayList<RoomType> list = HotelDataBase.getRoomTypes();

        if (list.isEmpty()) {
            Label empty = new Label("No room types yet. Use '＋ Add Room Type' to create one.");
            empty.getStyleClass().add("main-content-hint");
            empty.setStyle("-fx-padding: 40;");
            roomTypeGrid.getChildren().add(empty);
            return;
        }

        for (RoomType rt : list) {
            roomTypeGrid.getChildren().add(buildCard(rt));
        }
    }

    private VBox buildCard(RoomType rt) {
        VBox card = new VBox(0);
        card.setPrefWidth(196);
        card.setMaxWidth(196);

        // ── Gold accent top bar ───────────────────────────────────────────
        Pane topBar = new Pane();
        topBar.setPrefHeight(5);
        topBar.setStyle("-fx-background-color: #C9A84C; -fx-background-radius: 10 10 0 0;");

        // ── Navy header panel ─────────────────────────────────────────────
        VBox header = new VBox(4);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #0F2160; -fx-padding: 14 16 14 16;");

        // Size icon — pick a luxury emoji by capacity
        Label iconLabel = new Label(roomTypeIcon(rt.getSize(), rt.getCapacity()));
        iconLabel.setStyle("-fx-font-size: 30px; -fx-padding: 0 0 4 0;");

        Label nameLabel = new Label(rt.getSize().toUpperCase());
        nameLabel.setStyle(
                "-fx-text-fill: #FFFFFF;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 0.06em;"
        );

        // Capacity chip
        Label capacityChip = new Label(
                rt.getCapacity() + (rt.getCapacity() == 1 ? " Guest" : " Guests")
        );
        capacityChip.setStyle(
                "-fx-text-fill: #0F2160;" +
                        "-fx-background-color: #C9A84C;" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 0.10em;" +
                        "-fx-padding: 3 10;" +
                        "-fx-background-radius: 10;"
        );
        HBox chipRow = new HBox(capacityChip);
        chipRow.setStyle("-fx-padding: 4 0 0 0;");

        header.getChildren().addAll(iconLabel, nameLabel, chipRow);

        // ── White body ────────────────────────────────────────────────────
        VBox body = new VBox(10);
        body.setAlignment(Pos.CENTER_LEFT);
        body.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-padding: 14 16 14 16;"
        );

        // Price display
        Label priceLabel = new Label(String.format("$%.0f", rt.getBasePrice()));
        priceLabel.setStyle(
                "-fx-text-fill: #0F2160;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;"
        );
        Label perNightLabel = new Label("per night");
        perNightLabel.setStyle(
                "-fx-text-fill: #9B9589;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-style: italic;"
        );

        VBox priceBlock = new VBox(0, priceLabel, perNightLabel);
        priceBlock.setAlignment(Pos.CENTER_LEFT);

        // Usage indicator — how many rooms use this type
        long roomCount = HotelDataBase.getRooms().stream()
                .filter(r -> r.getType().getSize().equals(rt.getSize()))
                .count();
        Label usageLabel = new Label(roomCount + (roomCount == 1 ? " room" : " rooms") + " using this type");
        usageLabel.setStyle(
                "-fx-text-fill: " + (roomCount > 0 ? "#C9A84C" : "#AAAAAA") + ";" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 0.04em;"
        );

        body.getChildren().addAll(priceBlock, usageLabel);

        // ── Footer with Edit / Delete ─────────────────────────────────────
        Pane divider = new Pane();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: #E0DAD0;");

        Button editBtn = new Button("✎  Edit");
        editBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #C9A84C; -fx-border-radius: 6; -fx-border-width: 1.5;" +
                        "-fx-text-fill: #C9A84C; -fx-font-size: 11px; -fx-font-weight: bold;" +
                        "-fx-padding: 6 12; -fx-cursor: hand;"
        );
        editBtn.setOnMouseEntered(e ->
                editBtn.setStyle(editBtn.getStyle()
                        .replace("-fx-background-color: transparent;",
                                "-fx-background-color: rgba(201,168,76,0.10);")));
        editBtn.setOnMouseExited(e ->
                editBtn.setStyle(editBtn.getStyle()
                        .replace("-fx-background-color: rgba(201,168,76,0.10);",
                                "-fx-background-color: transparent;")));
        editBtn.setOnAction(e -> openEditForm(rt));
        HBox.setHgrow(editBtn, Priority.ALWAYS);
        editBtn.setMaxWidth(Double.MAX_VALUE);

        Button deleteBtn = new Button("✕");
        deleteBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: rgba(176,0,32,0.25); -fx-border-radius: 6; -fx-border-width: 1.5;" +
                        "-fx-text-fill: rgba(176,0,32,0.70); -fx-font-size: 11px; -fx-font-weight: bold;" +
                        "-fx-padding: 6 10; -fx-cursor: hand;"
        );
        deleteBtn.setOnMouseEntered(e ->
                deleteBtn.setStyle(deleteBtn.getStyle()
                        .replace("-fx-background-color: transparent;",
                                "-fx-background-color: rgba(176,0,32,0.06);")));
        deleteBtn.setOnMouseExited(e ->
                deleteBtn.setStyle(deleteBtn.getStyle()
                        .replace("-fx-background-color: rgba(176,0,32,0.06);",
                                "-fx-background-color: transparent;")));
        deleteBtn.setOnAction(e -> handleDelete(rt));
        boolean typeInUse = HotelDataBase.reservations.stream()
                .anyMatch(r -> r.getRoom().getType().getSize().equalsIgnoreCase(rt.getSize())
                        && (r.getStatus() == Reservation.Status.PENDING
                        ||  r.getStatus() == Reservation.Status.CONFIRMED));
        deleteBtn.setDisable(typeInUse);
        deleteBtn.setOpacity(typeInUse ? 0.3 : 1.0);

        HBox actions = new HBox(8, editBtn, deleteBtn);
        actions.setAlignment(Pos.CENTER);
        actions.setStyle("-fx-padding: 12 16 14 16; -fx-background-color: #FFFFFF; -fx-background-radius: 0 0 10 10;");

        card.getChildren().addAll(topBar, header, body, divider, actions);

        // Hover shadow
        card.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #E0DAD0;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 0.5;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(15,33,96,0.07), 8, 0, 0, 3);"
        );
        card.setOnMouseEntered(e ->
                card.setStyle(card.getStyle()
                        .replace("-fx-border-color: #E0DAD0;", "-fx-border-color: #C9A84C;")
                        .replace("rgba(15,33,96,0.07), 8", "rgba(15,33,96,0.13), 14")));
        card.setOnMouseExited(e ->
                card.setStyle(card.getStyle()
                        .replace("-fx-border-color: #C9A84C;", "-fx-border-color: #E0DAD0;")
                        .replace("rgba(15,33,96,0.13), 14", "rgba(15,33,96,0.07), 8")));

        return card;
    }

    // ── Form logic ───────────────────────────────────────────────────────────

    @FXML
    private void openAddForm() {
        editingRoomType = null;
        formTitle.setText("Add Room Type");
        btnFormSubmit.setText("Save Room Type");
        fieldSize.clear();
        fieldPrice.clear();
        fieldCapacity.clear();
        clearErrors();
        showForm();
    }

    private void openEditForm(RoomType rt) {
        editingRoomType = rt;
        formTitle.setText("Edit  ·  " + rt.getSize());
        btnFormSubmit.setText("Update Room Type");
        fieldSize.setText(rt.getSize());
        fieldPrice.setText(String.valueOf((int) rt.getBasePrice()));
        fieldCapacity.setText(String.valueOf(rt.getCapacity()));
        clearErrors();
        showForm();
    }

    @FXML
    private void closeForm() {
        formPanel.setVisible(false);
        formPanel.setManaged(false);
        feedbackLabel.setVisible(false);
    }

    @FXML
    private void submitForm() {
        clearErrors();
        boolean valid = true;

        // ── Validate size ─────────────────────────────────────────────────
        String size = fieldSize.getText().trim();
        if (size.isEmpty()) {
            MainController.setFieldError(sizeError, "Name cannot be empty.");
            valid = false;
        }

        // ── Validate price ────────────────────────────────────────────────
        double price = 0;
        try {
            price = Double.parseDouble(fieldPrice.getText().trim());
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            MainController.setFieldError(priceError, "Enter a valid positive number.");
            valid = false;
        }

        // ── Validate capacity ─────────────────────────────────────────────
        int capacity = 0;
        try {
            capacity = Integer.parseInt(fieldCapacity.getText().trim());
            if (capacity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            MainController.setFieldError(capacityError, "Enter a whole number ≥ 1.");
            valid = false;
        }

        if (!valid) return;

        RoomType updated = new RoomType(size, price, capacity);

        try {
            if (editingRoomType == null) {
                // ── CREATE ────────────────────────────────────────────────
                RoomType.create(updated);
                showFeedback("✔  Room type \"" + size + "\" added successfully.", false);
            } else {
                // ── UPDATE ────────────────────────────────────────────────
                int liveIdx = HotelDataBase.getRoomTypes().indexOf(editingRoomType);
                if (liveIdx == -1) {
                    showFeedback("Room type no longer exists. Refreshing.", true);
                    renderCards();
                    closeForm();
                    return;
                }
                editingRoomType.update(updated);
                showFeedback("✔  Room type updated successfully.", false);
            }
            closeForm();
            renderCards();
        } catch (InvalidInputException ex) {
            showFeedback(ex.getMessage(), true);
        }
    }

    private void handleDelete(RoomType rt) {
        int liveIdx = HotelDataBase.getRoomTypes().indexOf(rt);
        if (liveIdx == -1) {
            showFeedback("Room type no longer exists. Refreshing.", true);
            renderCards();
            return;
        }

        // Check if in use before even showing dialog
        boolean inUse = HotelDataBase.reservations.stream()
                .anyMatch(r -> r.getRoom().getType().getSize().equalsIgnoreCase(rt.getSize())
                        && (r.getStatus() == Reservation.Status.PENDING
                        ||  r.getStatus() == Reservation.Status.CONFIRMED));

        if (inUse) {
            showFeedback("❌  Cannot delete \"" + rt.getSize() + "\" — it has active reservations.", true);
            return;
        }

        // Themed dialog
        Alert confirm = new Alert(Alert.AlertType.NONE);
        confirm.setTitle("Delete Room Type");
        confirm.setHeaderText(null);

        DialogPane pane = confirm.getDialogPane();
        pane.setStyle(
                "-fx-background-color: #0F2160;" +
                        "-fx-border-color: #C9A84C;" +
                        "-fx-border-width: 1.5;"
        );

        Label msg = new Label(
                "You are about to delete  \"" + rt.getSize() + "\".\n\nThis action cannot be undone."
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

        // Style the buttons after dialog is shown
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
                rt.delete(liveIdx);
                showFeedback("✔  \"" + rt.getSize() + "\" deleted successfully.", false);
                renderCards();
            } catch (Exception ex) {
                showFeedback("❌  " + ex.getMessage(), true);
            }
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void showForm() {
        formPanel.setVisible(true);
        formPanel.setManaged(true);
    }

    private void showFeedback(String msg, boolean isError) {
        feedbackLabel.setText(msg);
        feedbackLabel.setStyle(isError
                ? "-fx-text-fill: #B00020; -fx-font-size: 13px; -fx-font-weight: bold;"
                : "-fx-text-fill: #1D9E75; -fx-font-size: 13px; -fx-font-weight: bold;");
        feedbackLabel.setVisible(true);
    }

    private void clearErrors() {
        MainController.clearErrors(sizeError, priceError, capacityError);
        feedbackLabel.setVisible(false);
    }

    /**
     * Returns a luxury emoji that hints at the room tier,
     * based on common size names or capacity.
     */
    private String roomTypeIcon(String size, int capacity) {
        String s = size.toLowerCase();
        if (s.contains("presidential") || s.contains("royal")) return "\uD83C\uDFDB"; // 🏛
        if (s.contains("pent") || s.contains("penthouse"))     return "\uD83C\uDF06"; // 🌆
        if (s.contains("suite"))                               return "\uD83D\uDEC1"; // 🛁
        if (s.contains("triple") || capacity >= 3)             return "\uD83D\uDECF"; // 🛏 (triple)
        if (s.contains("double") || capacity == 2)             return "\uD83D\uDECF"; // 🛏
        if (s.contains("single") || capacity == 1)             return "\uD83D\uDECC"; // 🛌
        return "\uD83C\uDFE8"; // 🏨 default
    }
}
