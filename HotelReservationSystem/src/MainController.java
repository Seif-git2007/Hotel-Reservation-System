import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
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
        if(session.history.isEmpty()) {
            if(!file.equals("Login_Menu.fxml")&&!file.equals("Register_Menu.fxml")&&!file.equals("Forget_Password.fxml")&&!file.equals("Main_Menu.fxml")){
            session.history.push(file);
            }
        }
        else if(!session.history.peek().equals(file)){
            session.history.push(file);
        }
        load(event, file);
    }


    public void logout(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Alert confirm = new Alert(Alert.AlertType.NONE);
        confirm.setTitle("Sign Out");
        confirm.setHeaderText(null);

        DialogPane pane = confirm.getDialogPane();
        pane.setStyle(
                "-fx-background-color: #0F2160;" +
                        "-fx-border-color: #C9A84C;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        VBox content = new VBox(10);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setStyle("-fx-padding: 20 28 10 28;");

        Label icon = new Label("✦");
        icon.setStyle(
                "-fx-text-fill: #C9A84C;" +
                        "-fx-font-size: 28px;"
        );

        Label title = new Label("Leaving So Soon?");
        title.setStyle(
                "-fx-text-fill: #FFFFFF;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-style: italic;"
        );

        Label subtitle = new Label("Are you sure you want to sign out\nof your Kempinski account?");
        subtitle.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.60);" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-alignment: center;" +
                        "-fx-alignment: center;"
        );
        subtitle.setAlignment(javafx.geometry.Pos.CENTER);

        Pane rule = new Pane();
        rule.setPrefHeight(1);
        rule.setPrefWidth(200);
        rule.setStyle("-fx-background-color: rgba(201,168,76,0.35);");
        rule.setMaxWidth(Double.MAX_VALUE);

        content.getChildren().addAll(icon, title, subtitle, rule);
        pane.setContent(content);

        ButtonType btnYes = new ButtonType("Sign Out", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo  = new ButtonType("Stay",     ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        confirm.setOnShown(ev -> {
            Button yesButton = (Button) pane.lookupButton(btnYes);
            yesButton.setStyle(
                    "-fx-background-color: #B00020;" +
                            "-fx-text-fill: #FFFFFF;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 13px;" +
                            "-fx-padding: 10 32;" +
                            "-fx-background-radius: 6;" +
                            "-fx-cursor: hand;" +
                            "-fx-border-color: transparent;" +
                            "-fx-border-width: 0;"
            );
            yesButton.setMinWidth(100);
            yesButton.setOnMouseEntered(e ->
                    yesButton.setStyle(yesButton.getStyle()
                            .replace("-fx-background-color: #B00020;",
                                    "-fx-background-color: #8B0018;")));
            yesButton.setOnMouseExited(e ->
                    yesButton.setStyle(yesButton.getStyle()
                            .replace("-fx-background-color: #8B0018;",
                                    "-fx-background-color: #B00020;")));

            Button noButton = (Button) pane.lookupButton(btnNo);
            noButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #C9A84C;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 13px;" +
                            "-fx-padding: 10 32;" +
                            "-fx-border-color: #C9A84C;" +
                            "-fx-border-radius: 6;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-cursor: hand;"
            );
            noButton.setOnMouseEntered(e ->
                    noButton.setStyle(noButton.getStyle()
                            .replace("-fx-background-color: transparent;",
                                    "-fx-background-color: rgba(201,168,76,0.10);")));
            noButton.setOnMouseExited(e ->
                    noButton.setStyle(noButton.getStyle()
                            .replace("-fx-background-color: rgba(201,168,76,0.10);",
                                    "-fx-background-color: transparent;")));

            pane.lookup(".button-bar").setStyle("-fx-background-color: #0F2160; -fx-padding: 10 20 18 20;");
        });

        confirm.showAndWait().ifPresent(btn -> {
            if (btn != btnYes) return;
            ((AppSession) stage.getUserData()).logout();
            navigate(event, "Main_Menu.fxml");
        });
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
    @FXML
    public void back(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        AppSession session = (AppSession) stage.getUserData();

        if(!session.history.isEmpty()){
            session.history.pop();
            if(!session.history.isEmpty()){
                    load(event, session.history.peek());
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
    public static void cancelNoShows() {
        for (Reservation r : HotelDataBase.reservations) {
            if (r.getStatus() == Reservation.Status.PENDING
                    && r.getCheckInDate().isBefore(JumpInTime.now)) {
                r.setStatus(Reservation.Status.CANCELLED);
                DataBaseManager.runAsync(() -> {
                    DataBaseManager.updateReservationStatus(r);
                    EventBus.fire(EventBus.Event.RESERVATION_CHANGED);
                });
            }
        }
    }
    public static void handleOverDue(AppSession session)  {
        for(Reservation r:HotelDataBase.reservations){
            if(r.getGuest()==session.getCurrentGuest()&&r.getStatus()== Reservation.Status.CONFIRMED&&r.getCheckOutDate().isBefore(JumpInTime.now)){
                try {
                    if(session.getDueInvoice()==null){
                        session.setDueInvoice(r.getGuest().checkOut());
                    }
                    else if(!session.getDueInvoice().getReservation().contains(r)){
                        session.getDueInvoice().getReservation().add(r);
                    }
                    System.out.println("i entered");

                } catch (InvalidInputException e) {
                }
                r.setStatus(Reservation.Status.COMPLETED);
                DataBaseManager.updateReservationStatus(r);
                session.getCurrentGuest().setOverDue(true);
            }
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelNoShows();

    }
}
