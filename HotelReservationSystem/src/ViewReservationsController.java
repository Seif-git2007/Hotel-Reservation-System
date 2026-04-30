import javafx.fxml.FXML;

public class ViewReservationsController {

    @FXML private GuestSidebarController sidebarController;

    @FXML
    public void initialize() {
        if (sidebarController != null)
            sidebarController.btnViewReservations.getStyleClass().add("sidebar-nav-btn-active");
    }
}
