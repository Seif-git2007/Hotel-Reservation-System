import com.sun.tools.javac.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {
    @FXML private Label nameError;
    @FXML private Label passwordError;
    @FXML private TextField name;
    @FXML private PasswordField password;
    private String validname;
    private String validPassword;
    public void Register(){
        MainController.clearErrors(nameError,passwordError);
        try{
            validname=Authenticator.validateName(name.getText());

        }catch (InvalidInputException e){
            MainController.setFieldError(nameError, e.getMessage());
        }
        try{
            validPassword=Authenticator.validatePassword(password.getText());
            passwordError.setText(" ");
        }catch (InvalidInputException e){
            MainController.setFieldError(passwordError, e.getMessage());
        }
    }
    public void toLogin(ActionEvent event){
        MainController.navigate(event,"Login_Menu.fxml");
    }
}
