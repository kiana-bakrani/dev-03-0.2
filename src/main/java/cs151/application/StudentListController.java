package cs151.application;

import javafx.collections.*;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class StudentListController {
    // variables from the FXML File
    @FXML private TableView<Student> StudentsList;
    @FXML private TableColumn<Student, String> NameColumn;
    @FXML private TableColumn<Student, String> EmploymentColumn;
    @FXML private TableColumn<Student, String> WorkplaceColumn;
    @FXML private TableColumn<Student, String> YearColumn;
    @FXML private TableColumn<Student, String> RoleColumn;
    @FXML private TableColumn<Student, String> DatabasesColumn;
    @FXML private TableColumn<Student, String> LanguagesColumn;
    @FXML private TableColumn<Student, String> BlackListColumn;
    @FXML private TableColumn<Student, String> CommentsColumn;
    @FXML private Button backBtn;

    private final StudentRepositoryCsv repo = new StudentRepositoryCsv();

    @FXML
    public void initialize() {
        // Match columns to Student getters
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        YearColumn.setCellValueFactory(new PropertyValueFactory<>("academicStatus"));
        WorkplaceColumn.setCellValueFactory(new PropertyValueFactory<>("jobDetails"));
        RoleColumn.setCellValueFactory(new PropertyValueFactory<>("preferredRole"));

        // "true"/"false" string fields
        EmploymentColumn.setCellValueFactory(new PropertyValueFactory<>("employment"));
        BlackListColumn.setCellValueFactory(new PropertyValueFactory<>("blackList"));

        // Display strings for lists
        LanguagesColumn.setCellValueFactory(new PropertyValueFactory<>("progLang"));
        DatabasesColumn.setCellValueFactory(new PropertyValueFactory<>("database"));
        CommentsColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        ObservableList<Student> base = loadStudents();

        // Sort A→Z by name
        NameColumn.setComparator(String::compareToIgnoreCase);
        SortedList<Student> sorted = new SortedList<>(base);
        sorted.setComparator(Comparator.comparing(
                s -> s.getFullName() == null ? "" : s.getFullName(),
                String::compareToIgnoreCase));

        StudentsList.setItems(sorted);
        StudentsList.setPlaceholder(new Label("No students saved yet."));

        backBtn.setOnAction(e -> Main.INSTANCE.openHomePage());
    }

    private ObservableList<Student> loadStudents() {
        // Gets the list of students
        try {
            List<Student> all = repo.loadAll();
            return FXCollections.observableArrayList(all);
        } catch (IOException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
}
