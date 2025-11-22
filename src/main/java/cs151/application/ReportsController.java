package cs151.application;

// import all the javafx stuff needed for the reports page
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

// handles the logic for the reports page. loads students, filters them, and updates the table.
public class ReportsController {
    // table for showing students
    @FXML
    private TableView<Student> studentsTable;
    @FXML
    private TableColumn<Student, String> nameColumn;
    @FXML
    private TableColumn<Student, String> statusColumn;
    @FXML
    private TableColumn<Student, String> roleColumn;
    @FXML
    private TableColumn<Student, String> employmentColumn;
    @FXML
    private TableColumn<Student, String> languagesColumn;
    @FXML
    private TableColumn<Student, String> whitelistColumn;
    @FXML
    private TableColumn<Student, String> blacklistColumn;
    // filter buttons
    @FXML
    private Button whitelistBtn;
    @FXML
    private Button blacklistBtn;
    @FXML
    private Button allStudentsBtn;
    // Back Button
    @FXML private Button backBtn;

    // list of all students
    private ObservableList<Student> students;

    // called when the page loads. sets up the table and loads all students.
    @FXML
    public void initialize() {
        // set up table columns to show student info
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        // always display the academic status (year) in this column
        // whitelist/blacklist are shown in their own columns to the right
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAcademicStatus()));
        roleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPreferredRole()));
        employmentColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmployment()));
        languagesColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProgLang()));
        whitelistColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().isWhiteList() ? "✓" : ""));
        blacklistColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().isBlacklist() ? "✓" : ""));

        // load all students from csv file
        try {
            students = FXCollections.observableArrayList(StudentRepositoryCsv.repo.loadAll());
        } catch (Exception e) {
            students = FXCollections.observableArrayList();
        }
        studentsTable.setItems(students);

        // set up filter buttons to show different student lists
        whitelistBtn.setOnAction(e -> showWhitelisted());
        blacklistBtn.setOnAction(e -> showBlacklisted());
        allStudentsBtn.setOnAction(e -> showAll());

        // Set up Back button
        backBtn.setOnAction(e -> Main.INSTANCE.openHomePage());
    }


    // shows only whitelisted students in the table
    private void showWhitelisted() {
        List<Student> filtered = students.filtered(Student::isWhiteList);
        studentsTable.setItems(FXCollections.observableArrayList(filtered));
    }

    // shows only blacklisted students in the table
    private void showBlacklisted() {
        List<Student> filtered = students.filtered(Student::isBlacklist);
        studentsTable.setItems(FXCollections.observableArrayList(filtered));
    }

    // shows all students in the table
    private void showAll() {
        studentsTable.setItems(students);
    }
}
    

