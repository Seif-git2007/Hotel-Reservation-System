import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;

public class GuestProfileController implements Initializable {
    @FXML Label fullNameLabel;
    @FXML Label genderLabel;
    @FXML Label birthDateLabel ;
    @FXML Label balanceLabel;
    @FXML Label adressLabel;
    @FXML Label avatarInitialsLabel;
    @Override
    public void initialize(URL location, ResourceBundle resources){
        Guest guest = (Guest) MainController.getUser();
        fullNameLabel.setText(guest.getUsername());
        genderLabel.setText(guest.getGender().toString().substring(0,1).toUpperCase() + guest.getGender().toString().substring(1).toLowerCase());
        birthDateLabel.setText(guest.getDateOfBirth().toString());
        balanceLabel.setText(String.valueOf(guest.getBalance())+" $");
        adressLabel.setText(guest.getAddress());
        avatarInitialsLabel.setText(guest.getUsername().substring(0,1).toUpperCase());
    }
    public void back(ActionEvent event){
        if (!MainController.history.isEmpty()) {
            MainController.history.pop();
            if(!MainController.history.isEmpty()) {
                MainController.loadScene(event, MainController.history.peek());
            }
        }
    }

}
