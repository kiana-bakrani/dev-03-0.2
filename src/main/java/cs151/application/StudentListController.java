package cs151.application;

import javafx.collections.*;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StudentListController {
    // FXML refs
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
    @FXML private TextField searchBar;
    @FXML private Button searchButton;
    @FXML private Button deleteBtn;         // 👈 added
    @FXML private Button backBtn;
    @FXML private Label statusLabel;

    private final StudentRepositoryCsv repo = new StudentRepositoryCsv();

    @FXML
    public void initialize() {
        // search UI
        searchBar.setPromptText("Search");
        searchButton.setOnAction(e -> onSearch());
        // allow pressing Enter to search
        searchBar.setOnAction(e -> onSearch());

        // map columns to Student getters (matches your Student class)
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        YearColumn.setCellValueFactory(new PropertyValueFactory<>("academicStatus"));
        WorkplaceColumn.setCellValueFactory(new PropertyValueFactory<>("jobDetails"));
        RoleColumn.setCellValueFactory(new PropertyValueFactory<>("preferredRole"));
        EmploymentColumn.setCellValueFactory(new PropertyValueFactory<>("employment"));   // "true"/"false" string
        BlackListColumn.setCellValueFactory(new PropertyValueFactory<>("blackList"));     // "true"/"false" string
        WhiteListColumn.setCellValueFactory(new PropertyValueFactory<>("whiteList"));     // "true"/"false" string
        LanguagesColumn.setCellValueFactory(new PropertyValueFactory<>("progLang"));      // joined string
        DatabasesColumn.setCellValueFactory(new PropertyValueFactory<>("database"));      // joined string
        CommentsColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));        // joined string (if you expose it)

        // setup table + initial data
        setUpStudentsList();

        // delete button: enable only when a row is selected
        deleteBtn.setDisable(true);
        StudentsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            deleteBtn.setDisable(sel == null);
            if (statusLabel != null) statusLabel.setText(""); // clear any prior status
        });
        deleteBtn.setOnAction(e -> onDeleteSelected());

        backBtn.setOnAction(e -> Main.INSTANCE.openHomePage());

        // optional: right-click context menu for delete
        attachContextMenu();
    }

    private void setUpStudentsList() {
        ObservableList<Student> base = loadStudents();

        // A→Z by name, case-insensitive
        NameColumn.setComparator(String::compareToIgnoreCase);
        SortedList<Student> sorted = new SortedList<>(base);
        sorted.setComparator(Comparator.comparing(
                s -> s.getFullName() == null ? "" : s.getFullName(),
                String::compareToIgnoreCase));

        StudentsList.setItems(sorted);
        StudentsList.setPlaceholder(new Label("No students found."));
    }

    private ObservableList<Student> loadStudents() {
        try {
            List<Student> all = repo.loadAll();
            return FXCollections.observableArrayList(all);
        } catch (IOException e) {
            e.printStackTrace();
            if (statusLabel != null) statusLabel.setText("Failed to load students.");
            return FXCollections.observableArrayList();
        }
    }

    private void onSearch() {
        if (searchBar.getText() == null || searchBar.getText().isBlank()) {
            if (statusLabel != null) statusLabel.setText("No Search Conditions");
            setUpStudentsList();
            return;
        }
        try {
            List<Student> temp = repo.loadSomeStudents(searchBar.getText());
            ObservableList<Student> newStudentList = FXCollections.observableArrayList(temp);
            StudentsList.setItems(newStudentList);
            if (statusLabel != null) statusLabel.setText(temp.isEmpty() ? "No matches." : "Found " + temp.size());
        } catch (IOException e) {
            if (statusLabel != null) statusLabel.setText("Student Search Failed.");
            e.printStackTrace();
        }
    }

    // --- Delete handling ---

    private void onDeleteSelected() {
        Student sel = StudentsList.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        String name = sel.getFullName() == null ? "(unknown)" : sel.getFullName();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Student");
        confirm.setHeaderText("Delete \"" + name + "\"?");
        confirm.setContentText("This will permanently remove the profile.");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;

        try {
            boolean deleted = repo.deleteByFullNameIgnoreCase(name);
            if (deleted) {
                refreshTable();
                if (statusLabel != null) statusLabel.setText("Deleted: " + name);
            } else {
                if (statusLabel != null) statusLabel.setText("Not found (already deleted?)");
                // also refresh to reflect current file state
                refreshTable();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Delete failed: " + ex.getClass().getSimpleName()).showAndWait();
        }
    }

    /** Refreshes the table, preserving search if a query is present. */
    private void refreshTable() {
        if (searchBar.getText() != null && !searchBar.getText().isBlank()) {
            onSearch(); // re-run current search
        } else {
            setUpStudentsList(); // reload everything with sorting
        }
        // keep selection cleared after refresh
        StudentsList.getSelectionModel().clearSelection();
        deleteBtn.setDisable(true);
    }

    // Optional: add a row context menu with "Delete"
    private void attachContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem del = new MenuItem("Delete");
        del.setOnAction(e -> onDeleteSelected());
        menu.getItems().add(del);

        StudentsList.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );
            return row;
        });
    }
}
