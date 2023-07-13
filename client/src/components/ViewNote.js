import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { getByNoteId } from "../services/noteApi";

export default function ViewNote() {
    const { noteId } = useParams();
    const [note, setNote] = useState({});
    const [formattedDate, setFormattedDate] = useState("");
    const navigate = useNavigate();
    const [isError, setIsError] = useState(false);
    const [errorsList, setErrorsList] = useState([]);


    useEffect(() => {
        const fetchNote = async () => {

            try {
                const data = await getByNoteId(noteId);
                setNote(data);
                const inputDate = data.date;
                const parts = inputDate.split("-");
                const formattedDate = `${parts[1]}/${parts[2]}/${parts[0]}`;
                setFormattedDate(formattedDate);
                setIsError(false);
            } catch (error) {
                setIsError(true);
                setErrorsList(error);
            }
        }

        fetchNote();
    }, []);

    const handleEdit = () => {
        navigate(`/edit-note/${note.courseId}/${noteId}`);
    }

    const handleReturnToCourse = () => {
        navigate(`/notes/${note.courseId}`);
    }


    return (
        <div className='container flex flex-col px-4 mx-auto w-3/4 mt-4'>
            {/* Error message */}
            {isError && <div className="alert alert-error">
                <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                <span>Error! your note could not be found '{errorsList.map(error => <span className='list-none' key={error}>{error}</span>)}'</span>
            </div>}
            {/* Breadcrumb navigation */}
            <div className="text-sm breadcrumbs">
                <ul>
                    <li><Link to="/user-portal">User Portal</Link></li>
                    <li><Link to={`/notes/${note.courseId}`}> Course</Link></li>
                    <li>View Note </li>
                </ul>
            </div>
            <h1 className='text-4xl font-extrabold text-black pb-5 text-center'>{note.title} - {formattedDate}</h1>

            <fieldset className="form-control">
                <textarea
                    name="content"
                    id="content"
                    cols="60"
                    rows="15"
                    value={note.content}
                    className="textarea border-zinc-400"
                    readOnly>
                </textarea>
            </fieldset>
            <div className="mt-3">
                <button
                    type="button"
                    className="btn text-white bg-emerald-600 hover:bg-emerald-700 normal-case"
                    onClick={() => handleEdit()}>
                    Edit Note
                </button>
                <button
                    type="button"
                    className="btn bg-violet-600 hover:bg-violet-700 text-white ml-3 normal-case"
                    onClick={() => handleReturnToCourse()}>
                    Return to Course
                </button>
            </div>

        </div>
    )
}


// {/* <button type="submit" className='btn text-white text-base bg-emerald-600 hover:bg-emerald-700'></button> */ }