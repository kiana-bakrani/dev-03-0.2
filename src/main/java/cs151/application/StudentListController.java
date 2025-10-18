package cs151.application;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

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
    @FXML private Button backBtn;

    private final StudentRepositoryCsv repo = new StudentRepositoryCsv();

    @FXML
    public void initialize() {
        // Sets the TableView's contents to a list of students
        ObservableList<Student> students = loadStudents();
        StudentsList.setItems(students);

        // Sets each individual column of the TableView to a certain variable of the Student
        NameColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("fullName"));
        YearColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("academicStatus"));
        WorkplaceColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("jobDetails"));
        RoleColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("preferredRole"));
        BlackListColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("blackList"));
        EmploymentColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("employment"));
        LanguagesColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("ProgLang"));
        DatabasesColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("database"));
        
        // Back Button
        backBtn.setOnAction(e -> goBack());
        
    }

    private ObservableList<Student> loadStudents() {
        // Gets the list of students
        try {
            return repo.loadAll();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void clearList() {
        // Clears the list of students
        StudentsList.getColumns().clear();
    }

    private void goBack() {
        // simple: rebuild home page via your Main instance
        clearList();
        Main.INSTANCE.openHomePage();
    }
}

