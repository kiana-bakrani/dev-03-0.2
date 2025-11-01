package cs151.application;

import javafx.collections.*;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StudentListController {
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
    @FXML private Button deleteBtn;
    @FXML private Button editBtn;          // wired up now
    @FXML private Button backBtn;
    @FXML private Label statusLabel;

    private final StudentRepositoryCsv repo = new StudentRepositoryCsv();

    @FXML
    public void initialize() {
        searchBar.setPromptText("Search");
        searchButton.setOnAction(e -> onSearch());
        searchBar.setOnAction(e -> onSearch());

        NameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        YearColumn.setCellValueFactory(new PropertyValueFactory<>("academicStatus"));
        WorkplaceColumn.setCellValueFactory(new PropertyValueFactory<>("jobDetails"));
        RoleColumn.setCellValueFactory(new PropertyValueFactory<>("preferredRole"));
        EmploymentColumn.setCellValueFactory(new PropertyValueFactory<>("employment"));
        BlackListColumn.setCellValueFactory(new PropertyValueFactory<>("blackList"));
        WhiteListColumn.setCellValueFactory(new PropertyValueFactory<>("whiteList"));
        LanguagesColumn.setCellValueFactory(new PropertyValueFactory<>("progLang"));
        DatabasesColumn.setCellValueFactory(new PropertyValueFactory<>("database"));
        CommentsColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        setUpStudentsList();

        // enable/disable Delete + Edit based on selection
        deleteBtn.setDisable(true);
        editBtn.setDisable(true);
        StudentsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            boolean none = (sel == null);
            deleteBtn.setDisable(none);
            editBtn.setDisable(none);
            if (statusLabel != null) statusLabel.setText("");
        });

        deleteBtn.setOnAction(e -> onDeleteSelected());
        editBtn.setOnAction(e -> onEditSelected());

        backBtn.setOnAction(e -> Main.INSTANCE.openHomePage());

        attachContextMenu();
    }

    private void setUpStudentsList() {
        ObservableList<Student> base = loadStudents();
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
            statusLabel.setText("No Search Conditions");
            setUpStudentsList();
            return;
        }
        try {
            List<Student> temp = repo.loadSomeStudents(searchBar.getText());
            StudentsList.setItems(FXCollections.observableArrayList(temp));
            statusLabel.setText(temp.isEmpty() ? "No matches." : "Found " + temp.size());
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Student Search Failed.");
        }
    }

    private void onDeleteSelected() {
        Student sel = StudentsList.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Student");
        confirm.setHeaderText("Delete \"" + sel.getFullName() + "\"?");
        confirm.setContentText("This will permanently remove the profile.");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;

        try {
            boolean deleted = repo.deleteByFullNameIgnoreCase(sel.getFullName());
            if (deleted) {
                refreshTable();
                statusLabel.setText("Deleted: " + sel.getFullName());
            } else {
                statusLabel.setText("Not found (already deleted?)");
                refreshTable();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Delete failed: " + ex.getClass().getSimpleName()).showAndWait();
        }
    }

    // --- Edit handling ---
    private void onEditSelected() {
        Student sel = StudentsList.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        try {
            // Load your existing student form screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cs151/application/student-form.fxml"));
            Parent root = loader.load();

            Object formCtrl = loader.getController();
            // Try common method names to prefill the form if available
            try {
                formCtrl.getClass().getMethod("loadStudentForEdit", Student.class).invoke(formCtrl, sel);
            } catch (NoSuchMethodException ignore1) {
                try {
                    formCtrl.getClass().getMethod("setEditingStudent", Student.class).invoke(formCtrl, sel);
                } catch (NoSuchMethodException ignore2) {
                    // no prefill method yet — still navigate to form
                }
            }

            Stage stage = (Stage) StudentsList.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 420));
            stage.setTitle("Edit Student");
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not open Edit screen: " + ex.getClass().getSimpleName()).showAndWait();
        }
    }

    private void refreshTable() {
        if (searchBar.getText() != null && !searchBar.getText().isBlank()) onSearch();
        else setUpStudentsList();
        StudentsList.getSelectionModel().clearSelection();
        deleteBtn.setDisable(true);
        editBtn.setDisable(true);
    }

    private void attachContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem edit = new MenuItem("Edit");
        edit.setOnAction(e -> onEditSelected());

        MenuItem del = new MenuItem("Delete");
        del.setOnAction(e -> onDeleteSelected());

        menu.getItems().addAll(edit, del);

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

    // --- Add Comment feature (kept as-is) ---
    @FXML
    private void onAddComment() {
        Student selected = StudentsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a student first.").showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Comment");
        dialog.setHeaderText("Add comment for: " + selected.getFullName());
        dialog.setContentText("Enter comment:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(comment -> {
            if (!comment.isBlank()) {
                selected.addComment(comment);
                try {
                    repo.updateStudent(selected); // your existing method
                    refreshTable();
                } catch (IOException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Failed to save comment.").showAndWait();
                }
            }
        });
    }
}
