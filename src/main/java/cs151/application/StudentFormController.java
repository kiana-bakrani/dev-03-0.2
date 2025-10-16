package cs151.application;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class StudentFormController {
    @FXML private TextField fullNameField, jobDetailsField;
    @FXML private ComboBox<String> statusCombo, roleCombo;
    @FXML private RadioButton employedYes, employedNo;
    @FXML private ListView<String> languagesList, databasesList;
    @FXML private CheckBox whitelistCheck, blacklistCheck;
    @FXML private TextArea commentArea;
    @FXML private Button saveBtn, backBtn;
    @FXML private Label statusLabel;

    private final ToggleGroup employedGroup = new ToggleGroup();
    private final StudentRepositoryCsv repo = new StudentRepositoryCsv();

    @FXML
    public void initialize() {
        // Radios
        employedYes.setToggleGroup(employedGroup);
        employedNo.setToggleGroup(employedGroup);
        employedGroup.selectedToggleProperty().addListener((obs, o, n) ->
                jobDetailsField.setDisable(n == employedNo));

        // Enums as strings for now
        statusCombo.setItems(FXCollections.observableArrayList("Freshman","Sophomore","Junior","Senior","Graduate"));
        roleCombo.setItems(FXCollections.observableArrayList("Front-End","Back-End","Full-Stack","Data","Other"));

        // Databases (hard-coded)
        databasesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        databasesList.setItems(FXCollections.observableArrayList("MySQL","Postgres","MongoDB"));

        // Languages (load from existing CSV)
        languagesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        languagesList.setItems(FXCollections.observableArrayList(loadLanguages()));

        // Mutual exclusivity WL/BL
        whitelistCheck.selectedProperty().addListener((obs, oldV, on) -> { if (on) blacklistCheck.setSelected(false); });
        blacklistCheck.selectedProperty().addListener((obs, oldV, on) -> { if (on) whitelistCheck.setSelected(false); });

        // Buttons
        saveBtn.setOnAction(e -> onSave());
        backBtn.setOnAction(e -> goBack());
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

    private void onSave() {
        try {
            // Validate
            String name = fullNameField.getText().trim();
            if (name.isEmpty()) { statusLabel.setText("Full name is required."); return; }
            if (statusCombo.getValue() == null) { statusLabel.setText("Pick academic status."); return; }
            boolean employed = employedYes.isSelected();
            if (employed && jobDetailsField.getText().trim().isEmpty()) { statusLabel.setText("Job details required."); return; }

            // Duplicate prevention by trimmed full name (case-insensitive)
            if (repo.loadAll().stream().anyMatch(s -> s.getFullName().trim().equalsIgnoreCase(name))) {
                statusLabel.setText("Student already exists.");
                return;
            }

            // Build Student
            Student s = new Student();
            s.setFullName(name);
            s.setAcademicStatus(statusCombo.getValue());
            s.setEmployed(employed);
            s.setJobDetails(jobDetailsField.getText().trim());
            s.setProgrammingLanguages(new ArrayList<>(languagesList.getSelectionModel().getSelectedItems()));
            s.setDatabases(new ArrayList<>(databasesList.getSelectionModel().getSelectedItems()));
            s.setPreferredRole(roleCombo.getValue());
            s.setWhitelist(whitelistCheck.isSelected());
            s.setBlacklist(blacklistCheck.isSelected());
            if (!commentArea.getText().isBlank()) s.addComment(commentArea.getText().trim());

            // Save
            repo.save(s);
            statusLabel.setText("Saved ✔");
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Save failed.");
        }
    }

    private void clearForm() {
        fullNameField.clear();
        statusCombo.getSelectionModel().clearSelection();
        employedNo.setSelected(true);
        jobDetailsField.clear(); jobDetailsField.setDisable(true);
        languagesList.getSelectionModel().clearSelection();
        databasesList.getSelectionModel().clearSelection();
        roleCombo.getSelectionModel().clearSelection();
        whitelistCheck.setSelected(false);
        blacklistCheck.setSelected(false);
        commentArea.clear();
    }

    private void goBack() {
        // simple: rebuild home page via your Main instance
        Main.INSTANCE.openHomePage();
    }
}

