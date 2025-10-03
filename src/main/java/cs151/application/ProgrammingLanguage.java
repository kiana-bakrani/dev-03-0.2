package cs151.application;

public class ProgrammingLanguage {
    private String name;

    public ProgrammingLanguage() {}

    public ProgrammingLanguage(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override public String toString() { return name; }
}
