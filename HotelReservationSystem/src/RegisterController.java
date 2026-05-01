import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class RegisterController implements Initializable {
    @FXML private Label nameError, passwordError, emailError, genderEmpty,
                        dateEmpty, addressEmpty, balanceError, floorError,
                        viewEmpty, displayNameEmpty;
    @FXML private TextField     displayName, name, email, address, balance, prefFloor;
    @FXML private PasswordField password;
    @FXML private ComboBox<User.Gender>  genderCombo;
    @FXML private DatePicker            dateOfBirth;
    @FXML private ComboBox<Room.view>   prefView;

    private String     validDisplayName, validname, validemail, validPassword, validAddress;
    private User.Gender gender;
    private LocalDate   birthDate;
    private double      validBalance;
    private int         validFloor;
    private Room.view   view;
    private int cnt = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UnaryOperator<TextFormatter.Change> decimalFilter = c ->
            c.getControlNewText().matches("\\d*(\\.\\d{0,2})?") ? c : null;
        UnaryOperator<TextFormatter.Change> integerFilter = c ->
            c.getControlNewText().matches("\\d*") ? c : null;

        prefFloor.setTextFormatter(new TextFormatter<>(integerFilter));
        balance.setTextFormatter(new TextFormatter<>(decimalFilter));
        genderCombo.getItems().setAll(User.Gender.values());
        prefView.getItems().setAll(Room.view.values());
    }

    public void Register(ActionEvent event) {
        MainController.clearErrors(nameError, passwordError, genderEmpty, dateEmpty,
            addressEmpty, balanceError, floorError, viewEmpty, displayNameEmpty, emailError);
        cnt = 0;

        if (!displayName.getText().isEmpty()) { validDisplayName = displayName.getText(); cnt++; }
        else MainController.setFieldError(displayNameEmpty, "Please enter Display Name");

        try { validname = Authenticator.validateName(name.getText()); cnt++; }
        catch (InvalidInputException e) { MainController.setFieldError(nameError, e.getMessage()); }

        try { validemail = Authenticator.validateEmail(email.getText()); cnt++; }
        catch (InvalidInputException e) { MainController.setFieldError(emailError, e.getMessage()); }

        try { validPassword = Authenticator.validatePassword(password.getText()); cnt++; }
        catch (InvalidInputException e) { MainController.setFieldError(passwordError, e.getMessage()); }

        if (genderCombo.getValue() != null) { gender = genderCombo.getValue(); cnt++; }
        else MainController.setFieldError(genderEmpty, "Please select Gender");

        try { birthDate = Authenticator.validateBirthDate(dateOfBirth.getValue()); cnt++; }
        catch (InvalidInputException e) { MainController.setFieldError(dateEmpty, e.getMessage()); }

        if (!address.getText().isEmpty()) { validAddress = address.getText(); cnt++; }
        else MainController.setFieldError(addressEmpty, "Please enter Address");

        if (!balance.getText().isEmpty()) { validBalance = Double.parseDouble(balance.getText()); cnt++; }
        else MainController.setFieldError(balanceError, "Please enter Balance");

        if (!prefFloor.getText().isEmpty()) { validFloor = Integer.parseInt(prefFloor.getText()); cnt++; }
        else MainController.setFieldError(floorError, "Please enter a Floor");

        if (prefView.getValue() != null) { view = prefView.getValue(); cnt++; }
        else MainController.setFieldError(viewEmpty, "Please select a View");

        if (cnt == 10) {
            Guest guest = new Guest();
            guest.Register(validname, validPassword, gender.toString(), validBalance,
                birthDate, validAddress, new roomPreferences(validFloor, view),
                validDisplayName, validemail);
            MainController.navigate(event, "Login_Menu.fxml");
        }
    }

    public void toLogin(ActionEvent event) {
        MainController.navigate(event, "Login_Menu.fxml");
    }
}
