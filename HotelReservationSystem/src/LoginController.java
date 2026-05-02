import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField     name;
    @FXML private PasswordField password;
    @FXML private Label         nameError;

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

    public void Back(ActionEvent event) {
        MainController.navigate(event, "Register_Menu.fxml");
    }

    public void forgetPassword(ActionEvent event) {
        MainController.navigate(event, "Forget_Password.fxml");
    }
}
