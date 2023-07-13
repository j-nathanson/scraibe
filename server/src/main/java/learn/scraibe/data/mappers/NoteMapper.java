package learn.scraibe.data.mappers;

import learn.scraibe.models.AppUser;
import learn.scraibe.models.Note;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoteMapper implements RowMapper<Note>{
        @Override
        public Note mapRow(ResultSet rs, int i) throws SQLException {
            Note note = new Note();

            note.setNoteId(rs.getInt("note_id"));
            note.setTitle(rs.getString("title"));
            note.setContent(rs.getString("content"));
            note.setDate(rs.getDate("date").toLocalDate());

            return note;
        }
}
