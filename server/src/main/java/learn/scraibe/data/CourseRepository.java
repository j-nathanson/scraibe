package learn.scraibe.data;

import learn.scraibe.models.Course;

import java.util.List;

public interface CourseRepository {
    List<Course> getAll();

    Course getByCourseId(int courseId);

    List<Course> getCoursesByUserId(int userId);

    Course addCourse(Course course);

    boolean editCourse(Course course);

    boolean deleteCourse(int courseId);
}
