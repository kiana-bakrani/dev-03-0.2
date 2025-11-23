package cs151.application;

import java.time.LocalDate;

public class Comment {
    private final LocalDate date;
    private final String text;

    public Comment(LocalDate date, String text) {
        this.date = date;
        this.text = text;
    }

    public LocalDate getDate() { return date; }
    public String getText() { return text; }
}
