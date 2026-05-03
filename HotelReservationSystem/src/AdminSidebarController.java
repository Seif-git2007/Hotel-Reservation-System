import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminSidebarController implements SessionController {

    @FXML public Button btnDashboard;
    @FXML public Button btnRooms;
    @FXML public Button btnRoomTypes;
    @FXML public Button btnAmenities;
    @FXML public Button btnStaff;
    @FXML public Button btnGuests;
    @FXML public Button btnReservations;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
    }

    /** Call this from the host controller to highlight the active page button. */
    public void setActive(Button btn) {
        clearActive();
        if (btn != null) btn.getStyleClass().add("sidebar-nav-btn-active");
    }

    private void clearActive() {
        Button[] all = { btnDashboard, btnRooms, btnRoomTypes,
                         btnAmenities, btnStaff, btnGuests, btnReservations };
        for (Button b : all) b.getStyleClass().remove("sidebar-nav-btn-active");
    }

    @FXML private void goDashboard(ActionEvent e)    { MainController.navigate(e, "Admin_Menu.fxml"); }
    @FXML private void goRooms(ActionEvent e)        { MainController.navigate(e, "Admin_Rooms.fxml"); }
    @FXML private void goRoomTypes(ActionEvent e)    { MainController.navigate(e, "AdminRoomTypes.fxml"); }
    @FXML private void goAmenities(ActionEvent e)    { MainController.navigate(e, "AdminAmenities.fxml"); }
    @FXML private void goStaff(ActionEvent e)        { MainController.navigate(e, "Admin_Staff.fxml"); }
    @FXML private void goGuests(ActionEvent e)       { MainController.navigate(e, "Admin_Guests.fxml"); }
    @FXML private void goReservations(ActionEvent e) { MainController.navigate(e, "Admin_Reservations.fxml"); }
}
