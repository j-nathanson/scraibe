import React, { useEffect, useRef, useContext } from 'react';
import UserPortal from './UserPortal';
import { getUsers, editUser, deleteByUserId } from '../services/UserApi';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthContext from '../context/AuthContext';



const DEFAULT_USER = {
  appUserId: 0,
  username: "",
  email: "",
  enabled: 0
}

export default function AdminPortal() {
  const auth = useContext(AuthContext);
  const [users, setUsers] = useState([]);
  const [deleting, setDeleting] = useState(false);
  const [userToDelete, setUserToDelete] = useState(DEFAULT_USER);
  // activating variable is used to determine if an alert should show, is true for either activating or deactivating
  const [activating, setActvating] = useState(false);
  const [userToActivate, setUserToActivate] = useState(DEFAULT_USER);
  const [showConfirm, setShowConfirm] = useState(false);
  const [transition, setTransition] = useState(false);
  const [confirmMessage, setConfirmMessage] = useState("");

  const userRef = useRef(null);

useEffect(() => {
  fetchData();
}, []);

useEffect(() => {
  let timeout;
  if (!showConfirm) {
    timeout = setTimeout(() => {
      setTransition(false);
    }, 500); // Delay to allow the fade-out animation to play (e.g., 500ms = 0.5 seconds)
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


const fetchData = async () => {
  try { 
    const fetchedUsers = await getUsers();
    setUsers(fetchedUsers);
  } catch (error) {
    console.error('Error fetching data:', error);
  }
};

const handleActivation = (user) => {
    user.enabled = !user.enabled;
    editUser(user)
      .then(() => {
        fetchData();
        setActvating(false);
        setUserToActivate(DEFAULT_USER);
        confirmation(user.username + "'s account has been " + (user.enabled ? "activated!" : "deactivated!"));

      })
      .catch(console.log);
}
  
  const handleDelete = (user) => {
    deleteByUserId(user.appUserId)
    .then(() => {
      fetchData();
      setDeleting(false);
      setUserToDelete(DEFAULT_USER);
      confirmation(user.username + "'s account has been deleted!");
    })
    .catch(console.log);
}

const deleteAlert = (user) => {
  setDeleting(true);
  setUserToDelete(user);
  setActvating(false);
}

const activateAlert = (user) => {
  setActvating(true);
  setUserToActivate(user);
  setDeleting(false);
}

const scrollToSection = (ref) => {
  ref.current.scrollIntoView({ behavior: 'smooth' });
};

return (<>
    <div className='container flex flex-col px-4 mx-auto bg-grey-200 mt-4'>
      <h1 className='text-4xl font-extrabold text-black pb-5 text-center'>Admin Portal</h1>
      <div className='text-2xl font-bold text-black pb-5' ref={userRef}>Users</div>
      {deleting && 
        <div className="alert alert-warning my-3">
          <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
          <span className='text-lg'>Are you sure you want to delete {userToDelete.username}'s account?</span>
          <div className='space-x-3'>
            <button className="btn btn-accent normal-case text-lg" onClick={() => handleDelete(userToDelete)}>Delete</button>
            <button className="btn normal-case text-lg" onClick={() => setDeleting(false)}>Cancel</button>
          </div>
        </div>
      }

      {activating &&
        <div className="alert alert-warning my-3">
          <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
          <span className='text-lg'>Are you sure you want to {userToActivate.enabled ? 'deactivate' : 'activate'} {userToActivate.username}'s account?</span>
          <div className='space-x-3'>
            {userToActivate.enabled ? 
              <button className="btn btn-accent normal-case text-lg" onClick={() => handleActivation(userToActivate)}>Deactivate</button> :
              <button className="btn btn-ghost text-white text-lg bg-emerald-600 hover:bg-emerald-700 normal-case" onClick={() => handleActivation(userToActivate)}>Activate</button>
            }
            <button className="btn normal-case text-lg" onClick={() => setActvating(false)}>Cancel</button>
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

      <div className='pb-3'>
        <table className='table table-auto table-zebra pb-9'>
          <thead className='bg-gray-600 text-white'>
            <th className='text-lg'>Username</th>
            <th className='text-lg'>Email</th>
            <th>&nbsp;</th>
          </thead>
          <tbody>
            {users.length >=1 && users.map(user =>
              <tr key={user.appUserId} className='hover'>
                <td className='text-lg'>{user.username}</td>
                <td className='text-lg'>{user.email}</td>
                <td>
                  <div className='float-right space-x-3'>
                    {user.username === auth.user.username ?
                    <>
                     <button onClick={() => {activateAlert(user); scrollToSection(userRef);}} className='btn btn-secondary text-lg normal-case btn-disabled'> {user.enabled ? 'Deactivate User' : 'Activate User'}</button>
                     <button onClick={() => {deleteAlert(user); scrollToSection(userRef)}} className='btn btn-accent text-lg normal-case btn-disabled'> Delete User</button></> :
                     <>
                     <button onClick={() => {activateAlert(user); scrollToSection(userRef);}} className='btn btn-secondary text-lg normal-case '> {user.enabled ? 'Deactivate User' : 'Activate User'}</button>
                    <button onClick={() => {deleteAlert(user); scrollToSection(userRef)}} className='btn btn-accent text-lg normal-case'> Delete User</button></>
                    }
                  </div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
      <div className="divider"></div> 
    </div>
    <UserPortal/>
    </>
  )
}
