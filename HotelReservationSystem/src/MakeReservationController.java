import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MakeReservationController {

    @FXML private GuestSidebarController sidebarController;
    @FXML DatePicker checkInDate;
    @FXML DatePicker checkOutDate;
    @FXML VBox roomContainer;
    @FXML Label errorLabel;

    @FXML
    public void initialize() {
        if (sidebarController != null)
            sidebarController.btnMakeReservation.getStyleClass().add("sidebar-nav-btn-active");
    }

    public void search(ActionEvent event) {
        MainController.clearErrors(errorLabel);

        try{
            Authenticator.validateReservationDates(checkInDate.getValue(),checkOutDate.getValue());
            ViewRoomsController.renderRooms(HotelDataBase.getAvailableRooms(checkInDate.getValue(), checkOutDate.getValue()), roomContainer,event);

        } catch (InvalidInputException e) {
            MainController.setFieldError(errorLabel,e.getMessage());
        }
    }
}
