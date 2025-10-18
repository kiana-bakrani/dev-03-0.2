package cs151.application;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StudentRepositoryCsv {
    private static final String FILE_PATH = "data/students.csv";

    public void save(Student s) throws IOException {
        Files.createDirectories(Paths.get("data"));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(toCsv(s));
            writer.newLine();
        }
    }
    public ObservableList<Student> loadAll() throws IOException {
        ObservableList<Student> students = FXCollections.observableArrayList();
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) return students;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                students.add(fromCsv(line));
            }
        }
        return students;
    }

    private String toCsv(Student s) {
        return String.join(",",
                s.getFullName(),
                s.getAcademicStatus(),
                Boolean.toString(s.isEmployed()),
                s.getJobDetails(),
                String.join("|", s.getProgrammingLanguages()),
                String.join("|", s.getDatabases()),
                s.getPreferredRole(),
                Boolean.toString(s.isBlacklist())
        );
    }

    private Student fromCsv(String line) {
        String[] parts = line.split(",", -1);
        Student s = new Student();
        s.setFullName(parts[0]);
        s.setAcademicStatus(parts[1]);
        s.setEmployed(Boolean.parseBoolean(parts[2]));
        s.setJobDetails(parts[3]);
        s.setProgrammingLanguages(Arrays.asList(parts[4].split("\\|")));
        s.setDatabases(Arrays.asList(parts[5].split("\\|")));
        s.setPreferredRole(parts[6]);
        s.setBlacklist(Boolean.parseBoolean(parts[7]));
        return s;
    }
}