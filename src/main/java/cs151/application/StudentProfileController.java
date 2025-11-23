package cs151.application;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentProfileController {

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;  // we'll show Academic Status here for now
    @FXML private Label majorLabel;

    @FXML private TableView<Comment> commentsTable;
    @FXML private TableColumn<Comment, LocalDate> dateColumn;
    @FXML private TableColumn<Comment, String> commentColumn;

    private final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private Student student;

    public void setStudent(Student s) {
        this.student = s;
        if (s == null) return;

        nameLabel.setText(s.getFullName());
        // If you later add getEmail(), change this line. For now, reuse for Academic Status:
        emailLabel.setText(s.getAcademicStatus());
        // Show preferred role as “Major” field placeholder
        majorLabel.setText(s.getPreferredRole());

        // Load comments WITHOUT requiring an ID
        List<Comment> comments = CommentRepository.loadForStudentName(s.getFullName());
        commentsTable.setItems(FXCollections.observableArrayList(comments));
    }

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : DATE_FMT.format(value));
            }
        });

        commentColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        commentColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String text, boolean empty) {
                super.updateItem(text, empty);
                if (empty || text == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    String shortText = text.length() > 100 ? text.substring(0, 100) + "…" : text;
                    setText(shortText);
                    setTooltip(new Tooltip(text));
                }
            }
        });

        commentsTable.setRowFactory(tv -> {
            TableRow<Comment> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    Comment c = row.getItem();
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Comment");
                    a.setHeaderText(DATE_FMT.format(c.getDate()));
                    a.setContentText(c.getText());
                    a.getDialogPane().setMinWidth(520);
                    a.showAndWait();
                }
            });
            return row;
        });
    }
}
