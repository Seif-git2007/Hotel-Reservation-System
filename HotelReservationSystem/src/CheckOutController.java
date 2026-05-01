import javafx.fxml.FXML;

public class CheckOutController implements SessionController {

    @FXML private GuestSidebarController sidebarController;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.btnCheckOut.getStyleClass().add("sidebar-nav-btn-active");
        }
    }
}
