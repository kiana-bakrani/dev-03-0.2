package cs151.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Load the Home page (hello-view.fxml)
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 700, 450); // default window size
            stage.setTitle("RateMyStudent — v0.2");               // project title
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
