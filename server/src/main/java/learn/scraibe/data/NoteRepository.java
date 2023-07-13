package learn.scraibe.data;

import learn.scraibe.models.Note;

import java.util.List;

public interface NoteRepository {
    List<Note> getAll();

    Note getByNoteId(int noteId);

    List<Note> getNotesByCourseId(int courseId);

    Note addNote(Note note);

    boolean editNote(Note note);

    boolean deleteNote(int noteId);

}
