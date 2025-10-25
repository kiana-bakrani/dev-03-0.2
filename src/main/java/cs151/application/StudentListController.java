package cs151.application;

import javafx.collections.*;
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
    @FXML private TableColumn<Student, String> WhiteListColumn;
    @FXML private TableColumn<Student, String> BlackListColumn;
    @FXML private TableColumn<Student, String> CommentsColumn;
    @FXML private ComboBox<String> searchCategory;
    @FXML private TextField searchBar;
    @FXML private Button searchButton;
    @FXML private Button backBtn;
    @FXML private Label statusLabel;

    private final StudentRepositoryCsv repo = new StudentRepositoryCsv();

    @FXML
    public void initialize() {
        // Initialize the ComboBox
        searchCategory.setItems(FXCollections.observableArrayList("Name","Employment",
        "Job Details","Academic Status","Preferred Role","Databases","Languages","Whitelist",
        "Blacklist","Comments"));
        searchCategory.setPromptText("Search Category");
        
        // Initialize the Search Bar
        searchBar.setPromptText("Search");
        searchButton.setOnAction(e -> onSearch());

        // Match columns to Student getters
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        YearColumn.setCellValueFactory(new PropertyValueFactory<>("academicStatus"));
        WorkplaceColumn.setCellValueFactory(new PropertyValueFactory<>("jobDetails"));
        RoleColumn.setCellValueFactory(new PropertyValueFactory<>("preferredRole"));

        // "true"/"false" string fields
        EmploymentColumn.setCellValueFactory(new PropertyValueFactory<>("employment"));
        BlackListColumn.setCellValueFactory(new PropertyValueFactory<>("blackList"));
        WhiteListColumn.setCellValueFactory(new PropertyValueFactory<>("whiteList"));

        // Display strings for lists
        LanguagesColumn.setCellValueFactory(new PropertyValueFactory<>("progLang"));
        DatabasesColumn.setCellValueFactory(new PropertyValueFactory<>("database"));
        CommentsColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        setUpStudentsList();

        backBtn.setOnAction(e -> Main.INSTANCE.openHomePage());
    }

    private void setUpStudentsList() {
        ObservableList<Student> base = loadStudents();

        // Sort A→Z by name
        NameColumn.setComparator(String::compareToIgnoreCase);
        SortedList<Student> sorted = new SortedList<>(base);
        sorted.setComparator(Comparator.comparing(
                s -> s.getFullName() == null ? "" : s.getFullName(),
                String::compareToIgnoreCase));

        StudentsList.setItems(sorted);
        StudentsList.setPlaceholder(new Label("No students found."));
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

    private void onSearch() {
        // Base cases
        if(searchCategory.getValue()==null) {statusLabel.setText("Please Select a Search Category");return;}
        if(searchBar.getText()==null||searchBar.getText().equals("")) {statusLabel.setText("No Search Conditions");setUpStudentsList();return;}
        
        try {
            // Generate List of Students that match the search conditions
            List<Student> temp = repo.loadSomeStudents(searchCategory.getValue(), searchBar.getText());
            ObservableList<Student> newStudentList = FXCollections.observableArrayList(temp);
            StudentsList.setItems(newStudentList);
        } catch (IOException e) {
            System.out.println("Student Search Failed: ");
            e.printStackTrace();
        }
    }
}
