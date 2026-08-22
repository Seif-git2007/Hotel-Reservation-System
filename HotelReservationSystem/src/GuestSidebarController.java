import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class GuestSidebarController implements SessionController {

    @FXML public Button btnViewRooms;
    @FXML public Button btnMakeReservation;
    @FXML public Button btnViewReservations;
    @FXML public Button btnCheckOut;
    @FXML public Button btnLiveChat;
    private final Runnable refreshListener = this::disable;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;

        disable();
        EventBus.subscribe(EventBus.Event.USER_CHANGED, refreshListener);
        EventBus.subscribe(EventBus.Event.TIME_JUMPED, refreshListener);

        btnLiveChat.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                EventBus.unsubscribe(EventBus.Event.USER_CHANGED, refreshListener);
                EventBus.unsubscribe(EventBus.Event.TIME_JUMPED, refreshListener);
            }
        });
    }
    public void disable() {
        if (!(session.getCurrentUser() instanceof Guest)) return;
        MainController.handleOverDue(session);

        if (!session.getCurrentGuest().isOverDue()) return;

        btnLiveChat.setDisable(true);
        btnMakeReservation.setDisable(true);
        btnCheckOut.setDisable(true);
        btnViewReservations.setDisable(true);
        btnViewRooms.setDisable(true);

        if (!session.history.isEmpty() && session.history.peek().equals("CheckOut.fxml")) return;

        whenAttached(() -> {
            ActionEvent event = new ActionEvent(btnViewRooms, null);
            MainController.navigate(event, "CheckOut.fxml");
        });
    }

    private void whenAttached(Runnable action) {
        if (btnViewRooms.getScene() != null && btnViewRooms.getScene().getWindow() != null) {
            javafx.application.Platform.runLater(() -> {
                if (session.getCurrentUser() == null) return;
                if (!(session.getCurrentUser() instanceof Guest)) return;
                action.run();
            });
            return;
        }
        btnViewRooms.sceneProperty().addListener((sObs, oldS, newS) -> {
            if (newS == null) return;
            if (newS.getWindow() != null) {
                javafx.application.Platform.runLater(() -> {
                    if (session.getCurrentUser() == null) return;
                    if (!(session.getCurrentUser() instanceof Guest)) return;
                    action.run();
                });
                return;
            }
            newS.windowProperty().addListener((wObs, oldW, newW) -> {
                if (newW != null) {
                    javafx.application.Platform.runLater(() -> {
                        if (session.getCurrentUser() == null) return;
                        if (!(session.getCurrentUser() instanceof Guest)) return;
                        action.run();
                    });
                }
            });
        });
    }
    @FXML private void goViewRooms(ActionEvent e)         { MainController.navigate(e, "ViewRooms.fxml"); }
    @FXML private void goMakeReservation(ActionEvent e)   { MainController.navigate(e, "MakeReservation.fxml"); }
    @FXML private void goViewReservations(ActionEvent e)  { MainController.navigate(e, "ViewReservations.fxml"); }
    @FXML private void goCheckOut(ActionEvent e)          { MainController.navigate(e, "CheckOut.fxml"); }
    @FXML private void goLiveChat(ActionEvent e)          { MainController.navigate(e, "GuestChat.fxml"); }
}
