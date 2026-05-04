import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class LoginController {
    @FXML private TextField     name;
    @FXML private PasswordField password;
    @FXML private Label         nameError;
    @FXML private TextField passwordVisible;
    @FXML private Button    eyeBtn;

    public void login(ActionEvent event) {
        try {
            User user = User.Login(name.getText(), password.getText());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            AppSession session = (AppSession) stage.getUserData();
            session.setCurrentUser(user);
            if (!user.isLoggedIn()){
                if (user instanceof Guest) MainController.navigate(event, "Guest_Dashboard.fxml");
                else if (user instanceof Receptionist) MainController.navigate(event, "Receptionist_Menu.fxml");
                else if (user instanceof Admin) MainController.navigate(event, "Admin_Menu.fxml");
                user.setLoggedIn(true);
            }else {
                MainController.setFieldError(nameError, "User is already logged in");
            }
        } catch (InvalidInputException e) {
            MainController.setFieldError(nameError, e.getMessage());
        }
    }
    @FXML
    public void initialize() {
        passwordVisible.textProperty().addListener((obs, o, n) -> password.setText(n));
        password.textProperty().addListener((obs, o, n) -> {
            if (!passwordVisible.isVisible()) passwordVisible.setText(n);
        });
    }
    @FXML
    private void togglePassword() {
        if (!passwordVisible.isVisible()) {
            passwordVisible.setText(password.getText());
            password.setVisible(false);
            password.setManaged(false);
            passwordVisible.setVisible(true);
            passwordVisible.setManaged(true);
            eyeBtn.setText("🙈");
        } else {
            password.setText(passwordVisible.getText());
            passwordVisible.setVisible(false);
            passwordVisible.setManaged(false);
            password.setVisible(true);
            password.setManaged(true);
            eyeBtn.setText("👁");
        }
    }

    public void Back(ActionEvent event) {
        MainController.navigate(event, "Register_Menu.fxml");
    }

    public void forgetPassword(ActionEvent event) {
        MainController.navigate(event, "Forget_Password.fxml");
    }
}
