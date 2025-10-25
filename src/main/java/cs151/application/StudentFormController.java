package cs151.application;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Node;


import java.io.IOException;
import java.nio.file.*;
import java.util.*;

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
    private final StudentRepositoryCsv repo = new StudentRepositoryCsv();

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


        // WL/BL mutual exclusivity
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
            return lines.stream().skip(1).map(String::trim).filter(s -> !s.isEmpty()).toList();
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


                        // Got rid of this one because I think he said we need to have 
                        // both of them as variables, which probably means we can have
                        // as both unselected and be fine.
            // // Require exactly one of WL/BL (your earlier intent)
            // if (!whitelistCheck.isSelected() && !blacklistCheck.isSelected()) {
            //     statusLabel.setText("Select Whitelist or Blacklist.");
            //     return;
            // }

            // Duplicate prevention
            if (repo.existsByFullNameTrimmedIgnoreCase(name)) {
                statusLabel.setText("Student already exists.");
                return;
            }

            // Build Student
            Student s = new Student();
            s.setFullName(name);
            s.setAcademicStatus(statusCombo.getValue());
            s.setEmployed(employed);
            s.setJobDetails(jobDetailsField.getText() == null ? "N/A" : jobDetailsField.getText().trim());
            List<String> selectedLangs = new ArrayList<>();
            for (Node n : languagesBox.getChildren()) {
                if (n instanceof CheckBox cb && cb.isSelected()) {
                    selectedLangs.add(cb.getText());
                }
            }

            List<String> selectedDBs = new ArrayList<>();
            for (Node n : databasesBox.getChildren()) {
                if (n instanceof CheckBox cb && cb.isSelected()) {
                    selectedDBs.add(cb.getText());
                }
            }

            s.setProgrammingLanguages(selectedLangs);
            s.setDatabases(selectedDBs);

            s.setPreferredRole(roleCombo.getValue());

            // ✅ Save blacklist correctly (whitelist => blacklist=false)
            s.setWhiteList(whitelistCheck.isSelected());
            s.setBlacklist(blacklistCheck.isSelected());

            if (commentArea.getText() != null && !commentArea.getText().isBlank()) {
                s.addComment(commentArea.getText().trim());
            }

            // Persist
            repo.save(s);
            statusLabel.setText("Saved ✔");
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Save failed: " + ex.getClass().getSimpleName());
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
    }


    private void goBack() {
        Main.INSTANCE.openHomePage();
    }
}
