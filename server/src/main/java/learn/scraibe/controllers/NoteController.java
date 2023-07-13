package learn.scraibe.controllers;

import learn.scraibe.domain.NoteService;
import learn.scraibe.domain.Result;
import learn.scraibe.domain.ResultType;
import learn.scraibe.models.Note;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {
    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getByNoteId(@PathVariable int id) {
        Note note = service.getByNoteId(id);
        if (note == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    @GetMapping("/from-course/{id}")
    public ResponseEntity<List<Note>> getNotesByCourseId(@PathVariable int id) {
        return ResponseEntity.ok(service.getNotesByCourseId(id));
    }

    @PostMapping
    public ResponseEntity<Object> addNote(@RequestBody Note note) {
        Result<Note> result = service.addNote(note);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> editNote(@PathVariable int id, @RequestBody Note note) {
        if (id != note.getNoteId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Result<Note> result = service.editNote(note);
        if (!result.isSuccess()) {
            if (result.getResultType() == ResultType.NOT_FOUND) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST); // 400
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
    }

    //@DeleteMapping("/{id}")- ResponseEntity deleteNote(@PathVariable int id)
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteNote(@PathVariable int id) {
        if (service.deleteNote(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

