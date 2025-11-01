package cs151.application;
// creating a programming language class
public class ProgrammingLanguage {
    private String name;

    //default constructor
    public ProgrammingLanguage() {}

    //constructor that creates a programming language object with specific name
    public ProgrammingLanguage(String name) {
        this.name = name;
    }

    //getter that returns name of programming language
    public String getName() {
        return name;
    }
    //setter that sets the name of programming language
    public void setName(String name) {
        this.name = name;
    }
    //returns a string representation of the programming language
    @Override public String toString() {
        return name;
    }
}