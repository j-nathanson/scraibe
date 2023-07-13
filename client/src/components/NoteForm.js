import React, { useContext } from 'react';
import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { addNote, editNote, generateNote, getByNoteId } from '../services/noteApi';
import AuthContext from '../context/AuthContext';


function NoteForm() {
  const auth = useContext(AuthContext);
  const { courseId, noteId } = useParams();

  const DEFAULT_NOTE = {
    noteId: 0,
    title: '',
    content: '',
    date: '',
    courseId,
    userId: 0
  };

  const [note, setNote] = useState(DEFAULT_NOTE);
  const [previousNote, setPreviousNote] = useState(DEFAULT_NOTE);
  const [organized, setOrganized] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const [isError, setIsError] = useState(false);
  const [errorsList, setErrorsList] = useState([]);

  const navigate = useNavigate();

  const handleChange = (event) => {
    const newNote = { ...note };

    newNote[event.target.name] = event.target.value;

    setNote(newNote);
  }

  const handleSave = async () => {

    let newNote = {
      ...note,
      userId: auth.user.userId
    };

    if (!noteId) {
      //path add note
      try {
        newNote = await addNote(newNote);
        setNote(newNote);
        setIsSuccess(true);
      } catch (errors) {
        console.log(errors)
        setIsError(true)
        setErrorsList(errors)
      }
    } else {
      // path edit note
      newNote.noteId = noteId;
      try {
        await editNote(newNote);
        setIsSuccess(true);
      } catch (errors) {
        setIsError(true)
        setErrorsList(errors)
      }
    }
  }

  const handleOrganize = async () => {
    setIsLoading(true);
    try {
      let newNote;
      if (!organized) {
        newNote = await generateNote(note);
        setPreviousNote(note)
        setOrganized(true);
      } else {
        newNote = await generateNote(previousNote);
      }
      setIsLoading(false);
      setNote(newNote);
    } catch (errors) {
      setIsError(true)
      setErrorsList(errors)
    }
  }

  const handleReset = () => {
    setNote(DEFAULT_NOTE);
    setPreviousNote(DEFAULT_NOTE);
    setOrganized(false);
  }

  const handleSubmit = async (evt) => {
    setIsSuccess(false);
    setIsError(false);
    evt.preventDefault();
    // check which button was pressed so appropriate function is called
    const buttonValue = evt.nativeEvent.submitter.value;

    if (buttonValue === "save-btn") {
      handleSave();
    } else {
      handleOrganize();
    }
  }

  const handleReturnCourse = () => {
    setNote(DEFAULT_NOTE);
    navigate(`/notes/${courseId}`);
  }

  // if there is a 'noteId' from the params, update the form information with it's pre-made data
  useEffect(() => {
    const fetchData = async () => {
      // Only do this if there is an `id`
      if (noteId) {
        const data = await getByNoteId(noteId);
        setNote(data);
      }
    };

    fetchData();
  }, [noteId]);


  return (
    <div className='container flex flex-col px-4 mx-auto w-3/4 min-h-screen mt-4 mb-5'>
      {/* Breadcrumb navigation */}
      <div className="text-sm breadcrumbs">
        <ul>
          <li><Link to="/user-portal">User Portal</Link></li>
          <li><Link to={`/notes/${courseId}`}> Course</Link></li>
          <li>Note Form</li>
        </ul>
      </div>
      {/* Success message */}
      {isSuccess && <div className="alert alert-success mb-2">
        <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
        <span>Your note has been {note.noteId === 0 ? "added!" : "updated!"}</span>
      </div>}

      {/* Error message */}
      {isError && <div className="alert alert-error">
        <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
        <span>Error! your note could not be {note.noteId === 0 ? "added!" : "updated!"} '{errorsList.map(error => <span className='list-none' key={error}>{error}</span>)}'</span>
      </div>}

      {/* Heading */}
      <h1 className='text-4xl font-extrabold text-black pb-5 text-center'>{note.noteId === 0 ? "Add Note" : "Edit Note"}</h1>

      <form onSubmit={handleSubmit}>
        <fieldset className="form-control">
          <label className="label" htmlFor="title">Enter Title</label>
          <input
            type="text"
            name="title"
            id="title"
            value={note.title}
            required
            className="input border-zinc-400 w-full"
            placeholder='My Title...'
            onChange={handleChange} />
        </fieldset>
        <fieldset className="form-control">
          <label className="label" htmlFor="date">Enter Date</label>
          <input
            type="date"
            name="date"
            id="date"
            required
            value={note.date}
            className="input border-zinc-400 w-full"
            onChange={handleChange} />
        </fieldset>
        <fieldset className="form-control">
          <label className="label" htmlFor="content">Write your note here: </label>
          <textarea
            className="textarea border-zinc-400"
            name="content"
            id="content"
            cols="60"
            rows="15"
            required
            value={note.content}
            placeholder="The construction methods employed in building the pyramids remain a subject of fascination and debate. While traditional theories attribute their creation to manual labor, a recent controversial hypothesis suggests the involvement of extraterrestrial intervention. Proponents argue that the precise alignment of the pyramids with celestial bodies and the sheer scale of the structures indicate the use of advanced technologies beyond the capabilities of ancient civilizations.The pyramids' primary function was believed to serve as monumental tombs for the pharaohs, housing their mortal remains and treasures. However, some alternative theories propose that these pyramids were designed as powerful energy sources or astronomical observatories. The intricate hieroglyphs adorning the interior walls offer glimpses into the beliefs and rituals associated with the afterlife, but their full significance remains elusive.Throughout history, explorers and archaeologists have sought hidden chambers within the pyramids, anticipating the discovery of untold riches and ancient artifacts. Although some chambers have been found, tantalizing clues suggest the existence of unexplored passageways and secret compartments. These hidden recesses might contain undiscovered treasures or provide further insight into the lives of ancient Egyptians.The precise alignments of the pyramids with celestial bodies have sparked theories of an astronomical connection. Some suggest that the pyramids were designed to align with specific stars, such as Orion's Belt, at significant moments throughout the year, possibly reflecting the ancient Egyptians' cosmic beliefs. These alignments may have served as celestial maps or markers for important astronomical events.The pyramids hold immense cultural significance, both for Egyptians and the global community."
            onChange={handleChange}></textarea>
        </fieldset>
        {isLoading && <div className='flex flex-row justify-center mt-2'>
          <div className="loading loading-bars loading-lg text-center text-primary"></div>
        </div>}
        <div className="mt-3">
          <button
            type="submit"
            name="save-btn"
            value="save-btn"
            className="btn text-white text-lg bg-emerald-600 hover:bg-emerald-700 normal-case">
            Save
          </button>
          <button
            type="submit"
            name="organize-btn"
            value="organize-btn"
            className="btn text-lg btn-secondary ml-3 normal-case">
            {organized ? "Regenerate Response" : "Organize"}
          </button>
          <button
            type="button"
            className="btn text-lg bg-orange-600 hover:bg-orange-700 text-white ml-3 normal-case"
            onClick={() => handleReset()}>
            Reset Note
          </button>
          <button
            type="button"
            className="btn text-lg bg-violet-600 hover:bg-violet-700 text-white ml-3 normal-case"
            onClick={() => handleReturnCourse()}>
            Return to Course
          </button>
        </div>
      </form>
    </div>
  )
}

export default NoteForm;