package learn.scraibe.domain;

import learn.scraibe.data.NoteRepository;
import learn.scraibe.models.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class NoteServiceTest {

    @Autowired
    NoteService service;

    @MockBean
    NoteRepository repository;

    @Test
    void shouldGetAll() { //noteId, String title, String content, LocalDate date, int courseId, int userId)

        Note note1 = new Note(1, "Advanced Spanish", "past tense conjugations note", LocalDate.of(2023,1,1), 1, 1);
        Note note2 = new Note(2, "Intermediate French", "present tense conjugations note", LocalDate.of(2023,2,2), 2, 2);

        List<Note> noteList = new ArrayList<>();
        noteList.add(note1);
        noteList.add(note2);

        when(repository.getAll()).thenReturn(noteList);
        List<Note> result = service.getAll();
        assertEquals(result.size(), noteList.size());
        assertEquals(result.get(0).getNoteId(), 1);
        assertEquals(result.get(1).getNoteId(), 2);
        assertEquals(result.get(0).getTitle(),"Advanced Spanish");
        assertEquals(result.get(1).getTitle(),"Intermediate French");
    }

    @Test
    void shouldGetByNoteId() {
        Note note1 = new Note(1, "Advanced Spanish", "past tense conjugations note", LocalDate.of(2023,1,1), 1, 1);

        when(repository.getByNoteId(1)).thenReturn(note1);
        Note result = service.getByNoteId(1);
        assertEquals(result.getNoteId(), 1);
        assertEquals(result.getTitle(), "Advanced Spanish");
        assertEquals(result.getContent(), "past tense conjugations note");
        assertEquals(result.getDate(), LocalDate.of(2023, 1, 1));
        assertEquals(result.getCourseId(), 1);
        assertEquals(result.getUserId(), 1);
    }

    @Test
    void shouldNotGetByInvalidNoteId() {
        when(repository.getByNoteId(100)).thenReturn(null);

        Note result = service.getByNoteId(100);
        assertNull(result);
    }

    @Test
    void shouldGetNotesByCourseId(){
        Note note1 = new Note(1, "Spanish Lesson 1", "past tense conjugations note", LocalDate.of(2023,1,1), 1, 1);
        Note note2 = new Note(2, "Spanish Lesson 2", "present tense conjugations note", LocalDate.of(2023,1,2), 1, 1);

        List<Note> noteList = new ArrayList<>();
        noteList.add(note1);
        noteList.add(note2);
        when(repository.getNotesByCourseId(1)).thenReturn(noteList);
        List<Note> actual = service.getNotesByCourseId(1);

        assertEquals(2,actual.size());
        assertEquals(1,actual.get(0).getNoteId());
        assertEquals(2,actual.get(1).getNoteId());
    }

    @Test
    void shouldNotGetNotesByInvalidId(){
        List<Note> noteList = new ArrayList<>();

        when(repository.getNotesByCourseId(1000)).thenReturn(noteList);
        List<Note> actual = service.getNotesByCourseId(1000);

        assertEquals(0,actual.size());
    }

    @Test
    void shouldAdd(){
        Note note = new Note(0, "Advanced Spanish", "past tense conjugations note", LocalDate.of(2023,1,1), 1, 1);

        when(repository.addNote(note)).thenReturn(note);
        Result <Note> actual = service.addNote(note);
        assertTrue(actual.isSuccess());
        assertEquals(actual.getPayload().getNoteId(),0);
    }

    @Test
    void shouldNotAddNoteIfNoteIsNull(){
        //should not add null
        Result<Note> result = service.addNote(null);
        assertFalse(result.isSuccess());
        assertEquals("Note cannot be null. ", result.getMessages().get(0));
    }
    @Test
    void shouldNotAddWhenInvalidNoteId() {
        Note note = new Note(1000, "Advanced Spanish", "past tense conjugations note", LocalDate.of(2023,1,1), 1, 1);
        Result<Note> result = service.addNote(note);
        assertEquals("note_id cannot be set for `add` operation", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddIfTitleIsBlank() {
        Note note = new Note(0, "", "past tense conjugations note", LocalDate.of(2023,1,1), 1, 1);
        Result<Note> result = service.addNote(note);
        assertEquals("Title is required. ", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddIfTitleIsNull() {
        Note note = new Note(0, null, "past tense conjugations note", LocalDate.of(2023,1,1), 1, 1);
        Result<Note> result = service.addNote(note);
        assertEquals("Title is required. ", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddIfContentIsBlank() {
        Note note = new Note(0, "Advanced Spanish", "", LocalDate.of(2023,1,1), 1, 1);
        Result<Note> result = service.addNote(note);
        assertEquals("Content is required. ", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddIfContentIsNull() {
        Note note = new Note(0, "Advanced Spanish", null, LocalDate.of(2023,1,1), 1, 1);
        Result<Note> result = service.addNote(note);
        assertEquals("Content is required. ", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddIfDateIsNull() {
        Note note = new Note(0, "Advanced Spanish", "past tense conjugations note", null, 1, 1);
        Result<Note> result = service.addNote(note);
        assertEquals("Date is required. ", result.getMessages().get(0));
    }


    @Test
    void shouldNotAddIfDateIsInTheFuture() {
        Note note = new Note(0, "Advanced Spanish", "past tense conjugations note", LocalDate.of(2025, 1,1), 1, 1);
        Result<Note> result = service.addNote(note);
        assertEquals("Date cannot be in the future. ", result.getMessages().get(0));
    }


    @Test
    void shouldEdit() {
        Note note = new Note(1, "Advanced Spanish", "past tense conjugations note", LocalDate.of(2023, 1,1), 1, 1);
        when(repository.editNote(note)).thenReturn(true);
        Result<Note> result = service.editNote(note);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotEditInvalidNoteId() {
        Note note = new Note(0, "Advanced Spanish", "past tense conjugations note", LocalDate.of(2023,1,1), 1, 1);
        when(repository.editNote(note)).thenReturn(false);
        Result<Note> result = service.editNote(note);
        assertEquals("note_id must be set for `update` operation", result.getMessages().get(0));
    }

    @Test
        void shouldNotEditUnknownNoteId() {
            Note note = new Note(1000, "Advanced Spanish", "past tense conjugations note", LocalDate.of(2023,1,1), 1, 1);
            when(repository.editNote(note)).thenReturn(false);
            Result <Note> result = service.editNote(note);
            assertFalse(result.isSuccess());
            assertEquals("note_id: 1000, not found", result.getMessages().get(0));
    }


    @Test
    void shouldDeleteByNoteId() {
        when(repository.deleteNote(1)).thenReturn(true);
        boolean result = service.deleteNote(1);
        assertTrue(result);
    }

    @Test
    void shouldNotDeleteByUnknownNoteId() {
        when(repository.deleteNote(100)).thenReturn(false);
        boolean result = service.deleteNote(100);
        assertFalse(result);
    }

}