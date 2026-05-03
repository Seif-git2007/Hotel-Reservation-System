import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainController {
    public static void load(ActionEvent event , String file){
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            AppSession session = (AppSession) stage.getUserData();
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource(file));
            Parent root = loader.load();
            if (loader.getController() instanceof SessionController sc) {
                sc.initSession(session);
            }
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    MainController.class.getResource("style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void navigate(ActionEvent event, String file) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        AppSession session = (AppSession) stage.getUserData();
        session.history.push(file);
        load(event, file);

    }


    public void logout(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ((AppSession) stage.getUserData()).logout();
        navigate(event, "Main_Menu.fxml");
    }

    public void home(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        AppSession session = (AppSession) stage.getUserData();
        User user = session.getCurrentUser();
        if (user instanceof Guest){
            navigate(event, "Guest_Dashboard.fxml");
        }
        else if (user instanceof Receptionist){
            navigate(event, "Receptionist_Menu.fxml");
        }
        else if (user instanceof Admin){
            navigate(event, "Admin_Menu.fxml");
        }
    }

    public void back(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        AppSession session = (AppSession) stage.getUserData();

        if(!session.history.isEmpty()){
            session.history.pop();
            if(!session.history.isEmpty()){
                if(session.history.peek().equals("Login_Menu.fxml")) {
                    load(event, "Login_Menu.fxml");
                    session.logout();
                }else {
                    load(event, session.history.peek());
                }
            }
        }

    }

    public void viewProfile(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        User user = ((AppSession) stage.getUserData()).getCurrentUser();
        if (user instanceof Guest) {
            navigate(event, "Guest_Profile.fxml");
        } else {
            navigate(event, "Staff_Profile.fxml");
        }
    }


    public void ChooseLogin(ActionEvent event) {
        navigate(event, "Login_Menu.fxml");
    }

    public void ChooseRegister(ActionEvent event) {
        navigate(event, "Register_Menu.fxml");
    }

    public static void setFieldError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    public static void clearErrors(Label... labels) {
        for (Label l : labels) l.setVisible(false);
    }
}
