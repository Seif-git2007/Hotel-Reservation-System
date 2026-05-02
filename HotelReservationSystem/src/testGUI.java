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
            // ── Window 1: Receptionist Sidebar ─────────────────────────────
            AppSession session1 = new AppSession();
            Receptionist receptionist = (Receptionist) HotelDataBase.searchUserByName("2");
            receptionist.setLoggedIn(true);
            session1.setCurrentUser(receptionist);

            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("Receptionist_Menu.fxml"));
            Parent root1 = loader1.load();
            ((ReceptionistMenuController) loader1.getController()).initSession(session1);

            Scene scene1 = new Scene(root1);
            scene1.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            primaryStage.setUserData(session1);
            primaryStage.setTitle("TEST — Receptionist Sidebar");
            primaryStage.setScene(scene1);
            primaryStage.getIcons().add(new Image("icon.png"));
            primaryStage.setX(100);
            primaryStage.show();

            // ── Window 2: Admin Amenities CRUD ──────────────────────────────
            AppSession session2 = new AppSession();
            Admin admin = (Admin) HotelDataBase.searchUserByName("1");
            admin.setLoggedIn(true);
            session2.setCurrentUser(admin);

            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("AdminAmenities.fxml"));
            Parent root2 = loader2.load();
            ((AdminAmenitiesController) loader2.getController()).initSession(session2);

            Scene scene2 = new Scene(root2);
            scene2.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            Stage stage2 = new Stage();
            stage2.setUserData(session2);
            stage2.setTitle("TEST — Admin Amenities CRUD");
            stage2.setScene(scene2);
            stage2.getIcons().add(new Image("icon.png"));
            stage2.setX(1020);
            stage2.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
