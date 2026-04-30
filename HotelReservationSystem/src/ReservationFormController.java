import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;

public class ReservationFormController {
    @FXML
    private GuestSidebarController sidebarController;

    @FXML
    public void initialize() {
        if (sidebarController != null)
            sidebarController.btnMakeReservation.getStyleClass().add("sidebar-nav-btn-active");
    }

}
