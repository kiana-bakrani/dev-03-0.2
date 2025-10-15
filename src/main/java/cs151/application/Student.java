package cs151.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private String fullName;
    private String academicStatus;
    private boolean employed;
    private String jobDetails;
    private List<String> programmingLanguages = new ArrayList<>();
    private List<String> databases = new ArrayList<>();
    private String preferredRole;
    private List<String> comments = new ArrayList<>();
    private boolean whitelist;
    private boolean blacklist;

    public Student() {}
    // Constructor
    public Student(String fullName, String academicStatus, boolean employed, String jobDetails,
                   List<String> programmingLanguages, List<String> databases, String preferredRole,
                   boolean whitelist, boolean blacklist) {
        this.fullName = fullName;
        this.academicStatus = academicStatus;
        this.employed = employed;
        this.jobDetails = jobDetails;
        this.programmingLanguages = programmingLanguages;
        this.databases = databases;
        this.preferredRole = preferredRole;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
    }

    // Getters and setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAcademicStatus() { return academicStatus; }
    public void setAcademicStatus(String academicStatus) { this.academicStatus = academicStatus; }

    public boolean isEmployed() { return employed; }
    public void setEmployed(boolean employed) { this.employed = employed; }

    public String getJobDetails() { return jobDetails; }
    public void setJobDetails(String jobDetails) { this.jobDetails = jobDetails; }

    public List<String> getProgrammingLanguages() { return programmingLanguages; }
    public void setProgrammingLanguages(List<String> programmingLanguages) { this.programmingLanguages = programmingLanguages; }

    public List<String> getDatabases() { return databases; }
    public void setDatabases(List<String> databases) { this.databases = databases; }

    public String getPreferredRole() { return preferredRole; }
    public void setPreferredRole(String preferredRole) { this.preferredRole = preferredRole; }

    public List<String> getComments() { return comments; }
    public void addComment(String comment) {
        this.comments.add(LocalDate.now() + " - " + comment);
    }

    public boolean isWhitelist() { return whitelist; }
    public void setWhitelist(boolean whitelist) { this.whitelist = whitelist; }

    public boolean isBlacklist() { return blacklist; }
    public void setBlacklist(boolean blacklist) { this.blacklist = blacklist; }
}


