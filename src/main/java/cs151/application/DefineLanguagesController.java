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
    //List of programming languages
    private static final ObservableList<ProgrammingLanguage> DATA =
            FXCollections.observableArrayList();
    //UserInterface componnets like textboxs and display tables
    @FXML private TextField nameField;
    @FXML private TableView<ProgrammingLanguage> table;
    @FXML private TableColumn<ProgrammingLanguage, String> nameCol;
    @FXML private Label statusLabel;
     //table setup
    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        table.setItems(DATA);
        // lets you edit table columns
        table.setEditable(true);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(event -> {
            ProgrammingLanguage pl = event.getRowValue();
            pl.setName(event.getNewValue());
            table.refresh();
            statusLabel.setText("Edited: " + event.getNewValue());
        });
        // when you press delete key it will remove rows!
        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                ProgrammingLanguage selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    DATA.remove(selected);
                    statusLabel.setText("Deleted: " + selected.getName());
                }
            }
        });
        // sorts alphabetically  
        nameCol.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(nameCol);
        table.sort();  
        loadCSV(); // load saved data to csv
    }
    //the add button mechanism
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
    //back button function
    @FXML
    public void onBack(ActionEvent e) {
        try {
            Main.INSTANCE.openHomePage();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (statusLabel != null) statusLabel.setText("Back failed.");
        }
    }
    //save button function
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
    //for saving correctly in CSV
    private static String esc(String s) { return s == null ? "" : s.replace("\"", "\"\""); }
}
