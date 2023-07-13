import { useEffect, useState, useContext, React} from 'react';
import { Link, useNavigate } from "react-router-dom";
import AuthContext from "../context/AuthContext"
import { authenticate } from "../services/authApi";
import Errors from "../components/Errors";

const USER_DEFAULT = {
  username: "",
  password: ""
}


export default function LoginForm() {
  const [user, setUser] = useState(USER_DEFAULT)
  const [errors, setErrors] = useState([]);

  const url = 'http://localhost:8080/security';

  const navigate = useNavigate();

  const auth = useContext(AuthContext);

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

      if (user.roles.includes("admin")){
        navigate("/admin-portal")
      } else {
        navigate('/user-portal');
      }
    })
    .catch(err => setErrors(err));
  };


  return (
    <div className='flex flex-row px-4 mx-auto justify-center mt-4'>
      <div className='w-1/3'>
          <h1 className='text-4xl font-extrabold text-black pb-10 text-center' id="formHeading">Login</h1>
          <Errors errors={errors} />
          <form onSubmit={handleSubmit} id="form" className='space-y-4'>
            <fieldset className=''>
              {/* Change this later to also include email */}
              <label htmlFor="section" className='text-lg'>Username</label><br/>
              <input id="username"
                  name="username"
                  type="text"
                  value={user.username}
                  onChange={handleChange} 
                  className='input input-bordered w-full text-lg border-zinc-400'/>
            </fieldset>
            <fieldset className=''>
              <label htmlFor="section" className='text-lg'>Password</label><br/>
              <input id="password"
                  name="password"
                  type="password"
                  value={user.password}
                  onChange={handleChange} 
                  className='input input-bordered w-full text-lg border-zinc-400'/>
            </fieldset>
            <div className="space-x-3">
              <button type="submit" className='btn text-white text-lg bg-emerald-600 hover:bg-emerald-700 normal-case'>Login</button>
              <Link className='btn btn-accent text-lg normal-case ' to={"/"}>Cancel</Link>
            </div>
          </form>
        </div>
    </div>
  )
}
