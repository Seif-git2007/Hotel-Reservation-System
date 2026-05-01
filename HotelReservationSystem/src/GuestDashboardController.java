import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GuestDashboardController {

    @FXML private Label welcomeName;

    @FXML
    public void initialize() {
        if (MainController.getUser() instanceof Guest) {
            welcomeName.setText(((Guest) MainController.getUser()).getDisplayname());
        } else {
            welcomeName.setText(MainController.getUser().getUsername());
        }
    }
}
