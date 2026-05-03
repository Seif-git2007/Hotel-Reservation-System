import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class testGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProgressIndicator spinner = new ProgressIndicator();
        Label status = new Label("Loading hotel data...");
        VBox loadingPane = new VBox(20, spinner, status);
        loadingPane.setAlignment(Pos.CENTER);
        loadingPane.setStyle("-fx-background-color: #F5EFE0; -fx-padding: 80;");
        Scene loadingScene = new Scene(loadingPane, 900, 660);

        primaryStage.setUserData(new AppSession());
        primaryStage.setTitle("Kempinski Hotel - User 1");
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.setScene(loadingScene);
        primaryStage.show();
        primaryStage.setX(200);

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() {
                updateMessage("Loading hotel data from database...");
                try {
                    DataBaseManager.loadAll();
                } catch (Exception ex) {
                    System.out.println("DB unavailable, running in offline mode");
                    HotelDataBase.seedDefaultData();
                }
                if (HotelDataBase.getUsers().isEmpty()) {
                    HotelDataBase.seedDefaultData();
                }
                return null;
            }
        };

        status.textProperty().bind(loadTask.messageProperty());

        loadTask.setOnSucceeded(e -> {
            try {
                status.textProperty().unbind();

                Parent root = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(
                        MainController.class.getResource("style.css").toExternalForm());
                primaryStage.setScene(scene);

                Stage stage2 = new Stage();
                stage2.setUserData(new AppSession());
                Parent root2 = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));
                Scene scene2 = new Scene(root2);
                scene2.getStylesheets().add(
                        MainController.class.getResource("style.css").toExternalForm());
                stage2.setTitle("Kempinski Hotel - User 2");
                stage2.setScene(scene2);
                stage2.getIcons().add(new Image("icon.png"));
                stage2.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadTask.setOnFailed(e -> {
            status.textProperty().unbind();
            status.setText("Failed to load: " + loadTask.getException().getMessage());
            loadTask.getException().printStackTrace();
        });

        Thread loader = new Thread(loadTask, "DB-Loader");
        loader.setDaemon(true);
        loader.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
