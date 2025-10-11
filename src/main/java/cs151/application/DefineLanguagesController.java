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

    private static final ObservableList<ProgrammingLanguage> DATA = FXCollections.observableArrayList();

    @FXML private TextField nameField;
    @FXML private TableView<ProgrammingLanguage> table;
    @FXML private TableColumn<ProgrammingLanguage, String> nameCol;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        // bind column → model
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        table.setItems(DATA);

        // inline edit support
        table.setEditable(true);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(evt -> {
            String newVal = evt.getNewValue() == null ? "" : evt.getNewValue().trim();
            if (newVal.isEmpty()) {
                // reject empty rename
                if (statusLabel != null) statusLabel.setText("Name cannot be empty.");
                table.refresh();
                return;
            }
            if (existsByName(newVal)) {
                if (statusLabel != null) statusLabel.setText("Duplicate: '" + newVal + "' already exists.");
                table.refresh();
                return;
            }
            ProgrammingLanguage pl = evt.getRowValue();
            pl.setName(newVal);
            table.refresh();
            if (statusLabel != null) statusLabel.setText("Edited: " + newVal);
            sortByName();
        });

        // delete row on DEL key
        table.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.DELETE) {
                ProgrammingLanguage selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    DATA.remove(selected);
                    if (statusLabel != null) statusLabel.setText("Deleted: " + selected.getName());
                }
            }
        });

        // default sort: A→Z
        nameCol.setSortType(TableColumn.SortType.ASCENDING);
        table.getSortOrder().setAll(nameCol);

        // load from CSV if present
        loadCSV();
        sortByName(); // ensure sorted after load
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
    }

    @FXML
    public void onBack(ActionEvent e) {
        try {
            Main.INSTANCE.openHomePage();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (statusLabel != null) statusLabel.setText("Back failed.");
        }
    }

    @FXML
    public void onSaveCSV(ActionEvent e) {
        try {
            Path dir = Path.of("data");
            if (!Files.exists(dir)) Files.createDirectories(dir);
            File file = dir.resolve("programming_languages.csv").toFile();
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, false)))) {
                out.println("Name");
                for (ProgrammingLanguage pl : DATA) {
                    out.printf("\"%s\"%n", esc(pl.getName()));
                }
            }
            if (statusLabel != null) statusLabel.setText("Saved: " + file.getPath());
        } catch (IOException ex) {
            if (statusLabel != null) statusLabel.setText("Save failed: " + ex.getMessage());
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
