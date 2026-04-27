import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class testGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));
            Scene scene = new Scene(root);
            Image image = new Image("icon.png");
            primaryStage.setTitle("Kempinski Hotel");
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(image);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
