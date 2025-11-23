package cs151.application;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
// import javafx.scene.control.cell.PropertyValueFactory;

// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.List;

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

    //private final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
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
        ListingLabel.setText(student.isBlacklist()+"");

        commentsView.setItems(FXCollections.observableArrayList(student.getComments()));


        // Load comments WITHOUT requiring an ID
        // List<Comment> comments = CommentRepository.loadForStudentName(s.getFullName());
        // commentsTable.setItems(FXCollections.observableArrayList(comments));


        // I think we can use the Comment class but I'm rushed for time and hav eot go soon
        // so I'm just going to comment that out and just set comment table to the students
    }

    @FXML
    public void initialize() {
        // commentsTable.setItems();
        // commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        // dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        // dateColumn.setCellFactory(col -> new TableCell<>() {
        //     @Override protected void updateItem(LocalDate value, boolean empty) {
        //         super.updateItem(value, empty);
        //         setText(empty || value == null ? null : DATE_FMT.format(value));
        //     }
        // });

        // commentColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        // commentColumn.setCellFactory(col -> new TableCell<>() {
        //     @Override protected void updateItem(String text, boolean empty) {
        //         super.updateItem(text, empty);
        //         if (empty || text == null) {
        //             setText(null);
        //             setTooltip(null);
        //         } else {
        //             String shortText = text.length() > 100 ? text.substring(0, 100) + "…" : text;
        //             setText(shortText);
        //             setTooltip(new Tooltip(text));
        //         }
        //     }
        // });

        // commentsTable.setRowFactory(tv -> {
        //     TableRow<Comment> row = new TableRow<>();
        //     row.setOnMouseClicked(ev -> {
        //         if (ev.getClickCount() == 2 && !row.isEmpty()) {
        //             Comment c = row.getItem();
        //             Alert a = new Alert(Alert.AlertType.INFORMATION);
        //             a.setTitle("Comment");
        //             a.setHeaderText(DATE_FMT.format(c.getDate()));
        //             a.setContentText(c.getText());
        //             a.getDialogPane().setMinWidth(520);
        //             a.showAndWait();
        //         }
        //     });
        //     return row;
        // });
    }
}
