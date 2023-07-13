package learn.scraibe.domain;

import learn.scraibe.data.NoteRepository;
import learn.scraibe.models.Note;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NoteService {
    private final NoteRepository repository;

    public NoteService(NoteRepository repository) {
        this.repository = repository;
    }

    public List<Note> getAll() {
        return repository.getAll();
    }

    public Note getByNoteId(int noteId) {
        return repository.getByNoteId(noteId);
    }

    public List<Note> getNotesByCourseId(int courseId) {
        return repository.getNotesByCourseId(courseId);
    }

    //- note id should be 0 when adding
    public Result<Note> addNote(Note note) {
        Result<Note> result = validate(note);
        if (!result.isSuccess()) {
            return result;
        }
        if (note.getNoteId() > 0) {
            result.addMessage("note_id cannot be set for `add` operation", ResultType.INVALID);
            return result;
        }
        note = repository.addNote(note);
        result.setPayload(note);
        return result;
    }

    //- note id should not be 0 when updating/deleting.
    public Result<Note> editNote(Note note) {
        Result<Note> result = validate(note);
        if (!result.isSuccess()) {
            return result;
        }
        if (note.getNoteId() <= 0) {
            result.addMessage("note_id must be set for `update` operation", ResultType.INVALID);
            return result;
        }
        if (!repository.editNote(note)) {
            String msg = String.format("note_id: %s, not found", note.getNoteId());
            result.addMessage(msg, ResultType.NOT_FOUND);
        }
        return result;
    }

    public boolean deleteNote(int noteId) {
        return repository.deleteNote(noteId);
    }

    //- should have all fields not null or not blank
    private Result<Note> validate(Note note) {
        Result<Note> result = new Result<>();
        if (note == null) {
            result.addMessage("Note cannot be null. ", ResultType.INVALID);
            return result;
        }
        if (note.getTitle() == null || note.getTitle().isBlank()) {
            result.addMessage("Title is required. ", ResultType.INVALID);
        }
        if (note.getContent() == null || note.getContent().isBlank()) {
            result.addMessage("Content is required. ", ResultType.INVALID);
        }
        if (note.getDate() == null) {
            result.addMessage("Date is required. ", ResultType.INVALID);
            return result;
        }
        if (note.getDate().isAfter(LocalDate.now())) {
            result.addMessage("Date cannot be in the future. ", ResultType.INVALID);
        }
        return result;
    }

}
