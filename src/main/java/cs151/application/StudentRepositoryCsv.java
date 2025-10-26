package cs151.application;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class StudentRepositoryCsv {
    private static final String FILE_PATH = "data/students.csv";

    // =========================
    // Public API
    // =========================

    /** Append or create the CSV and add one student row. */
    public void save(Student s) throws IOException {
        ensureDataFile();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            w.write(toCsv(s));
            w.newLine();
        }
    }

    /** Load all students. Safe with empty files and skips blank/malformed rows. */
    public List<Student> loadAll() throws IOException {
        ensureDataFile();
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) continue;
                Student s = fromCsv(t);
                if (s != null) students.add(s);
            }
        }
        return students;
    }

    /**
     * Simple search across a few string fields.
     * Returns students whose name/status/db/langs/role contains the given term (case-insensitive).
     */
    public List<Student> loadSomeStudents(String search) throws IOException {
        String q = search == null ? "" : search.toLowerCase();
        if (q.isEmpty()) return loadAll();

        List<Student> students = loadAll();
        for (int i = students.size() - 1; i >= 0; i--) {
            Student s = students.get(i);
            if (containsIgnoreCase(s.getFullName(), q)) continue;
            if (containsIgnoreCase(s.getAcademicStatus(), q)) continue;
            if (containsIgnoreCase(s.getDatabase(), q)) continue;
            if (containsIgnoreCase(s.getProgLang(), q)) continue;
            if (containsIgnoreCase(s.getPreferredRole(), q)) continue;
            students.remove(i);
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

    /** Delete exactly one student by full name (case-insensitive). Returns true if a row was removed. */
    public boolean deleteByFullNameIgnoreCase(String fullName) throws IOException {
        if (fullName == null || fullName.trim().isEmpty()) return false;

        ensureDataFile();
        Path p = Paths.get(FILE_PATH);

        List<String> kept = new ArrayList<>();
        boolean deleted = false;

        try (BufferedReader r = Files.newBufferedReader(p)) {
            String line;
            while ((line = r.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) continue;
                Student s = fromCsv(t);
                if (s == null) continue;
                String n = s.getFullName() == null ? "" : s.getFullName().trim();
                if (n.equalsIgnoreCase(fullName.trim())) {
                    deleted = true;
                } else {
                    kept.add(t);
                }
            }
        }

        if (!deleted) return false;

        Path tmp = Paths.get(FILE_PATH + ".tmp");
        try (BufferedWriter w = Files.newBufferedWriter(tmp)) {
            for (String row : kept) {
                w.write(row);
                w.newLine();
            }
        }
        Files.move(tmp, p, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        return true;
    }

    /** Bulk delete by predicate; returns number of rows removed. */
    public int deleteWhere(java.util.function.Predicate<Student> pred) throws IOException {
        ensureDataFile();
        Path p = Paths.get(FILE_PATH);

        List<String> kept = new ArrayList<>();
        int removed = 0;

        try (BufferedReader r = Files.newBufferedReader(p)) {
            String line;
            while ((line = r.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) continue;
                Student s = fromCsv(t);
                if (s == null) continue;
                if (pred.test(s)) {
                    removed++;
                } else {
                    kept.add(t);
                }
            }
        }

        if (removed == 0) return 0;

        Path tmp = Paths.get(FILE_PATH + ".tmp");
        try (BufferedWriter w = Files.newBufferedWriter(tmp)) {
            for (String row : kept) {
                w.write(row);
                w.newLine();
            }
        }
        Files.move(tmp, p, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        return removed;
    }

    /** --- NEW: update an existing student --- */
    public void updateStudent(Student updated) throws IOException {
        List<Student> all = loadAll();
        boolean found = false;

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getFullName().equalsIgnoreCase(updated.getFullName())) {
                all.set(i, updated);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IOException("Student not found: " + updated.getFullName());
        }

        saveAll(all);
    }
    /** --- NEW: seed starter data if file is empty --- */
    public void seedFiveStudentsIfEmpty() throws IOException {
        ensureDataFile();
        Path p = Paths.get(FILE_PATH);
        if (Files.size(p) > 0) return; // already has data

        List<Student> seeds = new ArrayList<>();

        // Student 1
        Student s1 = new Student();
        s1.setFullName("Harry Potter");
        s1.setAcademicStatus("Senior");
        s1.setEmployed(true);
        s1.setJobDetails("Part-time SWE");
        s1.setProgrammingLanguages(List.of("Java", "Python", "JavaScript"));
        s1.setDatabases(List.of("MySQL"));
        s1.setPreferredRole("Full-Stack");
        s1.setWhiteList(true);
        s1.setBlacklist(false);
        s1.addComment("Strong in Expecto Potronum.");
        seeds.add(s1);

        // Student 2
        Student s2 = new Student();
        s2.setFullName("Climbing Guy");
        s2.setAcademicStatus("Junior");
        s2.setEmployed(false);
        s2.setJobDetails("N/A");
        s2.setProgrammingLanguages(List.of("C++", "Java"));
        s2.setDatabases(List.of("Postgres"));
        s2.setPreferredRole("Back-End");
        s2.setWhiteList(false);
        s2.setBlacklist(false);
        s2.addComment("Climbs and codes in nature.");
        seeds.add(s2);

        // Student 3
        Student s3 = new Student();
        s3.setFullName("Burger Guy");
        s3.setAcademicStatus("Graduate");
        s3.setEmployed(true);
        s3.setJobDetails("TA - Algorithms");
        s3.setProgrammingLanguages(List.of("Python", "R"));
        s3.setDatabases(List.of("MongoDB"));
        s3.setPreferredRole("Data");
        s3.setWhiteList(true);
        s3.setBlacklist(false);
        s3.addComment("Creates Burger Code.");
        seeds.add(s3);

        // Student 4
        Student s4 = new Student();
        s4.setFullName("Pink Pony");
        s4.setAcademicStatus("Sophomore");
        s4.setEmployed(false);
        s4.setJobDetails("N/A");
        s4.setProgrammingLanguages(List.of("JavaScript", "TypeScript"));
        s4.setDatabases(List.of("MySQL", "Postgres"));
        s4.setPreferredRole("Front-End");
        s4.setWhiteList(false);
        s4.setBlacklist(false);
        s4.addComment("UI/UX club member.");
        seeds.add(s4);

        // Student 5
        Student s5 = new Student();
        s5.setFullName("Random Student");
        s5.setAcademicStatus("Senior");
        s5.setEmployed(true);
        s5.setJobDetails("Intern @ ByteWorks");
        s5.setProgrammingLanguages(List.of("Go", "Java"));
        s5.setDatabases(List.of("Postgres", "MongoDB"));
        s5.setPreferredRole("Back-End");
        s5.setWhiteList(true);
        s5.setBlacklist(false);
        s5.addComment("Enjoys systems work.");
        seeds.add(s5);

        // Write them to CSV
        try (BufferedWriter w = Files.newBufferedWriter(p, StandardOpenOption.APPEND)) {
            for (Student s : seeds) {
                w.write(toCsv(s));
                w.newLine();
            }
        }
    }

    /** Overwrites the CSV with a full list of students */
    public void saveAll(List<Student> students) throws IOException {
        ensureDataFile();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Student s : students) {
                w.write(toCsv(s));
                w.newLine();
            }
        }
    }

    // =========================
    // Helpers (private)
    // =========================

    private void ensureDataFile() throws IOException {
        Files.createDirectories(Paths.get("data"));
        Path p = Paths.get(FILE_PATH);
        if (!Files.exists(p)) Files.createFile(p);
    }

    private String safe(String v) {
        return v == null ? "" : v.replace(",", " ").trim();
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return list.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("|"));
    }

    private boolean containsIgnoreCase(String hay, String needleLower) {
        if (hay == null) return false;
        return hay.toLowerCase().contains(needleLower);
    }

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
        String[] parts = line.split(",", -1);
        if (parts.length < 10) {
            System.err.println("Skipping malformed row: '" + line + "'");
            return null;
        }
        try {
            Student s = new Student();
            s.setFullName(parts[0]);
            s.setAcademicStatus(parts[1]);
            s.setEmployed(Boolean.parseBoolean(parts[2]));
            s.setJobDetails(parts[3]);

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







/*package cs151.application;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class StudentRepositoryCsv {
    private static final String FILE_PATH = "data/students.csv";


    // Public API


    / Append or create the CSV and add one student row.
    public void save(Student s) throws IOException {
        ensureDataFile();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            w.write(toCsv(s));
            w.newLine();
        }
    }

   /Load all students. Safe with empty files and skips blank/malformed rows. /
    public List<Student> loadAll() throws IOException {
        ensureDataFile();
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) continue;             // skip blank lines
                Student s = fromCsv(t);
                if (s != null) students.add(s);        // skip malformed rows
            }
        }
        return students;
    }

    /**
     * Simple search across a few string fields.
     * Returns students whose name/status/db/langs/role contains the given term (case-insensitive).
     /
    public List<Student> loadSomeStudents(String search) throws IOException {
        String q = search == null ? "" : search.toLowerCase();
        if (q.isEmpty()) return loadAll();

        List<Student> students = loadAll();
        for (int i = students.size() - 1; i >= 0; i--) {
            Student s = students.get(i);
            if (containsIgnoreCase(s.getFullName(), q)) continue;
            if (containsIgnoreCase(s.getAcademicStatus(), q)) continue;
            if (containsIgnoreCase(s.getDatabase(), q)) continue;   // joined db string from model
            if (containsIgnoreCase(s.getProgLang(), q)) continue;   // joined langs string from model
            if (containsIgnoreCase(s.getPreferredRole(), q)) continue;
            students.remove(i);
        }
        return students;
    }

    /** Case-insensitive duplicate check on trimmed full name. /
    public boolean existsByFullNameTrimmedIgnoreCase(String name) throws IOException {
        String key = name == null ? "" : name.trim();
        if (key.isEmpty()) return false;
        for (Student s : loadAll()) {
            String n = s.getFullName() == null ? "" : s.getFullName().trim();
            if (n.equalsIgnoreCase(key)) return true;
        }
        return false;
    }

    /** Delete exactly one student by full name (case-insensitive). Returns true if a row was removed. /
    public boolean deleteByFullNameIgnoreCase(String fullName) throws IOException {
        if (fullName == null || fullName.trim().isEmpty()) return false;

        ensureDataFile();
        Path p = Paths.get(FILE_PATH);

        List<String> kept = new ArrayList<>();
        boolean deleted = false;

        try (BufferedReader r = Files.newBufferedReader(p)) {
            String line;
            while ((line = r.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) continue;            // skip blank lines
                Student s = fromCsv(t);
                if (s == null) continue;              // skip malformed
                String n = s.getFullName() == null ? "" : s.getFullName().trim();
                if (n.equalsIgnoreCase(fullName.trim())) {
                    deleted = true;                   // drop this row
                } else {
                    kept.add(t);
                }
            }
        }

        if (!deleted) return false;

        // rewrite (tmp -> replace)
        Path tmp = Paths.get(FILE_PATH + ".tmp");
        try (BufferedWriter w = Files.newBufferedWriter(tmp)) {
            for (String row : kept) {
                w.write(row);
                w.newLine();
            }
        }
        Files.move(tmp, p, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        return true;
    }

    /** Bulk delete by predicate; returns number of rows removed. /
    public int deleteWhere(java.util.function.Predicate<Student> pred) throws IOException {
        ensureDataFile();
        Path p = Paths.get(FILE_PATH);

        List<String> kept = new ArrayList<>();
        int removed = 0;

        try (BufferedReader r = Files.newBufferedReader(p)) {
            String line;
            while ((line = r.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) continue;
                Student s = fromCsv(t);
                if (s == null) continue;
                if (pred.test(s)) {
                    removed++;
                } else {
                    kept.add(t);
                }
            }
        }

        if (removed == 0) return 0;

        Path tmp = Paths.get(FILE_PATH + ".tmp");
        try (BufferedWriter w = Files.newBufferedWriter(tmp)) {
            for (String row : kept) {
                w.write(row);
                w.newLine();
            }
        }
        Files.move(tmp, p, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        return removed;
    }

    // =========================
    // Helpers
    // =========================

    private void ensureDataFile() throws IOException {
        Files.createDirectories(Paths.get("data"));
        Path p = Paths.get(FILE_PATH);
        if (!Files.exists(p)) Files.createFile(p); // 0-byte file is fine
    }

    private String safe(String v) {
        return v == null ? "" : v.replace(",", " ").trim(); // strip commas to keep CSV columns aligned
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return list.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("|")); // store lists pipe-separated
    }

    private boolean containsIgnoreCase(String hay, String needleLower) {
        if (hay == null) return false;
        return hay.toLowerCase().contains(needleLower);
    }

    // =========================
    // CSV (10 columns)  — read/write
    // =========================
    // Indexes:
    // 0 name, 1 status, 2 employed, 3 job,
    // 4 langs(|), 5 dbs(|), 6 role, 7 whitelist, 8 blacklist, 9 comments(|)

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
        String[] parts = line.split(",", -1); // keep empty fields
        if (parts.length < 10) {              // expect 10 columns
            System.err.println("Skipping malformed row: '" + line + "'");
            return null;
        }
        try {
            Student s = new Student();
            s.setFullName(parts[0]);
            s.setAcademicStatus(parts[1]);
            s.setEmployed(Boolean.parseBoolean(parts[2]));
            s.setJobDetails(parts[3]);

            // lists
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

}*/
