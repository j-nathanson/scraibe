package learn.scraibe.data.mappers;

import learn.scraibe.models.Course;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseMapper implements RowMapper<Course> {

    @Override
    public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
        Course course = new Course();

        course.setCourseId(rs.getInt("course_id"));
        course.setName(rs.getString("name"));

        return course;
    }
}
