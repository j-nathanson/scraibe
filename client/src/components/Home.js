import React, { useContext, useRef } from 'react'
import LoginForm from './LoginForm'
import AuthContext from '../context/AuthContext'
import HomeLoginForm from './HomeLoginForm'
import TryMe from './TryMe'



function Home() {

  const TryMeRef = useRef(null);
  const scrollToSection = (ref) => {
    ref.current.scrollIntoView({ behavior: 'smooth' });
  };


  const auth = useContext(AuthContext);
  return (<>
    <header>

    <div className="hero min-h-screen bg-gradient-to-r from-stone-200 to-emerald-100">
    <div className="hero-content text-center">
    <div className="max-w-lg">
      <h1 className='text-6xl font-extrabold text-black pb-10 text-center' id="formHeading">Got Unorganized Notes?</h1>
      <h2 className='text-4xl font-extrabold text-black pb-10 text-center' id="formHeading">Use Scr-AI-be!</h2>
      <button onClick={()=>scrollToSection(TryMeRef)} className="btn btn-primary className='btn text-white text-lg bg-emerald-600 hover:bg-emerald-700">Click Here to Try it Out</button>
    </div>
    </div>
    </div>
    </header>

    <HomeLoginForm/>
    
    <div ref={TryMeRef}><TryMe/></div>
    
    

  </>)
}
export default Home;




