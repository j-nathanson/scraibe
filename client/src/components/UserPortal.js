import React, { useContext, useRef } from 'react';
import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import AuthContext from '../context/AuthContext';
import {editCourse, getCoursesByUserId} from '../services/CourseApi';
import { deleteCoursesById } from '../services/CourseApi';
import { addCourse } from '../services/CourseApi';
import Errors from './Errors';

let DEFAULT_COURSE = {
  courseId: 0,
  userId: 0,
  name: ""
}

export default function UserPortal() {
  const auth = useContext(AuthContext);

  const [courses, setCourses] = useState([]);
  const [course, setCourse] = useState(DEFAULT_COURSE);
  const [courseToEdit, setCourseToEdit] = useState(DEFAULT_COURSE);
  const [errors, setErrors] = useState([]);
  const [editing, setEditing] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [courseToDelete, setCourseToDelete] = useState(DEFAULT_COURSE);
  const [showConfirm, setShowConfirm] = useState(false);
  const [transition, setTransition] = useState(false);
  const [confirmMessage, setConfirmMessage] = useState("");

  // const navigate = useNavigate();
  const courseRef = useRef(null);
  const editRef = useRef(null);

  useEffect(() => {
    getCoursesByUserId(auth.user.userId)
    .then(data => setCourses(data)) 
    .catch(console.log);

    const newCourse = {...course};
    newCourse.userId = auth.user.userId;
    setCourse(newCourse);
    setCourseToEdit(newCourse);
    setCourseToDelete(newCourse);
    DEFAULT_COURSE.userId = auth.user.userId;
  }, []);

  useEffect(() => {
    let timeout;
    if (!showConfirm) {
      timeout = setTimeout(() => {
        setTransition(false);
      }, 500); // Delay to allow the fade-out animation to play
    }
    return () => clearTimeout(timeout);
  }, [showConfirm]);
    
  const confirmation = (message) => {
    setShowConfirm(true);
    setTransition(true);
    setConfirmMessage(message);
    const timer = setTimeout(() => {
      setShowConfirm(false);
      setConfirmMessage("")
    }, 2000);
  
    return () => clearTimeout(timer);
  }

  const handleChange = (event) => {
    if (editing){
      const newCourse = {...courseToEdit};
      newCourse[event.target.name] = event.target.value;
      setCourseToEdit(newCourse);

    } else {
      const newCourse = {...course};
      newCourse[event.target.name] = event.target.value;
      setCourse(newCourse);
    }
  };

  const handleEditCourse = async () => {
    return new Promise((resolve, reject) => {
      editCourse(courseToEdit.courseId, courseToEdit)
      .then(data => {
        let success;
        if (data) {
          setErrors(data);
          success = false;
        } else {
          setErrors([]);
          const newCourses = [...courses];
          const editedCourseIndex = newCourses.findIndex(c => c.courseId === courseToEdit.courseId)
          newCourses[editedCourseIndex].name = courseToEdit.name;
          setCourses(newCourses);
          success = true;
          confirmation(courseToEdit.name + " has been successfully updated.")
        }

        resolve(success);
      })
      .catch(err => {
        console.log(err);
        reject(err);
      });
    });
  }

  const handleDeleteCourse = async (toDelete) => {
        await deleteCoursesById(toDelete.courseId);
        setCourses(oldCourse => oldCourse.filter(c => c.courseId != toDelete.courseId));
        setDeleting(false);
        setCourseToDelete(DEFAULT_COURSE);
        confirmation(toDelete.name + " has been successfully deleted.")
  }

  const handleAddCourse = async () => {
    return new Promise((resolve, reject) => {
      addCourse(course)
      .then(data => {
        let success;
        if (data.courseId){
          setErrors([]);
          const newCourse = {...course};
          newCourse.name = "";
          setCourse(newCourse);
          const newCourses = [...courses, data];
          setCourses(newCourses);
          success = true;
          confirmation(data.name + " has been successfully added.")
        } else {
          setErrors(data);
          success = false;
        }

        resolve(success);
      })
      .then()
      .catch(err => {
        console.log(err);
        reject(err);
      });
    });
  }

  const fillOutEditForm = (toEdit) => {
    setEditing(true);
    setCourseToEdit(toEdit);
    setCourse(toEdit);
    setErrors([]);
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    let success;

    if (editing){
      success = await handleEditCourse();
    } else {
      success = await handleAddCourse();
    }


    if (success){
      resetForm();
    }
  };

  const resetForm = () => {
    setEditing(false);
    setCourseToEdit(DEFAULT_COURSE);
    setCourse(DEFAULT_COURSE);
    setErrors([]);
  }

  const deleteAlert = (course) => {
    setDeleting(true);
    setCourseToDelete(course);
  }

  const scrollToSection = (ref) => {
    ref.current.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <div className='container flex flex-col px-4 mx-auto bg-grey-200 pb-5 mb-7'>
      {!auth.hasRole("admin") && <h1 className='text-4xl font-extrabold text-black pb-5 text-center mt-4'>{auth.user.username}'s Portal</h1>}
      <form onSubmit={handleSubmit} id ="form" className='space-y-4' ref={editRef}>
        <div className='text-2xl font-bold text-black bg-grey-200'>{editing ? "Editing " + course.name + " Course" : "Add a Course"}</div>
        <Errors errors={errors} />
        <fieldset>
          <label className='text-lg'>Course Name:</label>
          {/* extend to length of table */}
          <input id="name" name="name" type='text' value={editing ? courseToEdit.name : course.name} onChange={handleChange} className='input input-bordered w-full text-lg' required placeholder="e.g., English"/>
        </fieldset>
        <div className='space-x-3'>
        <button type="submit" className='btn text-white text-lg bg-emerald-600 hover:bg-emerald-700 normal-case'>
          {editing ? "Edit Course" : "Add Course"}
        </button>
        {editing && 
          <button type='button' onClick={resetForm} className='btn btn-accent text-lg normal-case'>
            Cancel
          </button>
        }
        </div>
      </form>

      <div className="divider"></div> 
      <div className='text-2xl font-bold text-black pt-2 pb-5' ref={courseRef}>Your Courses</div>
      {deleting && 
        <div className="alert alert-warning my-3">
          <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
          <span className='text-lg'>Are you sure you want to delete {courseToDelete.name} from Your Courses? All associated notes will also be deleted!</span>
          <div className='space-x-3'>
            <button className="btn btn-accent normal-case text-lg" onClick={() => handleDeleteCourse(courseToDelete)}>Delete</button>
            <button className="btn normal-case text-lg" onClick={() => setDeleting(false)}>Cancel</button>
          </div>
        </div>
      }
      {(showConfirm || transition) &&
        <div className={`alert alert-success mb-2 transition-opacity duration-500 ${
          showConfirm ? 'opacity-100' : 'opacity-0'
        }`}>
          <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
          <span className='text-lg'>{confirmMessage}</span>
        </div>
      }
      <table className='table table-auto table-zebra'>
        <thead className='bg-gray-600 text-white'>
          <th className='text-lg'>Course Name</th>
          <th>&nbsp;</th>
        </thead>
        
        <tbody>
          {courses.length === 0 && 
            <td className='text-lg'>Add a course to start writing notes! They'll show up here when created.</td>
          }
          {courses.map(course => <tr key={course.name} className="hover">
              <td className='text-lg'>{course.name}</td>
              <td>
                <div className='float-right space-x-3'>
                  <Link to={`/notes/${course.courseId}`} className='btn text-white text-lg bg-violet-600 hover:bg-violet-700 normal-case'>View Notes</Link>
                  <button onClick={() => {fillOutEditForm(course); scrollToSection(editRef)}} className='btn btn-secondary text-lg normal-case'>Edit Course</button>
                  <button onClick={() => {deleteAlert(course); scrollToSection(courseRef)}} className='btn btn-accent text-lg normal-case'>Delete Course</button>
                </div>
              </td>
            </tr>)}
        </tbody>
      </table>
    </div>
  )
}

// TODO:
// Change error messages in backend