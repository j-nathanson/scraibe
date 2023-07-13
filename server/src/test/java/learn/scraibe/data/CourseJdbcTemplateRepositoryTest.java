package learn.scraibe.data;

import learn.scraibe.models.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CourseJdbcTemplateRepositoryTest {

    @Autowired
    CourseJdbcTemplateRepository repo;

    @Autowired
    JdbcTemplate jdbcTemplate;

    static boolean hasSetup = false;

    @BeforeEach
    void setup() {
        if (!hasSetup) {
            hasSetup = true;
            jdbcTemplate.update("call set_known_good_state();");
        }
    }

    @Test
    void shouldGetAll(){
        List<Course> result = repo.getAll();
        assertTrue(result.size() >= 2);
        assertTrue(result.size() <= 4);
        assertTrue(result.stream().anyMatch(c -> c.getCourseId() == 1 && c.getName().equals("mathematics")));
    }

    @Test
    void shouldGetCoursesByUserId(){
        List<Course> result = repo.getCoursesByUserId(1);
        assertTrue(result.size() >= 1);
        assertTrue(result.size() <= 3);
        assertTrue(result.stream()
                .anyMatch(c -> c.getCourseId() == 1 &&
                        c.getName().equals("mathematics") &&
                        c.getUserId() == 1));
    }

    @Test
    void shouldNotGetCoursesByInvalidUserId(){
        List<Course> result = repo.getCoursesByUserId(1000);
        assertEquals(0,result.size());
    }

    @Test
    void shouldGetByCourseId(){
        Course result = repo.getByCourseId(1);

        assertNotNull(result);
        assertEquals("mathematics", result.getName());
        assertEquals(2, result.getNotes().size());
        assertEquals("this is a note about geometry basics.", result.getNotes().get(1).getContent());
        assertEquals(1, result.getUserId());
    }

    @Test
    void shouldNotGetIfNonExistentCourseId(){
        Course result = repo.getByCourseId(20);
        assertNull(result);
    }

    @Test
    void shouldAddCourse(){
        Course course = new Course(0, "physics", new ArrayList<>(), 2);
        Course result = repo.addCourse(course);

        assertNotNull(result);
        assertEquals(4, result.getCourseId());
    }

    @Test
    void shouldEditCourse(){
        Course course = new Course(2, "physics", new ArrayList<>(), 1);
        boolean result = repo.editCourse(course);

        assertTrue(result);

        Course edited = repo.getByCourseId(2);
        assertEquals("physics", edited.getName());
    }

    @Test
    void shouldNotEditInvalidCourse(){
        Course course = new Course(20, "physics", new ArrayList<>(), 1);
        boolean result = repo.editCourse(course);
        assertFalse(result);
    }

    @Test
    void shouldDeleteByCourseId(){
        boolean result = repo.deleteCourse(2);
        List<Course> courses = repo.getAll();

        assertTrue(result);
        assertFalse(courses.stream().anyMatch(c -> c.getCourseId() == 2));
    }

    @Test
    void shouldNotDeleteNonExistentCourseId(){
        boolean result = repo.deleteCourse(20);
        assertFalse(result);
    }

}