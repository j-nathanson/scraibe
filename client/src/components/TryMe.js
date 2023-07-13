import { useState } from "react";
import { generateNote } from "../services/noteApi";


export default function TryMe() {

    const DEFAULT_NOTE = {
        noteId: 0,
        title: '',
        content: "The Industrial Revolution marked a significant shift in human history, characterized by the transition from agrarian economies to industrialized societies. It began in the 18th century in Britain and later spread to Europe, North America, and beyond. Factors such as technological advancements, urbanization, and improved transportation fueled this revolution. It led to the rise of factories, mass production, and the growth of cities. Social and economic changes accompanied this era, including the emergence of the working class and the division of labor. The Industrial Revolution played a pivotal role in shaping modern society and setting the stage for subsequent advancements in science, technology, and globalization.",
        date: '',
        courseId: 0,
        userId: 0
    };

    const [exampleNote, setExampleNote] = useState(DEFAULT_NOTE);
    const [resultNote, setResultNote] = useState({ content: '' });
    const [isLoading, setIsLoading] = useState(false);

    const handleOrganize = async () => {
        setIsLoading(true);
        try {
            const newNote = await generateNote(exampleNote);
            setResultNote(newNote);
            setIsLoading(false);
        } catch (errors) {
            console.log(errors);
        }
    }

    return (
        <section className="mb-7">
            <h2 className="text-4xl font-extrabold text-black pb-10 text-center">Try Me!</h2>
            <div className="flex flex-row justify-center space-x-5 mb-3">
                <fieldset className="form-control">
                    <label htmlFor="content" className="text-center text-3xl font-extrabold text-black mb-2">Your Note</label>
                    <textarea
                        className="textarea border-zinc-400"
                        name="content"
                        id="content"
                        cols="60"
                        rows="15"
                        required
                        value={exampleNote.content}
                        placeholder="some text"
                        readOnly
                    >
                    </textarea>
                </fieldset>

                <fieldset className="form-control">
                    <label htmlFor="content" className="text-center text-3xl font-extrabold text-black mb-2">Organized Note</label>
                    <textarea
                        className="textarea border-zinc-400"
                        name="content"
                        id="content"
                        cols="60"
                        rows="15"
                        required
                        value={resultNote.content}
                        readOnly
                        placeholder='Click on "Organize" to see Scr-AI-be in action!'
                    >

                    </textarea>
                </fieldset>
            </div>
            {isLoading && <div className='flex flex-row justify-center my-3 '>
                <div className="loading loading-bars loading-lg text-center text-primary"></div>
            </div>}
            <div className="flex flex-row justify-center w-100">
                <button className="btn text-white text-lg bg-emerald-600 hover:bg-emerald-700 normal-case" onClick={() => handleOrganize()}>Organize</button>
            </div>
        </section>
    )
}
