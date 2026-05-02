import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import java.time.temporal.ChronoUnit;

public class ReservationFormController implements SessionController {

    @FXML private GuestSidebarController sidebarController;
    @FXML private DatePicker checkInPicker;
    @FXML private DatePicker checkOutPicker;
    @FXML private TextArea   specialRequestsArea;
    @FXML private Button     btnConfirmReservation;
    @FXML private Label      lblTotalNights;
    @FXML private Label      lblRoomType;
    @FXML private Label      lblPricePerNight;
    @FXML private Label      lblTotalCost;
    @FXML private Label      dateError;
    @FXML private VBox       vboxAmenities;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.btnMakeReservation.getStyleClass().add("sidebar-nav-btn-active");
        }

        ReservationContext ctx = session.getReservationContext();

        if (ctx.getCheckInDate() != null) {
            checkInPicker.setValue(ctx.getCheckInDate());
            checkOutPicker.setValue(ctx.getCheckOutDate());
            updateSummary();
        }

        lblRoomType.setText(ctx.getSelectedRoom().getType().getSize());
        lblPricePerNight.setText("$" + ctx.getSelectedRoom().getType().getBasePrice());

        vboxAmenities.getChildren().clear();
        for (Amenity amenity : ctx.getSelectedRoom().getAmenities()) {
            Label amenityLabel = new Label("• " + amenity.getName() + " | $" + amenity.getPrice());
            amenityLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
            vboxAmenities.getChildren().add(amenityLabel);
        }

        checkInPicker.valueProperty().addListener((obs, o, n) -> updateSummary());
        checkOutPicker.valueProperty().addListener((obs, o, n) -> updateSummary());
    }

    public void updateSummary() {
        MainController.clearErrors(dateError);
        if (checkInPicker.getValue() != null && checkOutPicker.getValue() != null) {
            try {
                Authenticator.validateReservationDates(checkInPicker.getValue(), checkOutPicker.getValue());
            } catch (InvalidInputException e) {
                lblTotalNights.setText("--");
                lblTotalCost.setText("$0.00");
                btnConfirmReservation.setDisable(true);
                MainController.setFieldError(dateError, e.getMessage());
                return;
            }
            long days = ChronoUnit.DAYS.between(checkInPicker.getValue(), checkOutPicker.getValue());
            if (days == 0) days = 1;
            lblTotalNights.setText("" + days);
            lblTotalCost.setText("$" + session.getReservationContext()
                .getSelectedRoom().calcTotal(checkInPicker.getValue(), checkOutPicker.getValue()));
            btnConfirmReservation.setDisable(false);
        } else {
            lblTotalNights.setText("--");
            lblTotalCost.setText("$0.00");
            btnConfirmReservation.setDisable(true);
        }
    }

    @FXML public void reserve(ActionEvent event) {
        MainController.clearErrors(dateError);
        if (checkInPicker.getValue() == null || checkOutPicker.getValue() == null) {
            MainController.setFieldError(dateError, "Please select both check-in and check-out dates");
            return;
        }
        ReservationContext ctx = session.getReservationContext();
        if (ctx.getSelectedRoom().isAvailable(checkInPicker.getValue(), checkOutPicker.getValue())) {
            session.getCurrentGuest().makeReservation(ctx.getSelectedRoom(), checkInPicker.getValue(), checkOutPicker.getValue(), specialRequestsArea.getText());
            ctx.clear();
            MainController.navigate(event, "ViewReservations.fxml");
        } else {
            MainController.setFieldError(dateError, "Selected room is not available for the chosen dates");
        }
    }
}
