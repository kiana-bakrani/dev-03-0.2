package cs151.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class StudentFormController {
    @FXML private TextField fullNameField, jobDetailsField;
    @FXML private ComboBox<String> statusCombo, roleCombo;
    @FXML private RadioButton employedYes, employedNo;
    @FXML private VBox languagesBox, databasesBox;
    @FXML private CheckBox whitelistCheck, blacklistCheck;
    @FXML private TextArea commentArea;
    @FXML private Button saveBtn, backBtn;
    @FXML private Label statusLabel;

    private final ToggleGroup employedGroup = new ToggleGroup();
    private final StudentRepositoryCsv repo = StudentRepositoryCsv.repo;

    // --- edit-mode state ---
    private boolean editing = false;
    private String originalName = null;     // name used to locate/replace the original row
    private Student editingStudent = null;  // original object (optional reference)

    @FXML
    public void initialize() {
        // Radios
        employedYes.setToggleGroup(employedGroup);
        employedNo.setToggleGroup(employedGroup);
        // default to "No" so job field disables on first load
        employedNo.setSelected(true);
        jobDetailsField.setDisable(true);

        employedGroup.selectedToggleProperty().addListener((obs, o, n) ->
                jobDetailsField.setDisable(n == employedNo));

        // Required dropdowns
        statusCombo.setItems(FXCollections.observableArrayList("Freshman","Sophomore","Junior","Senior","Graduate"));
        roleCombo.setItems(FXCollections.observableArrayList("Front-End","Back-End","Full-Stack","Data","Other"));

        // Databases (hard-coded)
        List<String> dbOptions = List.of("MySQL", "Postgres", "MongoDB");
        for (String db : dbOptions) {
            CheckBox cb = new CheckBox(db);
            databasesBox.getChildren().add(cb);
        }

        // Languages (load from CSV)
        for (String lang : loadLanguages()) {
            CheckBox cb = new CheckBox(lang);
            languagesBox.getChildren().add(cb);
        }

        // WL/BL mutual exclusivity (still allow both off)
        whitelistCheck.selectedProperty().addListener((obs, oldV, on) -> { if (on) blacklistCheck.setSelected(false); });
        blacklistCheck.selectedProperty().addListener((obs, oldV, on) -> { if (on) whitelistCheck.setSelected(false); });

        // Buttons
        saveBtn.setOnAction(e -> onSave());
        backBtn.setOnAction(e -> goBack());
    }

    // ----------------------------
    // Public methods for EDIT mode
    // ----------------------------
    public void loadStudentForEdit(Student s) {
        setEditingStudent(s);
    }

    public void setEditingStudent(Student s) {
        if (s == null) return;
        editing = true;
        editingStudent = s;
        originalName = s.getFullName();

        // Prefill fields
        fullNameField.setText(s.getFullName());
        statusCombo.getSelectionModel().select(s.getAcademicStatus());

        if (s.isEmployed()) {
            employedYes.setSelected(true);
            jobDetailsField.setDisable(false);
        } else {
            employedNo.setSelected(true);
            jobDetailsField.setDisable(true);
        }
        jobDetailsField.setText(s.getJobDetails());

        roleCombo.getSelectionModel().select(s.getPreferredRole());

        // Prefill languages
        setChecks(languagesBox, s.getProgrammingLanguages());

        // Prefill databases
        setChecks(databasesBox, s.getDatabases());

        // Prefill WL/BL
        whitelistCheck.setSelected(s.isWhiteList());
        blacklistCheck.setSelected(s.isBlacklist());

        // Optional: you can show most recent comment in the textarea if you want to edit/add,
        // but keeping your current behavior (textarea used only to add a new comment on save).
        statusLabel.setText("Editing: " + (originalName == null ? "" : originalName));
        saveBtn.setText("Update");
    }

    // Utility: check/uncheck boxes in a VBox by matching text
    private void setChecks(VBox box, List<String> selected) {
        Set<String> target = new HashSet<>();
        if (selected != null) {
            for (String v : selected) {
                if (v != null && !v.isBlank()) target.add(v.trim());
            }
        }
        for (Node n : box.getChildren()) {
            if (n instanceof CheckBox cb) {
                cb.setSelected(target.contains(cb.getText()));
            }
        }
    }

    private List<String> loadLanguages() {
        Path path = Paths.get("data/programming_languages.csv");
        if (!Files.exists(path)) return List.of();
        try {
            List<String> lines = Files.readAllLines(path);
            return lines.stream()
                    .skip(1)
                    .map(String::trim)
                    .map(s -> s.replaceAll("^\"|\"$", ""))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        } catch (IOException e) {
            if (statusLabel != null) statusLabel.setText("Could not load languages.");
            return List.of();
        }
    }

    private void onSave() {
        try {
            // Validate
            String name = fullNameField.getText() == null ? "" : fullNameField.getText().trim();
            if (name.isEmpty()) { statusLabel.setText("Full name is required."); return; }
            if (statusCombo.getValue() == null) { statusLabel.setText("Pick academic status."); return; }
            if (roleCombo.getValue() == null) { statusLabel.setText("Pick preferred role."); return; }

            boolean employed = employedYes.isSelected();
            if (employed && (jobDetailsField.getText() == null || jobDetailsField.getText().trim().isEmpty())) {
                statusLabel.setText("Job details required.");
                return;
            }

            // Build Student from UI
            Student s = new Student();
            s.setFullName(name);
            s.setAcademicStatus(statusCombo.getValue());
            s.setEmployed(employed);
            s.setJobDetails(jobDetailsField.getText() == null ? "N/A" : jobDetailsField.getText().trim());

            List<String> selectedLangs = new ArrayList<>();
            for (Node n : languagesBox.getChildren()) {
                if (n instanceof CheckBox cb && cb.isSelected()) selectedLangs.add(cb.getText());
            }
            List<String> selectedDBs = new ArrayList<>();
            for (Node n : databasesBox.getChildren()) {
                if (n instanceof CheckBox cb && cb.isSelected()) selectedDBs.add(cb.getText());
            }
            s.setProgrammingLanguages(selectedLangs);
            s.setDatabases(selectedDBs);
            s.setPreferredRole(roleCombo.getValue());
            s.setWhiteList(whitelistCheck.isSelected());
            s.setBlacklist(blacklistCheck.isSelected());

            if (commentArea.getText() != null && !commentArea.getText().isBlank()) {
                s.addComment(commentArea.getText().trim());
            } else if (editing && editingStudent != null) {
                // preserve existing comments when editing if none entered now
                s.setComments(new ArrayList<>(editingStudent.getComments()));
            }

            if (!editing) {
                // --- CREATE flow (your existing behavior) ---
                // Duplicate prevention
                if (repo.existsByFullNameTrimmedIgnoreCase(name)) {
                    statusLabel.setText("Student already exists.");
                    return;
                }
                repo.save(s);
                statusLabel.setText("Saved ✔");
                clearForm();
            } else {
                // --- EDIT flow ---
                boolean nameChanged = (originalName != null && !originalName.trim().equalsIgnoreCase(name));

                if (nameChanged && repo.existsByFullNameTrimmedIgnoreCase(name)) {
                    statusLabel.setText("Another student already uses that name.");
                    return;
                }

                // Easiest robust approach: delete old row by original name, then save the new data.
                // This avoids relying on a specific update signature and works with your CSV repo.
                if (originalName != null && !originalName.isBlank()) {
                    repo.deleteByFullNameIgnoreCase(originalName);
                }
                repo.save(s);

                statusLabel.setText("Updated ✔");
                // Optionally return to list or keep on form — sticking with current flow:
                // just clear edit mode but keep fields if you want
                originalName = s.getFullName();
                editingStudent = s;
                saveBtn.setText("Update");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText((editing ? "Update" : "Save") + " failed: " + ex.getClass().getSimpleName());
        }
    }

    private void clearForm() {
        fullNameField.clear();
        statusCombo.getSelectionModel().clearSelection();
        employedNo.setSelected(true);
        jobDetailsField.clear();
        jobDetailsField.setDisable(true);
        roleCombo.getSelectionModel().clearSelection();
        whitelistCheck.setSelected(false);
        blacklistCheck.setSelected(false);
        commentArea.clear();

        for (Node n : languagesBox.getChildren()) {
            if (n instanceof CheckBox cb) cb.setSelected(false);
        }
        for (Node n : databasesBox.getChildren()) {
            if (n instanceof CheckBox cb) cb.setSelected(false);
        }

        // reset edit state if you want a clean slate after save in create mode
        if (!editing) {
            originalName = null;
            editingStudent = null;
        }
    }

    private void goBack() {
        Main.INSTANCE.openHomePage();
    }
}
