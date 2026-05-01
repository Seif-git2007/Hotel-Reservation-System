import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GuestSidebarController {

    @FXML public Button btnViewRooms;
    @FXML public Button btnMakeReservation;
    @FXML public Button btnViewReservations;
    @FXML public Button btnCancelReservation;
    @FXML public Button btnCheckOut;


    @FXML private void goViewRooms(ActionEvent e)         { MainController.navigate(e, "ViewRooms.fxml"); }
    @FXML private void goMakeReservation(ActionEvent e)   { MainController.navigate(e, "MakeReservation.fxml"); }
    @FXML private void goViewReservations(ActionEvent e)  { MainController.navigate(e, "ViewReservations.fxml"); }
    @FXML private void goCancelReservation(ActionEvent e) { MainController.navigate(e, "CancelReservation.fxml"); }
    @FXML private void goCheckOut(ActionEvent e)          { MainController.navigate(e, "CheckOut.fxml"); }
}
