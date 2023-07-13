import React from "react";
import { Link, useNavigate, useParams } from 'react-router-dom';
import { deleteCoursesById } from "../services/CourseApi";


export default function Course( {course, setCourses} ){

    const handleDeleteCourse = async () => {
        if (window.confirm(`Delete course ${course.name}? WARNING: All notes associated with this course will be deleted!`)){
            console.log(`deleting ${course.name}...`);
            await deleteCoursesById(course.courseId);
            setCourses(oldCourse => oldCourse.filter(c => c.courseId != course.courseId));
        }
    }

    const handleEdit = async () => {
        
    }

    return <>
        <p>{course.name}</p>
        <Link to={''}>View Notes</Link>
        <button>Edit Course</button>
        <button onClick={() => handleDeleteCourse()}>Delete Course</button>

    </>

}