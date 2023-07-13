import { useEffect, useState, useContext, React } from 'react';
import { Link, useNavigate } from "react-router-dom";
import AuthContext from "../context/AuthContext"
import { authenticate } from "../services/authApi";
import Errors from "./Errors";

const USER_DEFAULT = {
    username: "",
    password: ""
}

export default function HomeLoginForm() {

    const [user, setUser] = useState(USER_DEFAULT)
    const [errors, setErrors] = useState([]);

    const url = 'http://localhost:8080/security';

    const navigate = useNavigate();

    const auth = useContext(AuthContext);

    // console.log(auth.user.roles)


    const handleChange = (event) => {
        // make a copy of the object 
        const newUser = { ...user };

        // update the value of the property changed 
        newUser[event.target.name] = event.target.value;

        // set the state 
        setUser(newUser);
    }

    const handleSubmit = (evt) => {
        evt.preventDefault();
        console.log("Submitting user: ");
        console.log(user);

        authenticate(user).then(user => {
            auth.onAuthenticated(user);

            if (user.roles.includes("admin")) {
                navigate("/admin-portal")
            } else {
                navigate('/user-portal');
            }
        })
            .catch(err => getErrorList(err));
    };

    const getErrorList = (err) => {
        if (Array.isArray(err)) {
            setErrors(err)
        } else {
            setErrors([err]);
        }
    }

    return (
        <div className="flex flex-row hero min-h-[60vh] bg-gray-300 justify-items-center px-6 mb-6">
            <div className='card w-[45vw] shadow-xl bg-white p-10 mr-10 center-items'>
                <div className='card'>
                    {/* Maybe change to be hndled in this component */}
                    {errors.length > 0 &&
                        <div className="alert alert-error bg-accent text-white w-full mb-3">
                            <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                            <div>
                                The following errors were encountered:
                                <ul className="list-disc pl-10">
                                    {errors.map(err => <li key={err}>{err}</li>)}
                                </ul>
                            </div>
                        </div>
                    }
                    <form id="form" onSubmit={handleSubmit} className='space-y-4 '>
                        <fieldset className=''>
                            <label htmlFor="section" className='text-lg'>Username</label><br />
                            <input id="username"
                                name="username"
                                type="text"
                                value={user.username}
                                onChange={handleChange}
                                className='input input-bordered w-full text-lg border-zinc-400' />
                        </fieldset>
                        <fieldset className=''>
                            <label htmlFor="section" className='text-lg'>Password</label><br />
                            <input id="password"
                                name="password"
                                type="password"
                                value={user.password}
                                onChange={handleChange}
                                className='input input-bordered w-full text-lg border-zinc-400' />
                        </fieldset>
                        <div className="space-x-3">
                            <button type="submit" className='btn text-white text-lg bg-emerald-600 hover:bg-emerald-700 normal-case mb-2'>Login</button>
                        </div>
                        <Link to='/signup' className='underline'>Don't have an Account? Sign up here</Link>
                    </form>
                </div>
            </div>
            <div className='w-1/2 '>
                <h1 className='text-4xl font-extrabold text-black pb-10 text-left'>Utilize the AI of the future to organize your notes today!</h1>
            </div>
        </div>

    )
} 