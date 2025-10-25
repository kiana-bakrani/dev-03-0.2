package cs151.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private StringProperty fullName;
    private StringProperty academicStatus;
    private StringProperty employed;
    private StringProperty jobDetails;
    private List<String> programmingLanguages = new ArrayList<>();
    private StringProperty progLangs;
    private List<String> databases = new ArrayList<>();
    private StringProperty database;
    private StringProperty preferredRole;
    private List<String> comments = new ArrayList<>();
    private StringProperty comment;
    private StringProperty blacklist;
    private StringProperty whiteList;

    public Student() {}
    // Constructor
    public Student(String fullName, String academicStatus, boolean employed, String jobDetails,
                   List<String> programmingLanguages, List<String> databases, List<String> comments, String preferredRole,
                   boolean blacklist, boolean whiteList) {
        this.fullName = new SimpleStringProperty();
        this.fullName.set(fullName);
        this.academicStatus =new SimpleStringProperty();
        this.academicStatus.set(academicStatus);
        this.employed = new SimpleStringProperty();
        this.employed.set(employed+"");
        this.jobDetails = new SimpleStringProperty();
        this.jobDetails.set(jobDetails);
        this.programmingLanguages = programmingLanguages;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < programmingLanguages.size(); i++){
            if(i==programmingLanguages.size()-1){sb.append(programmingLanguages.get(i));}
            else{sb.append(programmingLanguages.get(i)+", ");}
        }
        progLangs = new SimpleStringProperty();
        progLangs.set(sb.toString());
        this.databases = databases;
        sb = new StringBuilder();
        for (int i = 0; i < databases.size(); i++){
            if(i==databases.size()-1){sb.append(databases.get(i));}
            else{sb.append(databases.get(i)+", ");}
        }
        database = new SimpleStringProperty();
        database.set(sb.toString());
        this.preferredRole = new SimpleStringProperty();
        this.preferredRole.set(preferredRole);
        this.comments = comments;
        sb = new StringBuilder();
        for (int i = 0; i < comments.size(); i++){
            if(i==comments.size()-1){sb.append(comments.get(i));}
            else{sb.append(comments.get(i)+", ");}
        }
        comment = new SimpleStringProperty();
        comment.set(sb.toString());
        this.blacklist = new SimpleStringProperty();
        this.blacklist.set(blacklist+"");
        this.whiteList = new SimpleStringProperty();
        this.whiteList.set(whiteList+"");
    }

    // Getters and setters
    public String getFullName() { return fullName.get(); }
    public void setFullName(String fullName) {
        if(this.fullName==null) {this.fullName=new SimpleStringProperty(this, "fullName");}
        this.fullName.set(fullName);
    }

    public String getAcademicStatus() { return academicStatus.get(); }
    public void setAcademicStatus(String academicStatus) {
        if(this.academicStatus==null) {this.academicStatus=new SimpleStringProperty(this, "academicStatus");}
        this.academicStatus.set(academicStatus);
    }

    public boolean isEmployed() { return employed.get().equals("true"); }
    public void setEmployed(boolean employed) {
        if (this.employed==null) {this.employed = new SimpleStringProperty(this, "empolyment");}
        this.employed.set(employed+"");
    }
    public String getEmployment() {return employed.get();}

    public String getJobDetails() { return jobDetails.get(); }
    public void setJobDetails(String jobDetails) {
        if(this.jobDetails==null) {this.jobDetails=new SimpleStringProperty(this, "jobDetails");}

        // If Job Details field is empty, it will be "N/A"
        if(jobDetails==null || jobDetails.equals("")) {this.jobDetails.set("N/A");return;}
        
        this.jobDetails.set(jobDetails);
    }

    public List<String> getProgrammingLanguages() { return programmingLanguages; }
    public void setProgrammingLanguages(List<String> programmingLanguages) { 
        this.programmingLanguages = programmingLanguages;
        if(progLangs == null) {progLangs = new SimpleStringProperty(this, "progLang");}

        // Programming Languages Field will be "N/A" if empty
        if(programmingLanguages==null||programmingLanguages.size()==0) {progLangs.set("N/A");return;}

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < programmingLanguages.size(); i++){
            if(i==programmingLanguages.size()-1){sb.append(programmingLanguages.get(i));}
            else{sb.append(programmingLanguages.get(i)+", ");}
        }
        progLangs.set(sb.toString());
    }
    public String getProgLang() {return progLangs.get();}

    public List<String> getDatabases() { return databases; }
    public void setDatabases(List<String> databases) {
        this.databases = databases;
        if(database == null) {database = new SimpleStringProperty(this, "database");}

        // Databases field will be "N/A" if empty
        if(databases==null||databases.size()==0) {database.set("N/A");return;}

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < databases.size(); i++){
            if(i==databases.size()-1){sb.append(databases.get(i));}
            else{sb.append(databases.get(i)+", ");}
        }
        database.set(sb.toString());
    }
    public String getDatabase() {return database.get();}

    public String getPreferredRole() { return preferredRole.get(); }
    public void setPreferredRole(String preferredRole) {
        if(this.preferredRole==null) {this.preferredRole=new SimpleStringProperty(this, "preferredRole");}
        this.preferredRole.set(preferredRole);
    }

    public List<String> getComments() { return comments; }
    public void addComment(String com) {
        if(comments==null) {comments = new ArrayList<String>();}
        comments.add(LocalDate.now() + " - " + com);
        if(comment==null) {comment = new SimpleStringProperty(this, "comment");}
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < comments.size(); i++){
            if(i==comments.size()-1){sb.append(comments.get(i));}
            else{sb.append(comments.get(i)+"\n\n");}
        }
        comment.set(sb.toString());
    }
    public void setComments(List<String> comments) {
        this.comments = comments;
        if (comment == null) {comment = new SimpleStringProperty(this, "comment");}

        // Comments Field will be "N/A" if it is empty
        if (comments==null||comments.size()==0) {comment.set("N/A");return;}

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.comments.size(); i++){
            if(i==this.comments.size()-1){sb.append(this.comments.get(i));}
            else{sb.append(this.comments.get(i)+"\n\n");}
        }
        comment.set(sb.toString());
    }
    public String getComment() {return comment.get();}

    public boolean isBlacklist() { return blacklist.get().equals("true"); }
    public void setBlacklist(boolean blacklist) { 
        if(this.blacklist==null) {this.blacklist=new SimpleStringProperty(this, "blackList");}
        this.blacklist.set(blacklist+"");
    }
    public String getBlackList() {return blacklist.get();}

    
    public boolean isWhiteList() { return whiteList.get().equals("true"); }
    public void setWhiteList(boolean whiteList) { 
        if(this.whiteList==null) {this.whiteList=new SimpleStringProperty(this, "whiteList");}
        this.whiteList.set(whiteList+"");
    }
    public String getWhiteList() {return whiteList.get();}
}