import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;

import java.util.ArrayList;

public class AdminAmenitiesController implements SessionController {

    // ── FXML injections ──────────────────────────────────────────────────────
    @FXML private AdminSidebarController sidebarController;

    @FXML private Label     feedbackLabel;
    @FXML private VBox      formPanel;
    @FXML private Label     formTitle;
    @FXML private TextField fieldName;
    @FXML private TextField fieldPrice;
    @FXML private Label     nameError;
    @FXML private Label     priceError;
    @FXML private Button    btnFormSubmit;
    @FXML private FlowPane  amenityGrid;

    // ── State ────────────────────────────────────────────────────────────────
    private AppSession session;
    private Admin      admin;

    private Amenity editingAmenity = null;

    // ── SessionController ────────────────────────────────────────────────────
    @Override
    public void initSession(AppSession session) {
        this.session = session;
        this.admin   = (Admin) session.getCurrentUser();

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.btnAmenities.getStyleClass().add("sidebar-nav-btn-active");
        }

        renderCards();
    }

    // ── Card rendering ────────────────────────────────────────────────────────
    private void renderCards() {
        amenityGrid.getChildren().clear();

        ArrayList<Amenity> list = HotelDataBase.getAmenities();

        if (list.isEmpty()) {
            Label empty = new Label("No amenities yet. Use '+ Add Amenity' to create one.");
            empty.getStyleClass().add("main-content-hint");
            amenityGrid.getChildren().add(empty);
            return;
        }

        for (Amenity amenity : list) {

            VBox card = new VBox(12);
            card.setPrefWidth(190);
            card.setMaxWidth(190);
            card.getStyleClass().add("room-card");

            Pane strip = new Pane();
            strip.setPrefHeight(6);
            strip.setStyle("-fx-background-color: #C9A84C; -fx-background-radius: 6 6 0 0;");

            Label icon = new Label(amenityIcon(amenity.getName()));
            icon.setStyle("-fx-font-size: 36px; -fx-padding: 10 0 4 0;");
            icon.setMaxWidth(Double.MAX_VALUE);
            icon.setAlignment(Pos.CENTER);

            Label nameLabel = new Label(capitalize(amenity.getName()));
            nameLabel.getStyleClass().add("room-card-title");
            nameLabel.setStyle("-fx-font-size: 15px;");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            nameLabel.setAlignment(Pos.CENTER);

            Label priceLabel = new Label(String.format("$%.0f / stay", amenity.getPrice()));
            priceLabel.getStyleClass().add("room-card-status");
            priceLabel.setMaxWidth(Double.MAX_VALUE);
            priceLabel.setAlignment(Pos.CENTER);

            Pane divider = new Pane();
            divider.setPrefHeight(1);
            divider.setStyle("-fx-background-color: #E0DAD0;");

            Button editBtn = new Button("Edit");
            editBtn.getStyleClass().add("filter-btn-outline");
            editBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(editBtn, Priority.ALWAYS);
            editBtn.setOnAction(e -> openEditForm(amenity));
            boolean amenityEditLocked = HotelDataBase.reservations.stream()
                    .anyMatch(r -> r.getRoom().getAmenities().stream()
                            .anyMatch(a -> a.getName().equalsIgnoreCase(amenity.getName()))
                            && (r.getStatus() == Reservation.Status.PENDING
                            ||  r.getStatus() == Reservation.Status.CONFIRMED));
            editBtn.setDisable(amenityEditLocked);
            editBtn.setOpacity(amenityEditLocked ? 0.3 : 1.0);

            boolean amenityInUse = HotelDataBase.reservations.stream()
                    .anyMatch(r -> r.getRoom().getAmenities().contains(amenity) &&
                            (r.getStatus() == Reservation.Status.PENDING ||
                                    r.getStatus() == Reservation.Status.CONFIRMED));

            Button deleteBtn = new Button("Delete");
            deleteBtn.getStyleClass().add("btn-cancel-reservation");
            deleteBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(deleteBtn, Priority.ALWAYS);
            deleteBtn.setOnAction(e -> handleDelete(amenity));
            boolean deleteLocked = amenityEditLocked || amenityInUse;
            deleteBtn.setDisable(deleteLocked);
            deleteBtn.setOpacity(deleteLocked ? 0.3 : 1.0);

            HBox actions = new HBox(8, editBtn, deleteBtn);
            actions.setAlignment(Pos.CENTER);

            card.getChildren().addAll(strip, icon, nameLabel, priceLabel, divider, actions);
            amenityGrid.getChildren().add(card);
        }
    }

    // ── Form ──────────────────────────────────────────────────────────────────
    @FXML
    private void openAddForm() {
        editingAmenity = null;
        formTitle.setText("Add Amenity");
        btnFormSubmit.setText("Save Amenity");
        fieldName.clear();
        fieldPrice.clear();
        clearErrors();
        showForm();
    }

    private void openEditForm(Amenity amenity) {
        editingAmenity = amenity;
        formTitle.setText("Edit Amenity");
        btnFormSubmit.setText("Update Amenity");
        fieldName.setText(amenity.getName());
        fieldPrice.setText(String.valueOf((int) amenity.getPrice()));
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

        String name = fieldName.getText().trim();
        if (name.isEmpty()) {
            MainController.setFieldError(nameError, "Name cannot be empty.");
            valid = false;
        }

        double price = 0;
        try {
            price = Double.parseDouble(fieldPrice.getText().trim());
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            MainController.setFieldError(priceError, "Enter a valid positive number.");
            valid = false;
        }

        if (!valid) return;

        try {
            if (editingAmenity == null) {
                admin.addAmenity(name, price);
                closeForm();
                renderCards();
                showFeedback("✔  Amenity \"" + name + "\" added successfully.", false);
            } else {
                int liveIndex = HotelDataBase.amenities.indexOf(editingAmenity);
                if (liveIndex == -1) {
                    showFeedback("Amenity no longer exists. Refreshing.", true);
                    renderCards();
                    closeForm();
                    return;
                }
                admin.updateAmenityPrice(name, price, liveIndex);
                closeForm();
                renderCards();
                showFeedback("✔  Amenity updated successfully.", false);
            }
        } catch (InvalidInputException ex) {
            showFeedback(ex.getMessage(), true);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    private void handleDelete(Amenity amenity) {
        boolean inUse = HotelDataBase.reservations.stream()
                .anyMatch(r -> r.getRoom().getAmenities().contains(amenity) &&
                        (r.getStatus() == Reservation.Status.PENDING ||
                                r.getStatus() == Reservation.Status.CONFIRMED));

        if (inUse) {
            showFeedback("❌  Cannot delete \"" + amenity.getName() + "\" — it is in an active reservation.", true);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.NONE);
        confirm.setTitle("Delete Amenity");
        confirm.setHeaderText(null);

        DialogPane pane = confirm.getDialogPane();
        pane.setStyle(
                "-fx-background-color: #0F2160;" +
                        "-fx-border-color: #C9A84C;" +
                        "-fx-border-width: 1.5;"
        );

        Label msg = new Label(
                "You are about to delete \"" + amenity.getName() + "\".\n\nThis action cannot be undone."
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
            int liveIndex = HotelDataBase.amenities.indexOf(amenity);
            if (liveIndex == -1) {
                showFeedback("Amenity already removed.", true);
                renderCards();
                return;
            }
            try {
                admin.removeAmenity(liveIndex);
                showFeedback("✔  \"" + amenity.getName() + "\" deleted successfully.", false);
                renderCards();
            } catch (InvalidInputException ex) {
                showFeedback("❌  " + ex.getMessage(), true);
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
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
        MainController.clearErrors(nameError, priceError);
        feedbackLabel.setVisible(false);
    }

    private String amenityIcon(String name) {
        String n = name.toLowerCase();
        if (n.contains("jac") || n.contains("tub"))     return "\uD83D\uDEC1";
        if (n.contains("spa") || n.contains("mass"))    return "\uD83D\uDC86";
        if (n.contains("wifi") || n.contains("web"))    return "\uD83D\uDCF6";
        if (n.contains("fridge") || n.contains("mini")) return "\uD83E\uDDCA";
        if (n.contains("pool") || n.contains("swim"))   return "\uD83C\uDFCA";
        if (n.contains("gym") || n.contains("fit"))     return "\uD83C\uDFCB";
        if (n.contains("bar") || n.contains("drink"))   return "\uD83C\uDF78";
        if (n.contains("breakfast") || n.contains("food")) return "\uD83C\uDF73";
        if (n.contains("park"))                         return "\uD83D\uDE97";
        if (n.contains("tv") || n.contains("view"))     return "\uD83D\uDCFA";
        return "\u2728";
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
