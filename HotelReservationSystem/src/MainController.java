import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

public class MainController {
    private static Stack<String> history = new Stack<>();
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        MainController.user = user;
    }

    private static void loadScene(ActionEvent event, String file) {
        try {
            Parent root = FXMLLoader.load(MainController.class.getResource(file));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ChooseLogin(ActionEvent event) {
        navigate(event, "Login_Menu.fxml");
    }

    public void ChooseRegister(ActionEvent event) {
        navigate(event, "Register_Menu.fxml");
    }

    public static void navigate(ActionEvent event, String file) {
        history.push(file);
        loadScene(event, file);
    }

    public void back(ActionEvent event) {

        if (!history.isEmpty()) {
            history.pop();
            if(!history.isEmpty()) {
                loadScene(event, history.peek());
            }
        }
    }

    public void logout(ActionEvent event) {
        history.clear();
        loadScene(event, "Main_Menu.fxml");
    }
    public void home(ActionEvent event){
        if(user instanceof Guest){
            navigate(event,"Guest_Menu.fxml");
        }else if(user instanceof Receptionist){
            navigate(event,"Receptionist_Menu.fxml");
        }else if(user instanceof Admin){
            navigate(event,"Admin_Menu.fxml");
        }
    }
}


