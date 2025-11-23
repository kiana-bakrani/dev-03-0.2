package cs151.application;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ReportsController {

    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> statusColumn;
    @FXML private TableColumn<Student, String> roleColumn;
    @FXML private TableColumn<Student, String> employmentColumn;
    @FXML private TableColumn<Student, String> languagesColumn;
    @FXML private TableColumn<Student, String> whitelistColumn;
    @FXML private TableColumn<Student, String> blacklistColumn;
    @FXML private Button whitelistBtn;
    @FXML private Button blacklistBtn;
    @FXML private Button allStudentsBtn;
    @FXML private Button backBtn;

    private ObservableList<Student> students;

    @FXML
    public void initialize() {
        // column bindings
        nameColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFullName()));
        statusColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAcademicStatus()));
        roleColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPreferredRole()));
        employmentColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmployment()));
        languagesColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getProgLang()));
        whitelistColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().isWhiteList() ? "✓" : ""));
        blacklistColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().isBlacklist() ? "✓" : ""));

        // load data
        try {
            students = FXCollections.observableArrayList(StudentRepositoryCsv.repo.loadAll());
        } catch (Exception e) {
            students = FXCollections.observableArrayList();
        }
        studentsTable.setItems(students);

        // filters
        whitelistBtn.setOnAction(e -> showWhitelisted());
        blacklistBtn.setOnAction(e -> showBlacklisted());
        allStudentsBtn.setOnAction(e -> showAll());

        // backBtn
        backBtn.setOnAction(e -> Main.INSTANCE.openHomePage());

        // double-click -> open Student Profile (form on top, comments table on bottom)
        studentsTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    Student selected = row.getItem();
                    openStudentProfileWindow(selected);
                }
            });
            return row;
        });
    }

    private void showWhitelisted() {
        studentsTable.setItems(FXCollections.observableArrayList(students.filtered(Student::isWhiteList)));
    }

    private void showBlacklisted() {
        studentsTable.setItems(FXCollections.observableArrayList(students.filtered(Student::isBlacklist)));
    }

    private void showAll() {
        studentsTable.setItems(students);
    }

    /** Opens the new page with profile form (top) and comments table (bottom). */
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
}

