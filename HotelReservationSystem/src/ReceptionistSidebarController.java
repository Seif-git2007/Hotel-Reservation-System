import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ReceptionistSidebarController implements SessionController {

    @FXML public Button btnDashboard;
    @FXML public Button btnCheckIn;
    @FXML public Button btnCheckOut;
    @FXML public Button btnReservations;
    @FXML public Button btnGuests;
    @FXML public Button btnRooms;
    @FXML public Button btnLiveChat;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
    }

    public void setActive(Button btn) {
        Button[] all = { btnDashboard, btnCheckIn, btnCheckOut,
                btnReservations, btnGuests, btnRooms, btnLiveChat };
        for (Button b : all) b.getStyleClass().remove("sidebar-nav-btn-active");
        if (btn != null) btn.getStyleClass().add("sidebar-nav-btn-active");
    }

    @FXML private void goDashboard(ActionEvent e)    { MainController.navigate(e, "Receptionist_Menu.fxml"); }
    @FXML private void goCheckIn(ActionEvent e)      { MainController.navigate(e, "ReceptionistCheckIn.fxml"); }
    @FXML private void goCheckOut(ActionEvent e)     { MainController.navigate(e, "ReceptionistCheckOut.fxml"); }
    @FXML private void goReservations(ActionEvent e) { MainController.navigate(e, "ReceptionistReservations.fxml"); }
    @FXML private void goGuests(ActionEvent e)       { MainController.navigate(e, "ReceptionistGuests.fxml"); }
    @FXML private void goRooms(ActionEvent e)        { MainController.navigate(e, "Receptionist_Rooms.fxml"); }
    @FXML private void goLiveChat(ActionEvent e)     { MainController.navigate(e, "ReceptionistChat.fxml"); }
}