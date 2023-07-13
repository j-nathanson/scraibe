package learn.scraibe.models;

import java.util.List;
import java.util.Objects;

public class Course {
    private int courseId;
    private String name;
    private List<Note> notes;
    private int userId;


    public Course() {
    }

    public Course(int courseId, String name, List<Note> notes, int userId) {
        this.courseId = courseId;
        this.name = name;
        this.notes = notes;
        this.userId = userId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return userId == course.userId && name.equalsIgnoreCase(course.name);
    }
}
