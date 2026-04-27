import com.sun.tools.javac.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField name;
    @FXML private PasswordField password;
    public void login(ActionEvent event){
        User user ;
        try {
            user = User.Login(name.getText(), password.getText());
            MainController.setUser(user);
                if (user instanceof Guest) {
                    System.out.println("Guest");
                    MainController.navigate(event,"Guest_Menu.fxml");
                } else if (user instanceof Receptionist) {
                    System.out.println("Receptionist");
                    MainController.navigate(event,"Receptionist_Menu.fxml");
                }else if(user instanceof Admin){
                    System.out.println("Admin");
                    MainController.navigate(event,"Admin_Menu.fxml");
                }
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
        }
    }
    public void Back(ActionEvent event){
        MainController.navigate(event,"Main_Menu.fxml");
    }
}
