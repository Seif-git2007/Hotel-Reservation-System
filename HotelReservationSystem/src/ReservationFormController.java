import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import java.time.temporal.ChronoUnit;

public class ReservationFormController {
    @FXML private GuestSidebarController sidebarController;
    @FXML private DatePicker checkInPicker;
    @FXML private DatePicker checkOutPicker;
    @FXML private TextArea specialRequestsArea;
    @FXML private Button btnConfirmReservation;
    @FXML private Label lblTotalNights;
    @FXML private Label lblRoomType;
    @FXML private Label lblPricePerNight;
    @FXML private Label lblTotalCost;
    @FXML private Label dateError;
    @FXML private VBox vboxAmenities;

    @FXML
    public void initialize() {
        if (sidebarController != null) {
            sidebarController.btnMakeReservation.getStyleClass().add("sidebar-nav-btn-active");
        }

        if(MainController.getReservationContext().getCheckInDate() != null) {
            checkInPicker.setValue(MainController.getReservationContext().getCheckInDate());
            checkOutPicker.setValue(MainController.getReservationContext().getCheckOutDate());
            updateSummary();
        }

        lblRoomType.setText(MainController.getReservationContext().getSelectedRoom().getType().getSize());
        lblPricePerNight.setText("$" + MainController.getReservationContext().getSelectedRoom().getType().getBasePrice());


        vboxAmenities.getChildren().clear();
        for (Amenity amenity : MainController.getReservationContext().getSelectedRoom().getAmenities()) {
            Label amenityLabel = new Label("• " + amenity.getName()+" | "+"$"+amenity.getPrice());
            amenityLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
            vboxAmenities.getChildren().add(amenityLabel);
        }

        checkInPicker.valueProperty().addListener((obs, oldVal, newVal) -> updateSummary());
        checkOutPicker.valueProperty().addListener((obs, oldVal, newVal) -> updateSummary());
    }

    public void updateSummary(){
        MainController.clearErrors(dateError);
        if(checkInPicker.getValue() != null && checkOutPicker.getValue() != null){
            try {
                Authenticator.validateReservationDates(checkInPicker.getValue(), checkOutPicker.getValue());
            } catch (InvalidInputException e){
                lblTotalNights.setText("--");
                lblTotalCost.setText("$0.00");
                btnConfirmReservation.setDisable(true);
                MainController.setFieldError(dateError, e.getMessage());
                return;
            }
            long daysStayed = ChronoUnit.DAYS.between(checkInPicker.getValue(), checkOutPicker.getValue()) == 0 ? 1 : ChronoUnit.DAYS.between(checkInPicker.getValue(), checkOutPicker.getValue());

            lblTotalNights.setText("" + daysStayed);
            lblTotalCost.setText("$" + MainController.getReservationContext().getSelectedRoom().calcTotal(checkInPicker.getValue(), checkOutPicker.getValue()));
            btnConfirmReservation.setDisable(false);
        } else {
            lblTotalNights.setText("--");
            lblTotalCost.setText("$0.00");
            btnConfirmReservation.setDisable(true);
        }
    }

    @FXML public void reserve(ActionEvent event){
        MainController.clearErrors(dateError);
        if(checkInPicker.getValue() == null || checkOutPicker.getValue() == null){
            MainController.setFieldError(dateError, "Please select both check-in and check-out dates");
            return;
        }
        ((Guest)MainController.getUser()).makeReservation(MainController.getReservationContext().getSelectedRoom(), checkInPicker.getValue(), checkOutPicker.getValue(), specialRequestsArea.getText());
        MainController.setReservationContext(null, null, null);
        MainController.navigate(event, "ViewReservations.fxml");
    }
}