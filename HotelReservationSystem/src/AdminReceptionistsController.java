import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.PasswordField;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AdminReceptionistsController implements SessionController {

    @FXML private AdminSidebarController sidebarController;
    @FXML private TextField              searchField;
    @FXML private VBox                   receptionistList;
    @FXML private Label                  lblTotalReceptionists;
    @FXML private Label                  lblTotalHours;

    // ── Form fields ──────────────────────────────────────────────────────────
    @FXML private Label        feedbackLabel;
    @FXML private VBox         formPanel;
    @FXML private Label        formTitle;
    @FXML private TextField    fieldUsername;
    @FXML private PasswordField fieldPassword;
    @FXML private TextField    fieldEmail;
    @FXML private DatePicker   fieldDob;
    @FXML private ComboBox<String> fieldGender;
    @FXML private TextField    fieldHours;
    @FXML private Label        usernameError;
    @FXML private Label        passwordError;
    @FXML private Label        emailError;
    @FXML private Label        dobError;
    @FXML private Label        genderError;
    @FXML private Label        hoursError;

    private AppSession session;
    private Admin      admin;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
        this.admin   = (Admin) session.getCurrentUser();
        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.setActive(sidebarController.btnReceptionists);
        }

        fieldGender.getItems().addAll("MALE", "FEMALE");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> renderList());

        updateStats();
        renderList();
    }

    // ── Form ─────────────────────────────────────────────────────────────────

    @FXML
    private void openAddForm() {
        fieldUsername.clear();
        fieldPassword.clear();
        fieldEmail.clear();
        fieldDob.setValue(null);
        fieldGender.getSelectionModel().clearSelection();
        fieldHours.clear();
        clearErrors();
        formPanel.setVisible(true);
        formPanel.setManaged(true);
        feedbackLabel.setVisible(false);
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

        // Username
        String username = fieldUsername.getText().trim();
        if (username.isEmpty()) {
            MainController.setFieldError(usernameError, "Username cannot be empty.");
            valid = false;
        }

        // Password
        String password = fieldPassword.getText();
        try {
            Authenticator.validatePassword(password);
        } catch (InvalidInputException e) {
            MainController.setFieldError(passwordError, e.getMessage());
            valid = false;
        }

        // Email
        String email = fieldEmail.getText().trim();
        if (email.isEmpty()) {
            MainController.setFieldError(emailError, "Email cannot be empty.");
            valid = false;
        }

        // DOB
        LocalDate dob = fieldDob.getValue();
        if (dob == null) {
            MainController.setFieldError(dobError, "Please select a date of birth.");
            valid = false;
        }

        // Gender
        String gender = fieldGender.getValue();
        if (gender == null) {
            MainController.setFieldError(genderError, "Please select a gender.");
            valid = false;
        }

        // Hours
        int hours = 0;
        try {
            hours = Integer.parseInt(fieldHours.getText().trim());
            if (hours <= 0 || hours > 12)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            MainController.setFieldError(hoursError, "Enter a valid number between 1 and 12.");
            valid = false;
        }

        if (!valid) return;

        try {
            admin.addReceptionists(username, password, dob, hours, gender, email);
            closeForm();
            updateStats();
            renderList();
            showFeedback("✔  Receptionist \"" + username + "\" added successfully.", false);
        } catch (InvalidInputException e) {
            showFeedback("❌  " + e.getMessage(), true);
        }
    }

    // ── Stats + list (unchanged) ─────────────────────────────────────────────

    private void updateStats() {
        List<Receptionist> all = HotelDataBase.getReceptionists();
        lblTotalReceptionists.setText("Total Receptionists: " + all.size());
        int totalHours = all.stream().mapToInt(Receptionist::getWorkingHours).sum();
        lblTotalHours.setText("Combined Working Hours: " + totalHours + " hrs / week");
    }

    private void renderList() {
        receptionistList.getChildren().clear();

        String search = searchField.getText().trim().toLowerCase();
        List<Receptionist> filtered = HotelDataBase.getReceptionists().stream()
                .filter(r -> search.isEmpty()
                        || r.getUsername().toLowerCase().contains(search))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            Label empty = new Label("No receptionists found.");
            empty.setStyle("-fx-text-fill: #6B6B6B; -fx-font-size: 14px; -fx-padding: 40;");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            receptionistList.getChildren().add(empty);
            return;
        }

        for (Receptionist r : filtered) {
            receptionistList.getChildren().add(buildCard(r));
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void showFeedback(String msg, boolean isError) {
        feedbackLabel.setText(msg);
        feedbackLabel.setStyle(isError
                ? "-fx-text-fill: #B00020; -fx-font-size: 13px; -fx-font-weight: bold;"
                : "-fx-text-fill: #1D9E75; -fx-font-size: 13px; -fx-font-weight: bold;");
        feedbackLabel.setVisible(true);
    }

    private void clearErrors() {
        MainController.clearErrors(
                usernameError, passwordError, emailError,
                dobError, genderError, hoursError
        );
    }

    // ── Card + detailCell (completely unchanged) ─────────────────────────────

    private HBox buildCard(Receptionist r) {
        HBox card = new HBox(0);
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #E0DAD0;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 0.5;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(15,33,96,0.06), 8, 0, 0, 2);"
        );

        VBox avatarPanel = new VBox(0);
        avatarPanel.setAlignment(Pos.CENTER);
        avatarPanel.setMinWidth(80);
        avatarPanel.setPrefWidth(80);
        avatarPanel.setStyle(
                "-fx-background-color: #0F2160;" +
                        "-fx-background-radius: 10 0 0 10;"
        );

        Pane topAccent = new Pane();
        topAccent.setPrefHeight(4);
        topAccent.setStyle("-fx-background-color: #C9A84C; -fx-background-radius: 10 0 0 0;");

        Label avatarLabel = new Label(r.getUsername().substring(0, 1).toUpperCase());
        avatarLabel.setStyle(
                "-fx-text-fill: #C9A84C;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-style: italic;" +
                        "-fx-background-color: rgba(201,168,76,0.15);" +
                        "-fx-background-radius: 50;" +
                        "-fx-min-width: 50;" +
                        "-fx-min-height: 50;" +
                        "-fx-alignment: center;"
        );
        avatarLabel.setMinSize(50, 50);
        avatarLabel.setAlignment(Pos.CENTER);

        Label genderLabel = new Label(r.getGender() == User.Gender.MALE ? "♂" : "♀");
        genderLabel.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.40);" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 4 0 0 0;"
        );

        VBox avatarContent = new VBox(4, avatarLabel, genderLabel);
        avatarContent.setAlignment(Pos.CENTER);
        VBox.setVgrow(avatarContent, Priority.ALWAYS);
        avatarPanel.getChildren().addAll(topAccent, avatarContent);

        VBox content = new VBox(0);
        HBox.setHgrow(content, Priority.ALWAYS);

        HBox contentHeader = new HBox(0);
        contentHeader.setAlignment(Pos.CENTER_LEFT);
        contentHeader.setStyle("-fx-padding: 12 16 8 16;");

        VBox nameBlock = new VBox(2);
        HBox.setHgrow(nameBlock, Priority.ALWAYS);

        Label userName = new Label(r.getUsername());
        userName.setStyle(
                "-fx-text-fill: #0F2160;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;"
        );
        Label emailLabel = new Label(r.getEmail());
        emailLabel.setStyle("-fx-text-fill: #9B9589; -fx-font-size: 11px;");
        nameBlock.getChildren().addAll(userName, emailLabel);

        Label hoursChip = new Label(r.getWorkingHours() + " hrs / week");
        hoursChip.setStyle(
                "-fx-text-fill: #0F2160;" +
                        "-fx-background-color: #C9A84C;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 4 12;" +
                        "-fx-background-radius: 20;"
        );

        contentHeader.getChildren().addAll(nameBlock, hoursChip);

        Pane divider = new Pane();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: #F0EDE4;");

        HBox details = new HBox(0);
        details.setStyle(
                "-fx-padding: 10 16 12 16;" +
                        "-fx-background-color: #FDFAF4;" +
                        "-fx-background-radius: 0 0 10 0;"
        );
        details.getChildren().addAll(
                detailCell("📅", "Date of Birth", r.getDateOfBirth().toString(), true),
                detailCell("⚧", "Gender",
                        r.getGender().toString().substring(0, 1).toUpperCase() +
                                r.getGender().toString().substring(1).toLowerCase(), true),
                detailCell("🕐", "Working Hours", r.getWorkingHours() + " hours per week", false)
        );

        content.getChildren().addAll(contentHeader, divider, details);
        card.getChildren().addAll(avatarPanel, content);

        card.setOnMouseEntered(e -> card.setStyle(card.getStyle()
                .replace("-fx-border-color: #E0DAD0;", "-fx-border-color: #C9A84C;")
                .replace("rgba(15,33,96,0.06), 8", "rgba(15,33,96,0.12), 12")));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle()
                .replace("-fx-border-color: #C9A84C;", "-fx-border-color: #E0DAD0;")
                .replace("rgba(15,33,96,0.12), 12", "rgba(15,33,96,0.06), 8")));

        return card;
    }

    private VBox detailCell(String icon, String label, String value, boolean rightBorder) {
        VBox cell = new VBox(3);
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.setStyle(
                "-fx-padding: 0 16 0 0;" +
                        (rightBorder
                                ? "-fx-border-color: transparent #E0DAD0 transparent transparent;" +
                                "-fx-border-width: 0 1 0 0; -fx-padding: 0 16 0 0;"
                                : "")
        );
        HBox.setHgrow(cell, Priority.ALWAYS);
        HBox.setMargin(cell, new javafx.geometry.Insets(0, rightBorder ? 16 : 0, 0, 0));

        Label lbl = new Label(icon + "  " + label.toUpperCase());
        lbl.setStyle(
                "-fx-text-fill: #9B9589;" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 0.12em;"
        );
        Label val = new Label(value);
        val.setStyle(
                "-fx-text-fill: #2C2C2C;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );
        val.setWrapText(true);
        cell.getChildren().addAll(lbl, val);
        return cell;
    }
}