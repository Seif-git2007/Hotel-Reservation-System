import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GuestSidebarController implements SessionController {

    @FXML public Button btnViewRooms;
    @FXML public Button btnMakeReservation;
    @FXML public Button btnViewReservations;
    @FXML public Button btnCancelReservation;
    @FXML public Button btnCheckOut;
    @FXML public Button btnLiveChat;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
    }

    @FXML private void goViewRooms(ActionEvent e)         { MainController.navigate(e, "ViewRooms.fxml"); }
    @FXML private void goMakeReservation(ActionEvent e)   { MainController.navigate(e, "MakeReservation.fxml"); }
    @FXML private void goViewReservations(ActionEvent e)  { MainController.navigate(e, "ViewReservations.fxml"); }
    @FXML private void goCheckOut(ActionEvent e)          { MainController.navigate(e, "CheckOut.fxml"); }
    @FXML private void goLiveChat(ActionEvent e)          { MainController.navigate(e, "GuestChat.fxml"); }
}
