package cs151.application;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class StudentCommentController {

    @FXML private ListView<String> CommentsList;
    @FXML private Button backBtn;
    @FXML private Button addBtn;
    @FXML private Button finishBtn;
    @FXML private Label statusLabel;
    @FXML private TextArea commentTextArea;

    private static final StudentRepositoryCsv repo = StudentRepositoryCsv.repo;
    private Stage stage;
    private Student selected;
    private ObservableList<String> comments;
    private StudentListController studentListController;

    @FXML
    public void initialize() {
        // Get currently selected student from repository
        selected = repo.getSelectedStudent();

        if (selected == null) {
            comments = FXCollections.observableArrayList();
            CommentsList.setItems(comments);
            statusLabel.setText("No student selected.");
            addBtn.setDisable(true);
            finishBtn.setDisable(true);
            return;
        }

        // Load existing comments for this student
        comments = FXCollections.observableArrayList(selected.getComments());
        CommentsList.setItems(comments);

        addBtn.setOnAction(e -> onAdd());
    }

    /** Add a comment using the TextArea on the page (no popup). */
    private void onAdd() {
        String comment = commentTextArea.getText();

        if (comment == null || comment.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Comment cannot be empty.").showAndWait();
            return;
        }

        selected.addComment(comment.trim());
        resetList();
        commentTextArea.clear();
        statusLabel.setText("Comment added.");
    }

    /** Refresh the ListView after adding comments. */
    private void resetList() {
        comments.setAll(selected.getComments());
    }

    /** Called by StudentListController when opening this window. */
    public void setStage(Stage s, StudentListController studentListController) {
        this.studentListController = studentListController;
        this.stage = s;

        backBtn.setOnAction(e -> stage.close());

        finishBtn.setOnAction(e -> {
            try {
                selected.setComments(CommentsList.getItems());
                repo.updateStudent(selected);

                if (this.studentListController != null) {
                    this.studentListController.finished();
                }

                stage.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                new Alert(
                        Alert.AlertType.ERROR,
                        "Failed to save comments:\n" + ex.getMessage()
                ).showAndWait();
            }
        });
    }
}
