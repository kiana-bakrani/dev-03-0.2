package cs151.application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class CommentFullViewController {

    @FXML private TextArea commentTextArea;
    @FXML private Button closeBtn;

    /** Called by StudentProfileController after loading FXML. */
    public void setComment(String text) {
        commentTextArea.setText(text);
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}
