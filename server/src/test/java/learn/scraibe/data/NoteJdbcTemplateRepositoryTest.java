package learn.scraibe.data;

import learn.scraibe.models.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class NoteJdbcTemplateRepositoryTest {

    static boolean hasSetup = false;
    @Autowired
    NoteJdbcTemplateRepository repository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        if (!hasSetup) {
            hasSetup = true;
            jdbcTemplate.update("call set_known_good_state();");
        }
    }

    @Test
    void shouldGetAll() {
        List<Note> result = repository.getAll();
        assertTrue(result.size() >= 2);
        assertTrue(result.size() <= 4);
        assertTrue(result.stream().anyMatch(n ->
                n.getNoteId() == 1 &&
                        n.getTitle().equals("algebraic equations") &&
                        n.getContent().equals("this is a note about algebraic equations.") &&
                        n.getDate().equals(LocalDate.parse("2023-06-25")) &&
                        n.getCourseId() == 1 &&
                        n.getUserId() == 1));
    }

    @Test
    void shouldGetByNoteId() {
        Note result = repository.getByNoteId(1);
        assertNotNull(result);
        assertEquals(1, result.getNoteId());
        assertEquals("algebraic equations", result.getTitle());
        assertEquals("this is a note about algebraic equations.", result.getContent());
        assertEquals(LocalDate.parse("2023-06-25"), result.getDate());
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getCourseId());
    }

    @Test
    void shouldNotGetByInvalidId() {
        Note result = repository.getByNoteId(20);
        assertNull(result);
    }

    @Test
    void shouldGetNotesByCourseId() {
        List<Note> actual = repository.getNotesByCourseId(1);
        assertTrue(actual.size() >= 2);
    }

    @Test
    void shouldNotGetNotesByInvalidCourseId() {
        List<Note> actual = repository.getNotesByCourseId(1000);
        assertEquals(0, actual.size());
    }

    @Test
    void shouldAddNote() {
        Note note = new Note();
        note.setNoteId(0);
        note.setCourseId(1);
        note.setUserId(3);
        note.setTitle("Intermediate Chinese");
        note.setContent("lesson 1 notes");
        note.setDate(LocalDate.parse("2023-01-01"));

        Note result = repository.addNote(note);
        assertEquals(note, result);
    }


    @Test
    void shouldEditNote() {
        Note note = new Note();
        note.setNoteId(2);
        note.setCourseId(1);
        note.setContent("this is an updated note about geometry basics.");
        note.setTitle("geometry basics");
        note.setDate(LocalDate.of(2023, 6, 24));
        boolean result = repository.editNote(note);
        assertTrue(result);
    }

    @Test
    void shouldNotEditInvalidNoteId() {
        Note note = new Note();
        note.setNoteId(20);
        note.setCourseId(1);
        note.setContent("this is an updated note about geometry basics.");
        note.setTitle("geometry basics");
        note.setDate(LocalDate.of(2023, 6, 24));
        boolean result = repository.editNote(note);

        assertFalse(result);
    }

    @Test
    void shouldDeleteByNoteId() {
        boolean result = repository.deleteNote(4);
        List<Note> notes = repository.getAll();
        assertTrue(result);
        assertFalse(notes.stream().anyMatch(n -> n.getNoteId() == 4));
    }

    @Test
    void shouldNotDeleteInvalidNoteId() {
        boolean result = repository.deleteNote(20);
        assertFalse(result);
    }

}