import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class ForgetPasswordController {
    @FXML TextField userName, email, password, confirmPassword;
    @FXML Label userNameError, emailError, passwordError, confirmPasswordError;

    private String validPassword;

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
