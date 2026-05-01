public class CancelReservationFormController implements SessionController {
    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
    }

}
