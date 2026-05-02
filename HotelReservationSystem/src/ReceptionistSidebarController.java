import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ReceptionistSidebarController implements SessionController {

    // These are public so host controllers can call
    // btn.getStyleClass().add("sidebar-nav-btn-active") — same pattern as GuestSidebarController
    @FXML public Button btnDashboard;
    @FXML public Button btnCheckIn;
    @FXML public Button btnCheckOut;
    @FXML public Button btnReservations;
    @FXML public Button btnGuestSearch;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
    }

    @FXML private void goDashboard(ActionEvent e)    { MainController.navigate(e, "Receptionist_Menu.fxml"); }
    @FXML private void goCheckIn(ActionEvent e)      { MainController.navigate(e, "ReceptionistCheckIn.fxml"); }
    @FXML private void goCheckOut(ActionEvent e)     { MainController.navigate(e, "ReceptionistCheckOut.fxml"); }
    @FXML private void goReservations(ActionEvent e) { MainController.navigate(e, "ReceptionistReservations.fxml"); }
    @FXML private void goGuestSearch(ActionEvent e)  { MainController.navigate(e, "ReceptionistGuestSearch.fxml"); }
}
