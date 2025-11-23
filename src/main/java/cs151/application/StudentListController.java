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
    @FXML private Button commentBtn;
    @FXML private Button editBtn;          // wired up now
    @FXML private Button backBtn;
    @FXML private Label statusLabel;

    private final StudentRepositoryCsv repo = StudentRepositoryCsv.repo;

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
        commentBtn.setDisable(true);
        StudentsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            boolean none = (sel == null);
            deleteBtn.setDisable(none);
            editBtn.setDisable(none);
            commentBtn.setDisable(none);
            if (statusLabel != null) statusLabel.setText("");
        });

        deleteBtn.setOnAction(e -> onDeleteSelected());
        editBtn.setOnAction(e -> onEditSelected());
        commentBtn.setOnAction(e -> onEditComments());

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

    // For the comment Editor to let you know when to refresh the table
    // This is probably bad code, but I don't know how else to make it work
    public void finished() {
        refreshTable();
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

    private void openStudentProfileWindow(Student selected) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentProfileView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(selected.getFullName() + " — Profile & Comments");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Pass the selected student to the controller
            StudentProfileController controller = loader.getController();
            controller.setStudent(selected);
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Unable to open profile window.").showAndWait();
        }
    }

    // --- Add Comment feature (kept as-is) ---
    @FXML
    private void onEditComments() {
        repo.setSelectedStudent(StudentsList.getSelectionModel().getSelectedItem());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cs151/application/student-comment.fxml"));
            Scene scene = new Scene(loader.load(), 400, 500);
            StudentCommentController controller = loader.getController();
            Stage s = new Stage();
            s.setScene(scene);
            s.setTitle("Add Comments");
            s.show();
            controller.setStage(s, this);
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load Comments: "+e.getMessage());
        }
    }
}
