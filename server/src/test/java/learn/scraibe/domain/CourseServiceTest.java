package learn.scraibe.domain;

import learn.scraibe.data.CourseRepository;
import learn.scraibe.models.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CourseServiceTest {

    @Autowired
    CourseService service;

    @MockBean
    CourseRepository repo;

    @Test
    void shouldGetAll(){
        Course course1 = new Course(1, "Stats", new ArrayList<>(), 1);
        Course course2 = new Course(2, "English", new ArrayList<>(), 3);
        List<Course> courses = new ArrayList<>();
        courses.add(course1);
        courses.add(course2);

        when(repo.getAll()).thenReturn(courses);

        List<Course> result = service.getAll();

        assertEquals(courses, result);
    }

    @Test
    void shouldGetCoursesByUserId(){
        Course course1 = new Course(1, "Stats", new ArrayList<>(), 1);
        Course course2 = new Course(2, "English", new ArrayList<>(), 1);
        List<Course> courses = new ArrayList<>();
        courses.add(course1);
        courses.add(course2);

        when(repo.getCoursesByUserId(1)).thenReturn(courses);

        List<Course> result = service.getCoursesByUserId(1);

        assertEquals(result.get(0).getCourseId(),1);
        assertEquals(result.get(1).getCourseId(),2);
    }

    @Test
    void shouldNotGetCoursesByInvalidUserId(){
        List<Course> courses = new ArrayList<>();

        when(repo.getCoursesByUserId(1)).thenReturn(courses);

        List<Course> result = service.getCoursesByUserId(1);

        assertEquals(result.size(),0);

    }

    @Test
    void shouldGetById(){
        Course course = new Course(2, "English", new ArrayList<>(), 3);

        when(repo.getByCourseId(2)).thenReturn(course);

        Course result = service.getByCourseId(2);

        assertEquals(course, result);
    }

    @Test
    void shouldAdd(){
        Course course = new Course(0, "English", new ArrayList<>(), 3);
        Course newCourse = new Course(4, "English", new ArrayList<>(), 3);

        when(repo.addCourse(course)).thenReturn(newCourse);
        when(repo.getAll()).thenReturn(new ArrayList<>());

        Result<Course> result = service.addCourse(course);

        assertTrue(result.isSuccess());
        assertEquals(newCourse, result.getPayload());
    }

    @Test
    void shouldNotAddNullCourse(){
        Result<Course> result = service.addCourse(null);

        assertFalse(result.isSuccess());
        assertEquals("Course cannot be null.", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddNullOrBlankName(){
        Course course = new Course(0, null, new ArrayList<>(), 3);
        Result<Course> result = service.addCourse(course);

        assertFalse(result.isSuccess());
        assertEquals("Course name is required.", result.getMessages().get(0));

        course = new Course(0, "", new ArrayList<>(), 3);
        result = service.addCourse(course);

        assertFalse(result.isSuccess());
        assertEquals("Course name is required.", result.getMessages().get(0));

        course = new Course(0, "        ", new ArrayList<>(), 3);
        result = service.addCourse(course);

        assertFalse(result.isSuccess());
        assertEquals("Course name is required.", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddInvalidUserId(){
        Course course = new Course(0, "English", new ArrayList<>(), 0);
        Result<Course> result = service.addCourse(course);

        assertFalse(result.isSuccess());
        assertEquals("userId must be set.", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddDuplicateCourse(){
        Course course = new Course(0, "English", new ArrayList<>(), 3);
        Course c1 = new Course(1, "English", new ArrayList<>(), 3);
        Course c2 = new Course(2, "Science", new ArrayList<>(), 3);
        List<Course> courses = new ArrayList<>();
        courses.add(c1);
        courses.add(c2);

        when(repo.getAll()).thenReturn(courses);

        Result<Course> result = service.addCourse(course);

        assertFalse(result.isSuccess());
        assertEquals("Duplicate courses prohibited.", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddSetCourseId(){
        Course course = new Course(4, "English", new ArrayList<>(), 3);
        when(repo.getAll()).thenReturn(new ArrayList<>());
        Result<Course> result = service.addCourse(course);

        assertFalse(result.isSuccess());
        assertEquals("courseId cannot be set for 'add' operation", result.getMessages().get(0));
    }

    @Test
    void shouldEdit(){
        Course course = new Course(2, "English", new ArrayList<>(), 3);
        when(repo.editCourse(course)).thenReturn(true);

        Result<Course> result = service.editCourse(course);

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotEditUnsetCourseId(){
        Course course = new Course(0, "English", new ArrayList<>(), 3);
        Result<Course> result = service.editCourse(course);

        assertFalse(result.isSuccess());
        assertEquals("courseId must be set for 'edit' operation", result.getMessages().get(0));
    }

    @Test
    void shouldNotEditNotFoundId(){
        Course course = new Course(20, "English", new ArrayList<>(), 3);
        when(repo.editCourse(course)).thenReturn(false);

        Result<Course> result = service.editCourse(course);

        assertFalse(result.isSuccess());
        assertEquals("courseId: 20, not found", result.getMessages().get(0));
    }

    @Test
    void shouldNotEditDuplicateCourse(){
        Course course = new Course(2, "English", new ArrayList<>(), 3);
        Course c1 = new Course(1, "English", new ArrayList<>(), 3);
        Course c2 = new Course(2, "Science", new ArrayList<>(), 3);
        List<Course> courses = new ArrayList<>();
        courses.add(c1);
        courses.add(c2);

        when(repo.getAll()).thenReturn(courses);

        Result<Course> result = service.editCourse(course);

        assertFalse(result.isSuccess());
        assertEquals("Duplicate courses prohibited.", result.getMessages().get(0));
    }

    @Test
    void shouldDelete(){
        when(repo.deleteCourse(2)).thenReturn(true);

        boolean result = service.deleteCourse(2);

        assertTrue(result);
    }

    @Test
    void shouldNotDeleteNotFoundId(){
        when(repo.deleteCourse(20)).thenReturn(false);

        boolean result = service.deleteCourse(20);

        assertFalse(result);
    }

}