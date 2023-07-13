import React, { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getCoursesById } from '../services/CourseApi';
import { deleteNote } from '../services/noteApi';

export default function CoursePage() {
    const [courseName, setCourseName] = useState("");
    const [notes, setNotes] = useState([]);
    const { courseId } = useParams();
    const navigate = useNavigate();
    const [isError, setIsError] = useState(false);
    const [isSuccess, setIsSuccess] = useState(false);
    const [errorsList, setErrorsList] = useState([]);
    const [deleting, setDeleting] = useState(false);
    const [noteToDelete, setNoteToDelete] = useState({});

    useEffect(() => {
        const fetchNotes = async () => {

            try {
                const data = await getCoursesById(courseId);
                setCourseName(data.name)
                setNotes(data.notes);
                setIsError(false);
            } catch (error) {
                setIsError(true);
                setErrorsList(error);
            }
        }

        fetchNotes();
    }, [])

    const handleAdd = () => {
        navigate(`/add-note/${courseId}`)
    }

    const handleReturn = () => {
        navigate("/user-portal")
    }

    const handleDeleteNote = async () => {
        try {
            const result = await deleteNote(noteToDelete);
            setNotes(prevNotes => prevNotes.filter(n => n.noteId !== noteToDelete.noteId));
            setDeleting(false);
            setIsError(false);
            setIsSuccess(true);
        } catch (errors) {
            //update how to handle errors
            console.log(errors)
        }
    }

    return (
        <div className='container flex flex-col px-4 mx-auto mt-4 mb-5'>
            {/* Breadcrumb navigation */}
            <div className="text-sm breadcrumbs">
                <ul>
                    <li><Link to="/user-portal">User Portal</Link></li>
                    <li>Course</li>
                </ul>
            </div>

            <h1 className='text-4xl font-extrabold text-black pb-5 text-center'>{courseName}</h1>

            {isSuccess && <div className="alert alert-success mb-2">
                <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                <span>Your note has been deleted!</span>
            </div>}

            {/* Error message */}
            {isError && <div className="alert alert-error">
                <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                <span>Error! your notes could not be found '{errorsList.map(error => <span className='list-none' key={error}>{error}</span>)}'</span>
            </div>}

            {deleting &&
                <div className="alert alert-warning mb-4">
                    <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
                    <span>Are you sure you want to delete note '{noteToDelete.title}'?</span>
                    <div className='space-x-3'>
                        <button className="btn btn-sm btn-accent normal-case" onClick={() => handleDeleteNote()}>Delete</button>
                        <button className="btn btn-sm normal-case" onClick={() => setDeleting(false)}>Cancel</button>
                    </div>
                </div>
            }

            {notes.length === 0 && (
                <div className="alert alert-info mb-4">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" className="stroke-current shrink-0 w-6 h-6"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                    <span>No notes for this this course, click below to add a new note!</span>
                </div>
            )}

            <table className='table table-zebra'>
                <tbody>
                    {notes.map(note => <Note note={note} setIsSuccess={setIsSuccess} setDeleting={setDeleting} setNoteToDelete={setNoteToDelete} key={note.noteId} />)}
                </tbody>
            </table>

            <div className='flex flex-row w-10 mt-2'>
                <button className='btn text-white text-base bg-emerald-600 hover:bg-emerald-700 normal-case' onClick={() => handleAdd()}>Add New Note</button>
                <button className='btn text-white text-base bg-violet-600 hover:bg-violet-700 normal-case ml-3' onClick={() => handleReturn()}>Return to Courses</button>
            </div>
        </div>
    )
}

function Note({ note, setDeleting, setIsSuccess, setNoteToDelete }) {
    const { noteId, title, date } = note;
    const navigate = useNavigate();
    const { courseId } = useParams();
    const inputDate = date;
    const parts = inputDate.split("-");
    const formattedDate = `${parts[1]}/${parts[2]}/${parts[0]}`;

    const handleView = () => {
        navigate(`/view-note/${noteId}`);
    }
    const handleEdit = () => {
        navigate(`/edit-note/${courseId}/${noteId}`);
    }
    const handleDelete = async () => {
        setIsSuccess(false);
        setDeleting(true);
        setNoteToDelete(note);
    }
    return (
        <tr className="hover">
            <td className="text-lg">
                {title} - {formattedDate}
            </td>
            <td className="button-container float-right space-x-3">
                <button className='btn text-white text-base bg-violet-600 hover:bg-violet-700 normal-case' onClick={() => handleView()}>View</button>
                <button className='btn btn-secondary text-base normal-case' onClick={() => handleEdit()}>Edit</button>
                <button className='btn btn-accent text-base normal-case' onClick={() => handleDelete()}>Delete</button>
            </td>
        </tr>
    )
}