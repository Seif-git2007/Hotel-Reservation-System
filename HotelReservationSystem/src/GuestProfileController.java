import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GuestProfileController implements SessionController {
    @FXML Label name;
    @FXML Label fullNameLabel;
    @FXML Label genderLabel;
    @FXML Label birthDateLabel;
    @FXML Label balanceLabel;
    @FXML Label adressLabel;
    @FXML Label avatarInitialsLabel;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
        Guest guest = session.getCurrentGuest();
        name.setText(guest.getDisplayname());
        fullNameLabel.setText(guest.getUsername());
        genderLabel.setText(guest.getGender().toString().substring(0, 1).toUpperCase()
            + guest.getGender().toString().substring(1).toLowerCase());
        birthDateLabel.setText(guest.getDateOfBirth().toString());
        balanceLabel.setText(guest.getBalance() + " $");
        adressLabel.setText(guest.getAddress());
        avatarInitialsLabel.setText(guest.getDisplayname().substring(0, 1).toUpperCase());
    }

    public void back(ActionEvent event) {
        MainController.navigate(event, "Guest_Dashboard.fxml");
    }
}
