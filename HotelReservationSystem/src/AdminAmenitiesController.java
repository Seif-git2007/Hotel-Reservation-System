import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

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

    // null  → Add mode      non-null → Edit mode
    private Amenity editingAmenity = null;

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

    private void renderCards() {
        amenityGrid.getChildren().clear();

        ArrayList<Amenity> list = HotelDataBase.getAmenities();

        if (list.isEmpty()) {
            // BUG FIX: no curly/smart quotes inside Java string literals
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

            // Gold top-strip acts as the photo accent
            Pane strip = new Pane();
            strip.setPrefHeight(6);
            strip.setStyle("-fx-background-color: #C9A84C; -fx-background-radius: 6 6 0 0;");

            // Large emoji icon — the "photo"
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
            // BUG FIX: pass the object, not an index captured from a copy-list iteration
            editBtn.setOnAction(e -> openEditForm(amenity));

            Button deleteBtn = new Button("Delete");
            deleteBtn.getStyleClass().add("btn-cancel-reservation");
            deleteBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(deleteBtn, Priority.ALWAYS);
            deleteBtn.setOnAction(e -> handleDelete(amenity));

            HBox actions = new HBox(8, editBtn, deleteBtn);
            actions.setAlignment(Pos.CENTER);

            card.getChildren().addAll(strip, icon, nameLabel, priceLabel, divider, actions);
            amenityGrid.getChildren().add(card);
        }
    }

    // ── Form ─────────────────────────────────────────────────────────────────
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
        // Cast to int so "400.0" shows as "400"
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
                // CREATE
                admin.addAmenity(name, price);
                showFeedback("Amenity \"" + name + "\" added successfully.", false);
            } else {
                // UPDATE — find the live index at submit time, not at card-render time
                int liveIndex = HotelDataBase.amenities.indexOf(editingAmenity);
                if (liveIndex == -1) {
                    showFeedback("Amenity no longer exists. Refreshing.", true);
                    renderCards();
                    closeForm();
                    return;
                }
                admin.updateAmenityPrice(name, price, liveIndex);
                showFeedback("Amenity updated successfully.", false);
            }
            closeForm();
            renderCards();
        } catch (InvalidInputException ex) {
            showFeedback(ex.getMessage(), true);
        }
    }

    private void handleDelete(Amenity amenity) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete amenity \"" + amenity.getName() + "\"? This cannot be undone.",
                ButtonType.YES, ButtonType.CANCEL);
        confirm.setHeaderText("Confirm Delete");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                // BUG FIX: resolve the live index at delete-time, not card-render time
                int liveIndex = HotelDataBase.amenities.indexOf(amenity);
                if (liveIndex == -1) {
                    showFeedback("Amenity already removed.", true);
                    renderCards();
                    return;
                }
                try {
                    admin.removeAmenity(liveIndex);
                    showFeedback("Amenity deleted.", false);
                    renderCards();
                } catch (InvalidInputException ex) {
                    showFeedback(ex.getMessage(), true);
                }
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
        if (n.contains("jac") || n.contains("tub"))    return "\uD83D\uDEC1"; // 🛁
        if (n.contains("spa") || n.contains("mass"))   return "\uD83D\uDC86"; // 💆
        if (n.contains("wifi") || n.contains("web"))   return "\uD83D\uDCF6"; // 📶
        if (n.contains("fridge") || n.contains("mini"))return "\uD83E\uDDCA"; // 🧊
        if (n.contains("pool") || n.contains("swim"))  return "\uD83C\uDFCA"; // 🏊
        if (n.contains("gym") || n.contains("fit"))    return "\uD83C\uDFCB"; // 🏋
        if (n.contains("bar") || n.contains("drink"))  return "\uD83C\uDF78"; // 🍸
        if (n.contains("breakfast") || n.contains("food")) return "\uD83C\uDF73"; // 🍳
        if (n.contains("park"))                        return "\uD83D\uDE97"; // 🚗
        if (n.contains("tv") || n.contains("view"))    return "\uD83D\uDCFA"; // 📺
        return "\u2728"; // ✨ default
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}

