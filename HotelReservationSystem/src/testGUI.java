import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class testGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setUserData(new AppSession());
            Parent root = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                MainController.class.getResource("style.css").toExternalForm());

            primaryStage.setTitle("Kempinski Hotel - User 1");
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("icon.png"));
            primaryStage.show();
            primaryStage.setX(200);


            Stage stage2= new Stage();
            stage2.setUserData(new AppSession());
            Parent root2 = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));
            Scene scene2 = new Scene(root2);
            scene2.getStylesheets().add(
                    MainController.class.getResource("style.css").toExternalForm());

            stage2.setTitle("Kempinski Hotel - User 2");
            stage2.setScene(scene2);
            stage2.getIcons().add(new Image("icon.png"));

            stage2.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
