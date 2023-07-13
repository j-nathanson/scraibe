package learn.scraibe.data;

import learn.scraibe.data.mappers.AppUserMapper;
import learn.scraibe.data.mappers.CourseMapper;
import learn.scraibe.data.mappers.NoteMapper;
import learn.scraibe.models.Course;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CourseJdbcTemplateRepository implements CourseRepository{
    private final JdbcTemplate jdbcTemplate;
    public CourseJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Course> getAll() {
        final String sql = "select course_id, `name` " +
                "from course";
        List<Course> courseList = jdbcTemplate.query(sql, new CourseMapper());

        if (courseList.size() > 0) {
            courseList.forEach(this::addNotes);
            courseList.forEach(this::addUserId);
        }

        return courseList;
    }

    @Override
    @Transactional
    public Course getByCourseId(int courseId) {
        final String sql = "select c.course_id, c.`name` " +
                "from course c " +
                "where c.course_id = ?";
        Course course = jdbcTemplate.query(sql, new CourseMapper(), courseId).stream()
                .findFirst().orElse(null);

        if (course != null){
            addNotes(course);
            addUserId(course);
        }

        return course;
    }

    @Override
    public List<Course> getCoursesByUserId(int userId) {
        final String sql = "select * from course where app_user_id = ?;";

        List<Course> courseList  = jdbcTemplate.query(sql, new CourseMapper(), userId);

        if (courseList.size() > 0) {
            courseList.forEach(this::addNotes);
            courseList.forEach(this::addUserId);
        }

        return courseList;
    }

    @Override
    public Course addCourse(Course course) {
        final String sql = "insert into course (course_id, app_user_id, `name`) " +
                "values (?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, course.getCourseId());
            ps.setInt(2, course.getUserId());
            ps.setString(3, course.getName());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        course.setCourseId(keyHolder.getKey().intValue());
        return course;
    }

    @Override
    public boolean editCourse(Course course) {
        final String sql = "update course set " +
                "`name` = ? " +
                "where course_id = ?;";

        int noteUpdated = jdbcTemplate.update(sql,
                course.getName(),
                course.getCourseId());

        return noteUpdated > 0;
    }

    @Override
    @Transactional
    public boolean deleteCourse(int courseId) {
        jdbcTemplate.update("delete n, ucn from note n " +
                "inner join user_course_note ucn on n.note_id = ucn.note_id " +
                "where ucn.course_id = ?;", courseId);
        return jdbcTemplate.update("delete from course where course_id = ?", courseId) > 0;
    }

    private void addNotes(Course course){
        final String sql = "select n.note_id, n.title, n.content, n.`date` " +
                "from note n " +
                "inner join user_course_note ucn on n.note_id = ucn.note_id " +
                "where ucn.course_id = ?";
        var notes = jdbcTemplate.query(sql, new NoteMapper(), course.getCourseId());
        course.setNotes(notes);
    }

    //add way to add user ids
    private void addUserId(Course course){
        final String sql = "select au.app_user_id, au.username, au.email, au.password_hash, au.enabled from app_user au " +
                "inner join course c on au.app_user_id = c.app_user_id " +
                "where course_id = ?";
        jdbcTemplate.query(sql, new AppUserMapper(new ArrayList<>()), course.getCourseId()).stream()
                .findFirst().ifPresent(user -> course.setUserId(user.getAppUserId()));
    }
}
