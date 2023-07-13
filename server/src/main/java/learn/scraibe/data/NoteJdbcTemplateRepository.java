package learn.scraibe.data;

import learn.scraibe.data.mappers.AppUserMapper;
import learn.scraibe.data.mappers.CourseMapper;
import learn.scraibe.data.mappers.NoteMapper;
import learn.scraibe.models.Note;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NoteJdbcTemplateRepository implements NoteRepository {

    private final JdbcTemplate jdbcTemplate;

    public NoteJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Note> getAll() {
        final String sql = "select note_id, title, content, `date` from note n";
        List<Note> noteList = jdbcTemplate.query(sql, new NoteMapper());

        if (noteList.size() > 0) {
            noteList.forEach(this::addCourseId);
            noteList.forEach(this::addAppUser);
        }

        return noteList;
    }

    @Override
    @Transactional
    public Note getByNoteId(int noteId) {
        final String sql = "select n.note_id, title, content, `date` from note n where n.note_id = ?";

        Note note = jdbcTemplate.query(sql, new NoteMapper(), noteId).stream().findFirst().orElse(null);

        if (note != null) {
            addCourseId(note);
            addAppUser(note);
        }
        return note;
    }

    @Override
    public List<Note> getNotesByCourseId(int courseId) {
        final String sql = "select * from note where course_id = ?;";
        List<Note> noteList = jdbcTemplate.query(sql, new NoteMapper(), courseId);

        if (noteList.size() > 0) {
            noteList.forEach(this::addCourseId);
            noteList.forEach(this::addAppUser);
        }

        return noteList;
    }

    @Override
    @Transactional
    public Note addNote(Note note) {
        final String noteSql = "insert into note (course_id, title, content, `date`) values (?,?,?,?)";
        KeyHolder noteKeyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(noteSql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, note.getCourseId());
            ps.setString(2, note.getTitle());
            ps.setString(3, note.getContent());
            ps.setDate(4, Date.valueOf(note.getDate()));
            return ps;
        }, noteKeyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        final String ucnSql = "insert into user_course_note (app_user_id, course_id, note_id) values (?,?,?)";
        rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ucnSql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, note.getUserId());
            ps.setInt(2, note.getCourseId());
            ps.setInt(3, noteKeyHolder.getKey().intValue());
            return ps;
        });

        if (rowsAffected <= 0) {
            return null;
        }

        note.setNoteId(noteKeyHolder.getKey().intValue());
        return note;
    }

    @Override
    public boolean editNote(Note note) {
        final String sql = "update note set " +
                "title = ?, " +
                "content = ?, " +
                "`date` = ? " +
                "where note_id = ?;";

        int noteUpdated = jdbcTemplate.update(sql,
                note.getTitle(),
                note.getContent(),
                note.getDate(),
                note.getNoteId());

        return noteUpdated > 0;
    }

    @Override
    public boolean deleteNote(int noteId) {
        jdbcTemplate.update("delete from user_course_note where note_id = ?", noteId);
        return jdbcTemplate.update("delete from note where note_id = ?;", noteId) > 0;
    }

    private void addCourseId(Note note) {
        final String sql = "select c.course_id, c.`name` from user_course_note ucn" +
                " inner join course c on ucn.course_id = c.course_id" +
                " where ucn.note_id = ?";

        //should only have one course
        var courseList = jdbcTemplate.query(sql, new CourseMapper(), note.getNoteId());
        note.setCourseId(courseList.get(0).getCourseId());
    }

    private void addAppUser(Note note) {
        final String sql = "select au.app_user_id, au.username, au.email, au.password_hash, au.enabled from user_course_note ucn" +
                " inner join app_user au on ucn.app_user_id = au.app_user_id" +
                " where ucn.note_id = ?";
        var userList = jdbcTemplate.query(sql, new AppUserMapper(new ArrayList<>()), note.getNoteId());
        note.setUserId(userList.get(0).getAppUserId());
    }
}
