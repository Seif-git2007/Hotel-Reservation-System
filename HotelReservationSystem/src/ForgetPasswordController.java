import com.sun.tools.javac.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;


public class ForgetPasswordController {
    @FXML TextField userName;
    @FXML TextField email;
    @FXML TextField password;
    @FXML TextField confirmPassword;
    @FXML Label userNameError;
    @FXML Label emailError;
    @FXML Label passwordError;
    @FXML Label confirmPasswordError;
    private String validPassword;
    int cnt =0;
    public void confirmPassword(ActionEvent event) {
        MainController.clearErrors(userNameError,emailError,passwordError,confirmPasswordError);
        User user = HotelDataBase.searchUserByName(userName.getText());
        if (userName.getText().isEmpty()) {
            MainController.setFieldError(userNameError, "Please Enter user name");
        } else if (user == null) {
            MainController.setFieldError(userNameError, "Username not found");
        } else {
            cnt++;
            if (email.getText().isEmpty()) {
                MainController.setFieldError(emailError, "Please enter Email");
            }
            if (user.getEmail().equals(email.getText())) {
                cnt++;
                if (password.getText().isEmpty()) {
                    MainController.setFieldError(passwordError, "Please Enter Password");
                }
                if (confirmPassword.getText().isEmpty()) {
                    MainController.setFieldError(confirmPasswordError, "Please Confirm Password");
                }
                try {
                    validPassword = Authenticator.validatePassword(password.getText());
                    cnt++;
                } catch (InvalidInputException e) {
                    MainController.setFieldError(passwordError, e.getMessage());
                    return;
                }
                if(validPassword.equals(confirmPassword.getText())){
                    user.setPassword(validPassword);
                    MainController.navigate(event,"Login_Menu.fxml");
                }else{
                    MainController.setFieldError(confirmPasswordError,"Confirm Password Doesn't match");
                }

            } else {
                MainController.setFieldError(emailError, "Email doesn't match user name");
            }
        }
    }
    public void tologin(ActionEvent event){
        MainController.navigate(event,"Login_Menu.fxml");
    }




}
