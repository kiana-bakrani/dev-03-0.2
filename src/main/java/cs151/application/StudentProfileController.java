package cs151.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import javafx.collections.FXCollections;

import java.io.IOException;

public class StudentProfileController {

    @FXML private Label NameLabel;
    @FXML private Label StatusLabel;
    @FXML private Label EmployedLabel;
    @FXML private Label JobLabel;
    @FXML private Label ProgLangLabel;
    @FXML private Label DatabasesLabel;
    @FXML private Label RoleLabel;
    @FXML private Label ListingLabel;

    @FXML private ListView<String> commentsView;

    private Student student;

    public void setStudent(Student s) {
        this.student = s;
        if (s == null) return;

        NameLabel.setText(student.getFullName());
        StatusLabel.setText(student.getAcademicStatus());
        EmployedLabel.setText(student.getEmployment());
        JobLabel.setText(student.getJobDetails());
        ProgLangLabel.setText(student.getProgLang());
        DatabasesLabel.setText(student.getDatabase());
        RoleLabel.setText(student.getPreferredRole());

        // Show whether student is whitelisted/blacklisted
        if (student.isWhiteList()) {
            ListingLabel.setText("Whitelisted");
        } else if (student.isBlacklist()) {
            ListingLabel.setText("Blacklisted");
        } else {
            ListingLabel.setText("None");
        }

        // Load comments into the ListView
        commentsView.setItems(FXCollections.observableArrayList(student.getComments()));
    }

    @FXML
    public void initialize() {
        // Double-click a comment to open it in a new window
        commentsView.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                String selectedComment = commentsView.getSelectionModel().getSelectedItem();
                if (selectedComment != null && !selectedComment.isBlank()) {
                    openCommentWindow(selectedComment);
                }
            }
        });
    }

    /** Opens a popup window that shows the full comment text. */
    private void openCommentWindow(String commentText) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("comment-full-view.fxml"));
            Parent root = loader.load();

            // Pass the comment text to the controller
            CommentFullViewController controller = loader.getController();
            controller.setComment(commentText);

            Stage stage = new Stage();
            stage.setTitle("Comment");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Unable to open comment view.").showAndWait();
        }
    }
}
