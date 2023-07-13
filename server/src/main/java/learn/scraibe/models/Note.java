package learn.scraibe.models;

import java.time.LocalDate;

public class Note {
    private int noteId;
    private String title;
    private String content;
    private LocalDate date;
    private int courseId;
    private int userId;

    public Note() {
    }

    public Note(int noteId, String title, String content, LocalDate date, int courseId, int userId) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.date = date;
        this.courseId = courseId;
        this.userId = userId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) { this.date = date;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
