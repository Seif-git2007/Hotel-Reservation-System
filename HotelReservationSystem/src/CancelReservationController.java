import javafx.fxml.FXML;

public class CancelReservationController {

    @FXML private GuestSidebarController sidebarController;

    @FXML
    public void initialize() {
        if (sidebarController != null)
            sidebarController.btnCancelReservation.getStyleClass().add("sidebar-nav-btn-active");
    }
}
