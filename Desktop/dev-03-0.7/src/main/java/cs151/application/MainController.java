package cs151.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        if (welcomeText != null) {
            welcomeText.setText("Welcome to RateMyStudent!");
        }
    }

    // Loads define-languages.fxml from resources and switches the scene
    @FXML
    protected void onGoToDefineLanguages(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("define-languages.fxml"));
            Scene scene = new Scene(loader.load(), 600, 420);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) {
            ex.printStackTrace(); // check Run console if it still doesn't switch
            if (welcomeText != null) {
                welcomeText.setText("Failed to open Define page.");
            }
        }
    }
}
