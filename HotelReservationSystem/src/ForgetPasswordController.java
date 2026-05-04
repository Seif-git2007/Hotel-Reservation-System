import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class ForgetPasswordController {

    @FXML private TextField     userName;
    @FXML private TextField     email;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmPassword;
    @FXML private Label         userNameError;
    @FXML private Label         emailError;
    @FXML private Label         passwordError;
    @FXML private Label         confirmPasswordError;

    @FXML private TextField passwordVisible;
    @FXML private TextField confirmPasswordVisible;
    @FXML private Button    eyeBtn;
    @FXML private Button    eyeBtn2;

    private String validPassword;

    @FXML
    public void initialize() {
        passwordVisible.textProperty().addListener((obs, o, n) -> password.setText(n));
        password.textProperty().addListener((obs, o, n) -> {
            if (!passwordVisible.isVisible()) passwordVisible.setText(n);
        });
        confirmPasswordVisible.textProperty().addListener((obs, o, n) -> confirmPassword.setText(n));
        confirmPassword.textProperty().addListener((obs, o, n) -> {
            if (!confirmPasswordVisible.isVisible()) confirmPasswordVisible.setText(n);
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

    @FXML
    private void toggleConfirmPassword() {
        if (!confirmPasswordVisible.isVisible()) {
            confirmPasswordVisible.setText(confirmPassword.getText());
            confirmPassword.setVisible(false);
            confirmPassword.setManaged(false);
            confirmPasswordVisible.setVisible(true);
            confirmPasswordVisible.setManaged(true);
            eyeBtn2.setText("🙈");
        } else {
            confirmPassword.setText(confirmPasswordVisible.getText());
            confirmPasswordVisible.setVisible(false);
            confirmPasswordVisible.setManaged(false);
            confirmPassword.setVisible(true);
            confirmPassword.setManaged(true);
            eyeBtn2.setText("👁");
        }
    }

    public void confirmPassword(ActionEvent event) {
        MainController.clearErrors(userNameError, emailError, passwordError, confirmPasswordError);
        User user = HotelDataBase.searchUserByName(userName.getText());

        if (userName.getText().isEmpty()) {
            MainController.setFieldError(userNameError, "Please Enter user name");
        } else if (user == null) {
            MainController.setFieldError(userNameError, "Username not found");
        } else {
            if (email.getText().isEmpty()) {
                MainController.setFieldError(emailError, "Please enter Email");
                return;
            }
            if (!user.getEmail().equals(email.getText())) {
                MainController.setFieldError(emailError, "Email doesn't match user name");
                return;
            }
            try {
                validPassword = Authenticator.validatePassword(password.getText());
            } catch (InvalidInputException e) {
                MainController.setFieldError(passwordError, e.getMessage());
                return;
            }
            if (validPassword.equals(confirmPassword.getText())) {
                user.setPassword(validPassword);
                MainController.navigate(event, "Login_Menu.fxml");
            } else {
                MainController.setFieldError(confirmPasswordError, "Confirm Password Doesn't match");
            }
        }
    }

    public void tologin(ActionEvent event) {
        MainController.navigate(event, "Login_Menu.fxml");
    }
}