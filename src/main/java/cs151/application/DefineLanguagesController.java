
package cs151.application;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefineLanguagesController {

    private final ObservableList<ProgrammingLanguage> DATA = FXCollections.observableArrayList();


    @FXML private TextField nameField;
    @FXML private TableView<ProgrammingLanguage> table;
    @FXML private TableColumn<ProgrammingLanguage, String> nameCol;
    @FXML private Label statusLabel;
    @FXML private Button backButton;

    // in DefineLanguagesController.java
    private Runnable onBack;

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    @FXML
    public void initialize() {
        // Bind column → model
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        // Enable inline editing
        table.setEditable(true);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(evt -> {
            String newVal = evt.getNewValue() == null ? "" : evt.getNewValue().trim();
            if (newVal.isEmpty()) {
                statusLabel.setText("Name cannot be empty.");
                table.refresh();
                return;
            }
            if (existsByName(newVal)) {
                statusLabel.setText("Duplicate: '" + newVal + "' already exists.");
                table.refresh();
                return;
            }
            ProgrammingLanguage pl = evt.getRowValue();
            pl.setName(newVal);
            table.refresh();
            statusLabel.setText("Edited: " + newVal);
            sortByName();
            saveCSV();
        });

        // Delete with DEL key
        table.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.DELETE) {
                ProgrammingLanguage selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    DATA.remove(selected);
                    statusLabel.setText("Deleted: " + selected.getName());
                    saveCSV();
                }
            }
        });

        // Autosave on list changes
        DATA.addListener((javafx.collections.ListChangeListener<ProgrammingLanguage>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {
                    saveCSV();
                }
            }
        });

        // Sort setup
        nameCol.setSortType(TableColumn.SortType.ASCENDING);
        table.getSortOrder().setAll(nameCol);

        // Bind table to data list
        table.setItems(DATA);

        Path file = Path.of("data", "programming_languages.csv");
        DATA.clear();

        loadCSV(); // read whatever is saved

        if (DATA.isEmpty()) {
            DATA.addAll(
                    new ProgrammingLanguage("C++"),
                    new ProgrammingLanguage("Java"),
                    new ProgrammingLanguage("Python")
            );
            statusLabel.setText("Loaded default 3 languages");
            saveCSV();
        } else {
            statusLabel.setText("Loaded " + DATA.size() + " languages from CSV");
        }

        if (backButton != null) {
            backButton.setOnAction(e -> {
                if (onBack != null) onBack.run();
            });
        }

        
        sortByName();
        table.sort();
    }



    @FXML
    public void onAdd(ActionEvent e) {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (name.isEmpty()) {
            if (statusLabel != null) statusLabel.setText("Name is required.");
            return;
        }
        if (existsByName(name)) {
            if (statusLabel != null) statusLabel.setText("Duplicate: '" + name + "' already exists.");
            return;
        }
        DATA.add(new ProgrammingLanguage(name));
        nameField.clear();
        if (statusLabel != null) statusLabel.setText("Added: " + name);
        sortByName();
        saveCSV();
    }

    @FXML
    public void onBack(ActionEvent e) {
        if (onBack != null) {
            onBack.run();
        } else {
            // fallback for safety if not set by Main.java
            try {
                Main.INSTANCE.openHomePage();
            } catch (Exception ex) {
                ex.printStackTrace();
                if (statusLabel != null) statusLabel.setText("Back failed.");
            }
        }
    }

    /* ---------- helpers ---------- */

    private void loadCSV() {
        Path file = Path.of("data", "programming_languages.csv");
        if (!Files.exists(file)) return;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            DATA.clear();                 // ← avoid duplicates across runs
            String line = br.readLine();  // skip header
            while ((line = br.readLine()) != null) {
                String name = line.replaceAll("^\"|\"$", "").replace("\"\"", "\"").trim();
                if (!name.isEmpty() && !existsByName(name)) {
                    DATA.add(new ProgrammingLanguage(name));
                }
            }
            if (statusLabel != null) statusLabel.setText("Loaded existing CSV");
        } catch (IOException ex) {
            if (statusLabel != null) statusLabel.setText("Load failed: " + ex.getMessage());
        }
    }


    private void saveCSV() {
        try {
            Path dir = Path.of("data");
            if (!Files.exists(dir)) Files.createDirectories(dir);

            Path file = dir.resolve("programming_languages.csv");

            try (BufferedWriter bw = Files.newBufferedWriter(file)) {
                bw.write("Name\n");
                for (ProgrammingLanguage pl : DATA) {
                    bw.write("\"" + esc(pl.getName()) + "\"\n");
                }
            }

            if (statusLabel != null) statusLabel.setText("Saved " + DATA.size() + " languages to CSV");
        } catch (IOException ex) {
            if (statusLabel != null) statusLabel.setText("Save failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private boolean existsByName(String n) {
        String needle = n.trim().toLowerCase();
        return DATA.stream().anyMatch(pl -> {
            String s = pl.getName();
            return s != null && s.trim().toLowerCase().equals(needle);
        });
    }

    private void sortByName() {
        if (!table.getSortOrder().contains(nameCol)) {
            table.getSortOrder().add(nameCol);
        }
        nameCol.setSortType(TableColumn.SortType.ASCENDING);
        table.sort();
    }

    private static String esc(String s) { return s == null ? "" : s.replace("\"", "\"\""); }
}






