package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.nio.file.*;
import java.time.Year;
import java.util.*;

public class StudentListController {
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
    @FXML private Label statusLabel;

    private final StudentRepositoryCsv repo = new StudentRepositoryCsv();

    @FXML
    public void initialize() {
        ObservableList<Student> students = loadStudents();
        StudentsList.setItems(students);
        NameColumn.setCellValueFactory(new PropertyValueFactory("fullName"));
        YearColumn.setCellValueFactory(new PropertyValueFactory("academicStatus"));
        WorkplaceColumn.setCellValueFactory(new PropertyValueFactory("jobDetails"));
        RoleColumn.setCellValueFactory(new PropertyValueFactory("preferredRole"));
        BlackListColumn.setCellValueFactory(new PropertyValueFactory("blackList"));
        EmploymentColumn.setCellValueFactory(new PropertyValueFactory("employment"));
        LanguagesColumn.setCellValueFactory(new PropertyValueFactory("ProgLang"));
        DatabasesColumn.setCellValueFactory(new PropertyValueFactory("database"));
        backBtn.setOnAction(e -> goBack());
        
    }

    private ObservableList<Student> loadStudents() {
        try {
            return repo.loadAll();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> loadLanguages() {
        Path path = Paths.get("data/programming_languages.csv");
        if (!Files.exists(path)) return List.of();
        try {
            List<String> lines = Files.readAllLines(path);
            return lines.stream().map(String::trim).filter(s -> !s.isEmpty()).toList();
        } catch (IOException e) {
            statusLabel.setText("Could not load languages.");
            return List.of();
        }
    }

    private void clearList() {
        StudentsList.getColumns().clear();
    }

    private void goBack() {
        // simple: rebuild home page via your Main instance
        clearList();
        Main.INSTANCE.openHomePage();
    }
}

