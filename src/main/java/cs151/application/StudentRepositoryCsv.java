package cs151.application;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class StudentRepositoryCsv {
    private static final String FILE_PATH = "data/students.csv";

    // ---------- Public API ----------
    public void save(Student s) throws IOException {
        ensureDataFile();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            w.write(toCsv(s));
            w.newLine();
        }
    }

    /** Loads all students. Safe with empty files and skips bad lines. */
    public List<Student> loadAll() throws IOException {
        ensureDataFile();
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;           // skip blank lines
                Student s = fromCsv(line);
                if (s != null) students.add(s);         // skip malformed rows
            }
        }
        return students;
    }

    /** Case-insensitive duplicate check on trimmed full name. */
    public boolean existsByFullNameTrimmedIgnoreCase(String name) throws IOException {
        String key = name == null ? "" : name.trim();
        if (key.isEmpty()) return false;
        for (Student s : loadAll()) {
            String n = s.getFullName() == null ? "" : s.getFullName().trim();
            if (n.equalsIgnoreCase(key)) return true;
        }
        return false;
    }

    // ---------- Helpers ----------
    private void ensureDataFile() throws IOException {
        Files.createDirectories(Paths.get("data"));
        Path p = Paths.get(FILE_PATH);
        if (!Files.exists(p)) Files.createFile(p); // 0-byte file is OK
    }

    private String safe(String v) {
        return v == null ? "" : v.replace(",", " ").trim(); // strip commas to protect CSV
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return list.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("|"));       // store lists as pipe-separated
    }

    // CSV format (8 fields):
    // 0 name, 1 status, 2 employed, 3 job, 4 langs(|), 5 dbs(|), 6 role, 7 blacklist
    private String toCsv(Student s) {
        return String.join(",",
                safe(s.getFullName()),
                safe(s.getAcademicStatus()),
                Boolean.toString(s.isEmployed()),
                safe(s.getJobDetails()),
                joinList(s.getProgrammingLanguages()),
                joinList(s.getDatabases()),
                safe(s.getPreferredRole()),
                Boolean.toString(s.isWhiteList()),
                Boolean.toString(s.isBlacklist()),
                joinList(s.getComments())
        );
    }

    private Student fromCsv(String line) {
        String[] parts = line.split(",", -1);           // keep empty fields
        if (parts.length < 8) {                          // guard against short rows
            System.err.println("Skipping malformed row: '" + line + "'");
            return null;
        }
        try {
            Student s = new Student();
            s.setFullName(parts[0]);
            s.setAcademicStatus(parts[1]);
            s.setEmployed(Boolean.parseBoolean(parts[2]));
            s.setJobDetails(parts[3]);

            // lists: empty -> []
            List<String> langs = parts[4].isEmpty()
                    ? new ArrayList<>()
                    : new ArrayList<>(Arrays.asList(parts[4].split("\\|", -1)));
            langs.removeIf(x -> x == null || x.isBlank());
            s.setProgrammingLanguages(langs);

            List<String> dbs = parts[5].isEmpty()
                    ? new ArrayList<>()
                    : new ArrayList<>(Arrays.asList(parts[5].split("\\|", -1)));
            dbs.removeIf(x -> x == null || x.isBlank());
            s.setDatabases(dbs);

            s.setPreferredRole(parts[6]);
            s.setWhiteList(Boolean.parseBoolean(parts[7]));
            s.setBlacklist(Boolean.parseBoolean(parts[8]));

            List<String> cmts = parts[9].isEmpty()
                    ? new ArrayList<>()
                    : new ArrayList<>(Arrays.asList(parts[9].split("\\|", -1)));
            cmts.removeIf(x -> x == null || x.isBlank());
            s.setComments(cmts);

            return s;
        } catch (Exception ex) {
            System.err.println("Skipping row (parse error): '" + line + "' -> " + ex.getMessage());
            return null;
        }
    }
}
