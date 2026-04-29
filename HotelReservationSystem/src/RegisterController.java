import com.sun.tools.javac.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class RegisterController implements Initializable {
    @FXML private Label nameError;
    @FXML private Label passwordError;
    @FXML private Label emailError;
    @FXML private Label genderEmpty;
    @FXML private Label dateEmpty;
    @FXML private Label addressEmpty;
    @FXML private Label balanceError;
    @FXML private Label floorError;
    @FXML private Label viewEmpty;
    @FXML private Label displayNameEmpty;
    @FXML private TextField displayName;
    @FXML private TextField name;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private ComboBox<User.Gender> genderCombo;
    @FXML private DatePicker dateOfBirth;
    @FXML private TextField address;
    @FXML private TextField balance;
    @FXML private TextField prefFloor;
    @FXML private ComboBox<Room.view> prefView;
    private String validDisplayName;
    private String validname;
    private String validemail;
    private String validPassword;
    User.Gender gender;
    LocalDate birthDate;
    String validAddress;
    double validBalance;
    int validFloor;
    Room.view view;
    int cnt=0;
    @Override
    public void initialize(URL location, ResourceBundle resources){
        UnaryOperator<TextFormatter.Change> decimalFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        };
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(integerFilter);
        prefFloor.setTextFormatter(textFormatter);

        TextFormatter<String> formatter = new TextFormatter<>(decimalFilter);
        balance.setTextFormatter(formatter);

        genderCombo.getItems().setAll(User.Gender.values());
        prefView.getItems().setAll(Room.view.values());
    }

    public void Register(ActionEvent event){
        MainController.clearErrors(nameError,passwordError,genderEmpty,dateEmpty,addressEmpty,balanceError,floorError,viewEmpty,displayNameEmpty);
        if(!displayName.getText().isEmpty()){
            validDisplayName=displayName.getText();
            cnt++;
        }else {
            MainController.setFieldError(displayNameEmpty,"Please enter Display Name");
        }
        try{
            validname=Authenticator.validateName(name.getText());
            cnt++;
        }catch (InvalidInputException e){
            MainController.setFieldError(nameError, e.getMessage());
        }
        try{
            validemail=Authenticator.validateName(email.getText());
            cnt++;
        }catch (InvalidInputException e){
            MainController.setFieldError(emailError, e.getMessage());
        }
        try{
            validPassword=Authenticator.validatePassword(password.getText());
            cnt++;
        }catch (InvalidInputException e){
            MainController.setFieldError(passwordError, e.getMessage());
        }

        if(genderCombo.getValue()!=null) {
            gender = genderCombo.getValue();
            cnt++;
        }else {
            MainController.setFieldError(genderEmpty, "Please select Gender");
        }

        if(dateOfBirth.getValue()!=null) {
            birthDate = dateOfBirth.getValue();
            cnt++;
        }else {
            MainController.setFieldError(dateEmpty, "Please select Date of Birth");
        }

        if(!address.getText().isEmpty()){
            validAddress=address.getText();
            cnt++;
        }else {
            MainController.setFieldError(addressEmpty,"Please enter Address");
        }

        if(!balance.getText().isEmpty()){
            validBalance = Double.parseDouble(balance.getText());
            cnt++;
        }else {
            MainController.setFieldError(balanceError,"Please enter Balance");
        }
        if(!prefFloor.getText().isEmpty()){
            validFloor =Integer.parseInt(prefFloor.getText());
            cnt++;
        }else {
            MainController.setFieldError(floorError,"Please enter a Floor");
        }

        if(prefView.getValue()!=null) {
            view=prefView.getValue();
            cnt++;
        }else {
            MainController.setFieldError(viewEmpty, "Please select a View");
        }
        if(cnt==10){
            Guest guest=new Guest();
            roomPreferences r=new roomPreferences(validFloor,view);
            guest.Register(validname,validPassword,gender.toString(),validBalance,birthDate,validAddress,r,validDisplayName,validemail);
            System.out.println("I registered");
            MainController.navigate(event, "Login_Menu.fxml");
        }
         cnt=0;
    }
    public void toLogin(ActionEvent event){
        MainController.navigate(event,"Login_Menu.fxml");
    }

}
