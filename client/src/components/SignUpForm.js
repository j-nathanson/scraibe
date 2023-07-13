import React from 'react'
import { useEffect, useState, useContext } from 'react';
import { Link, useNavigate } from "react-router-dom";
import AuthContext from "../context/AuthContext";
import { create } from "../services/authApi";
import Errors from "../components/Errors";


const USER_DEFAULT = {
  username: "",
  password: "",
  email: ""
}

//test password: P@ssw0rd!

export default function SetUpForm() {
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
    console.log("Creating user: ");
    console.log(user);

    // uncomment when ready to connect to backend

    create(user).then(user => {
      auth.onAuthenticated(user);
      // find way to see if user is normal user or admin, navigate will go to different places depending on role
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
        <h1 id="formHeading" className='text-4xl font-extrabold text-black pb-10 text-center'>Sign Up</h1>
        <Errors errors={errors} />
        <form onSubmit={handleSubmit} id="form" className='space-y-4'>
          <fieldset className=''>
            <label htmlFor="section" className='text-lg'>Username</label><br/>
            <input id="username"
                name="username"
                type="text"
                value={user.username}
                onChange={handleChange} 
                className='input input-bordered w-full text-lg border-zinc-400'/>
          </fieldset>
          <fieldset className=''>
            <label htmlFor="section" className='text-lg'>Email</label><br/>
            <input id="email"
                name="email"
                type="text"
                value={user.email}
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
            <button type="submit" className='btn text-white text-lg bg-emerald-600 hover:bg-emerald-700 normal-case'>Sign Up</button>
            <Link className='btn btn-accent text-lg normal-case' type="button" to={"/"}>Cancel</Link>
          </div>
        </form>
      </div>
    </div>
  )
}
