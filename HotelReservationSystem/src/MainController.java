import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Stack;

public class MainController {
    static Stack<String> history = new Stack<>();
    private static User user;

    private static ReservationContext reservationContext = new ReservationContext();

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        MainController.user = user;
    }

    public static ReservationContext getReservationContext() {
        return reservationContext;
    }

    public static void setReservationContext(Room room, LocalDate checkIn, LocalDate checkOut) {
        reservationContext.setSelectedRoom(room);
        reservationContext.setCheckInDate(checkIn);
        reservationContext.setCheckOutDate(checkOut);
    }

    public static void loadScene(ActionEvent event, String file) {
        try {
            Parent root = FXMLLoader.load(MainController.class.getResource(file));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene =new Scene(root);
            String css = MainController.class.getResource("style.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
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
            navigate(event, "Guest_Dashboard.fxml");
        }else if(user instanceof Receptionist){
            navigate(event,"Receptionist_Menu.fxml");
        }else if(user instanceof Admin){
            navigate(event,"Admin_Menu.fxml");
        }
    }
    public static void setFieldError(Label label, String message){
        label.setText(message);
        label.setVisible(true);
    }
    public static void clearErrors(Label... labels) {
        for (Label l : labels) {
            l.setVisible(false);
        }
    }
    public void viewProfile(ActionEvent event){
        navigate(event,"Guest_Profile.fxml");
    }



}


