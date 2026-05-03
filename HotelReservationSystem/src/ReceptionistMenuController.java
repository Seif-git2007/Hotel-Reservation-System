import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ReceptionistMenuController implements SessionController {

    @FXML private ReceptionistSidebarController sidebarController;
    @FXML private Label lblCheckInCount;
    @FXML private Label lblCheckOutCount;
    @FXML private Label lblReservationCount;
    @FXML private Label welcomeName;

    private AppSession   session;
    private Receptionist receptionist;

    @Override
    public void initSession(AppSession session) {
        this.session      = session;
        this.receptionist = (Receptionist) session.getCurrentUser();

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.setActive(sidebarController.btnDashboard);
        }

        welcomeName.setText(receptionist.getUsername());
        refreshCounts();
    }

    private void refreshCounts() {
        lblCheckInCount.setText(String.valueOf(HotelDataBase.getPendingGuests().size()));
        lblCheckOutCount.setText(String.valueOf(HotelDataBase.checktodayinvoices().size()));
        lblReservationCount.setText(String.valueOf(HotelDataBase.reservations.size()));
    }
}