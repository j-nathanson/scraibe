package learn.scraibe.domain;

import learn.scraibe.data.CourseRepository;
import learn.scraibe.models.Course;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    CourseRepository repo;

    public CourseService(CourseRepository repo) {
        this.repo = repo;
    }

    public List<Course> getAll() {
        return repo.getAll();
    }

    public Course getByCourseId(int courseId) {
        return repo.getByCourseId(courseId);
    }

    public List<Course> getCoursesByUserId(int userId) {
        return repo.getCoursesByUserId(userId);
    }

    public Result<Course> addCourse(Course course) {
        Result<Course> result = validate(course);
        if (!result.isSuccess()) {
            return result;
        }

        if (course.getCourseId() != 0) {
            result.addMessage("courseId cannot be set for 'add' operation", ResultType.INVALID);
            return result;
        }

        course = repo.addCourse(course);
        result.setPayload(course);
        return result;
    }

    public Result<Course> editCourse(Course course) {
        Result<Course> result = validate(course);
        if (!result.isSuccess()) {
            return result;
        }

        if (course.getCourseId() <= 0) {
            result.addMessage("courseId must be set for 'edit' operation", ResultType.INVALID);
            return result;
        }

        if (!repo.editCourse(course)) {
            result.addMessage("courseId: " + course.getCourseId() + ", not found", ResultType.NOT_FOUND);
        }

        return result;
    }

    public boolean deleteCourse(int courseId) {
        return repo.deleteCourse(courseId);
    }

    private Result<Course> validate(Course course) {
        Result<Course> result = new Result<>();

        if (course == null) {
            result.addMessage("Course cannot be null.", ResultType.INVALID);
            return result;
        }

        if (course.getName() == null || course.getName().isBlank()) {
            result.addMessage("Course name is required.", ResultType.INVALID);
        }

        if (course.getUserId() <= 0) {
            result.addMessage("userId must be set.", ResultType.INVALID);
        }

        //check for duplicates here
        if (result.isSuccess()){
            List<Course> courses = repo.getAll();
            for (Course oldCourse : courses){
                if (oldCourse.getCourseId() != course.getCourseId()){
                    if (oldCourse.equals(course)){
                        result.addMessage("Duplicate courses prohibited.", ResultType.INVALID);
                    }
                }
            }
        }

        return result;
    }


}
