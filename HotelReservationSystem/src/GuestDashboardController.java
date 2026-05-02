import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GuestDashboardController implements SessionController {

    @FXML private Label                  welcomeName;
    @FXML private GuestSidebarController sidebarController;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
        welcomeName.setText(session.getCurrentGuest().getDisplayname());
        if (sidebarController != null) sidebarController.initSession(session);
    }
}
