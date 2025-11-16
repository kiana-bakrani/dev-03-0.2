package cs151.application;

import java.io.IOException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class StudentCommentController {
    @FXML private ListView<String> CommentsList;
    @FXML private Button backBtn;
    @FXML private Button addBtn;
    @FXML private Button finishBtn;
    @FXML private Label statusLabel;

    private static final StudentRepositoryCsv repo = StudentRepositoryCsv.repo;
    private Stage stage;
    private Student selected;
    private ObservableList<String> comments;
    private StudentListController studentListController;

    @FXML
    public void initialize() {
        selected = repo.getSelectedStudent();
        comments = FXCollections.observableArrayList(selected.getComments());
        CommentsList.setItems(comments);

        addBtn.setOnAction(e -> onAdd());
    }

    private void onAdd() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Comment");
        dialog.setHeaderText("Add Comment for "+selected.getFullName());
        dialog.setContentText("Add a comment:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(comment -> {
            selected.addComment(comment);
            resetList();
        });
    }

    /*@FXML
    private void onEdit() {
        String sel = CommentsList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a comment first.").showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edit Comment");
        dialog.setHeaderText("Edit Comment");
        dialog.setContentText("Edit comment:");
        dialog.getEditor().setText(sel);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(comment -> {
            selected.removeComment(sel);
            selected.addComment(comment);
            resetList();
        });
    } */

    private void resetList() {
        comments = FXCollections.observableArrayList(selected.getComments());
        CommentsList.setItems(comments);
    }

    public void setStage(Stage s, StudentListController studentListController) {
        this.studentListController = studentListController;
        stage = s;
        backBtn.setOnAction(e -> {stage.close();});
        finishBtn.setOnAction(e -> {
            try {
                selected.setComments(CommentsList.getItems());
                repo.updateStudent(selected);
                this.studentListController.finished();
                stage.close();
            } catch(IOException ex) {
                ex.printStackTrace();
                System.out.println("Failed to save comments "+ex.getMessage());
            }
        });
    }
}
