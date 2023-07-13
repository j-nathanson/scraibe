package learn.scraibe.controllers;

import learn.scraibe.domain.CourseService;
import learn.scraibe.domain.Result;
import learn.scraibe.domain.ResultType;
import learn.scraibe.models.Course;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @GetMapping
    List<Course> getAll(){
        return service.getAll();
    }

    @GetMapping("/from-user/{id}")
    List<Course> getCoursesByUserId(@PathVariable int id){
        return  service.getCoursesByUserId(id);
    }

    @GetMapping("/{id}")
    ResponseEntity<Object> getCourseById(@PathVariable int id){
        Course course = service.getByCourseId(id);
        if (course == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    @PostMapping
    ResponseEntity<Object> addCourse(@RequestBody Course course){
        Result<Course> result = service.addCourse(course);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}")
    ResponseEntity<Object> editCourse(@PathVariable int id, @RequestBody Course course){
        if (id != course.getCourseId()){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Course> result = service.editCourse(course);
        if (!result.isSuccess()) {
            if (result.getResultType() == ResultType.NOT_FOUND) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Object> deleteCourse(@PathVariable int id){
        if (service.deleteCourse(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
