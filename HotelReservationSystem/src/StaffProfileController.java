import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StaffProfileController implements SessionController {

    @FXML private Label nameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label genderLabel;
    @FXML private Label birthDateLabel;
    @FXML private Label workingHoursLabel;
    @FXML private Label avatarInitialsLabel;
    @FXML private Label roleLabel;
    @FXML private Label roleFooter;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
        User user = session.getCurrentUser();

        avatarInitialsLabel.setText(
                user.getUsername().substring(0, 1).toUpperCase()
        );
        usernameLabel.setText(user.getUsername());
        emailLabel.setText(user.getEmail());
        genderLabel.setText(
                user.getGender().toString().substring(0, 1).toUpperCase()
                        + user.getGender().toString().substring(1).toLowerCase()
        );
        birthDateLabel.setText(user.getDateOfBirth().toString());

        if (user instanceof Admin admin) {
            nameLabel.setText(admin.getUsername());
            workingHoursLabel.setText(admin.getWorkingHours() + " hrs / week");
            roleLabel.setText("Kempinski Administrator Account");
            roleFooter.setText("Administrator Account");
        } else if (user instanceof Receptionist receptionist) {
            nameLabel.setText(receptionist.getUsername());
            workingHoursLabel.setText(receptionist.getWorkingHours() + " hrs / week");
            roleLabel.setText("Kempinski Receptionist Account");
            roleFooter.setText("Receptionist Account");
        }
    }

    public void back(ActionEvent event) {
        User user = session.getCurrentUser();
        if (user instanceof Admin) {
            MainController.navigate(event, "Admin_Menu.fxml");
        } else {
            MainController.navigate(event, "Receptionist_Menu.fxml");
        }
    }
}