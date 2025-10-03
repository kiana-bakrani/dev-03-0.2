package cs151.application;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefineLanguagesController {

    // In-memory list for v0.2 (persistence is optional)
    private static final ObservableList<ProgrammingLanguage> DATA = FXCollections.observableArrayList();

    @FXML private TextField nameField;
    @FXML private TableView<ProgrammingLanguage> table;
    @FXML private TableColumn<ProgrammingLanguage, String> nameCol;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        table.setItems(DATA);
    }

    @FXML
    public void onAdd(ActionEvent e) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusLabel.setText("Name is required.");
            return;
        }
        DATA.add(new ProgrammingLanguage(name));
        nameField.clear();
        statusLabel.setText("Added: " + name);
    }

    @FXML
    public void onBack(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(loader.load(), 700, 450);
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) {
            if (statusLabel != null) statusLabel.setText("Back failed.");
        }
    }

    // Optional per rubric
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
            statusLabel.setText("Saved: " + file.getPath());
        } catch (IOException ex) {
            statusLabel.setText("Save failed: " + ex.getMessage());
        }
    }

    private static String esc(String s) { return s == null ? "" : s.replace("\"", "\"\""); }
}
