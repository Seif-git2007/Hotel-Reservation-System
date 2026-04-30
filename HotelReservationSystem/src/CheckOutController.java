import javafx.fxml.FXML;

public class CheckOutController {

    @FXML private GuestSidebarController sidebarController;

    @FXML
    public void initialize() {
        if (sidebarController != null)
            sidebarController.btnCheckOut.getStyleClass().add("sidebar-nav-btn-active");
    }
}
