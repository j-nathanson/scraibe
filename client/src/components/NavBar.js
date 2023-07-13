import React from "react";
import { useEffect, useState, useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthContext from "../context/AuthContext";

function NavBar() {
  const auth = useContext(AuthContext);
  const navigate = useNavigate();

  const handleSignOut = () => {
    auth.signOut();
    navigate("/");
  };

  //flex w-full flex-wrap items-center justify-between px-3

  return (
    <nav className="navbar bg-emerald-900 text-rose-200 font-mono text-lg font-semi-bold w-full p-0 justify-between flex-wrap sticky top-0 z-10">
      <div className="container mx-0 px-0 flex w-full flex-wrap items-center justify-between navbar-start">
        <div className="dropdown">
          <label tabindex="0" className="btn btn-ghost btn-circle lg:hidden">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5"
              fill="none"
              viewBox=" 0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M4 6h16M4 12h16M4 18h16M4"
              />
            </svg>
          </label>
          <ul className="menu menu-sm dropdown-content mt-3 z-10 text-black shadow font-semibold bg-red-100 rounded-box w-52">
            <li>
              <a href="/">Home</a>
            </li>
            <li>
              <a href="/login">Log In</a>
            </li>
            <li>
              <a href="/signup">Sign Up</a>
            </li>
            {auth.user.username && (
              <li>
                <a href="/" onClick={handleSignOut}>Sign Out</a>
              </li>
            )}
          </ul>
        </div>
      </div>
      {/* //max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 */}
      <div className="flex flex-row w-full items-center justify-between px-0">
        <div className="flex items-center justify-between h-16 w-full px-0 mx-0">
          {/* Scribe Logo - change to img for logo */}
          {/* Hyperlink to the main page */}
          <div className="flex-shrink-0 ml-4">
            <header>
              <a href="/">Scr-<span className="text-rose-500">AI</span>-be</a>
            </header>
          </div>
        </div>
        {/* Navigation links */}
        <div className=" ml-auto mr-4 justify-items-end">
          <div className="flex items-center space-x-4">
            <Link
              to="/"
              className="text-violet-50 hover:bg-gray-200 hover:text-black px-3 py-2 rounded-md text-sm font-lg font-semi-bold"
            >
              Home
            </Link>
            {!auth.user.username && (
              <>
                <Link
                  to="/login"
                  className="text-violet-50 hover:bg-gray-200 hover:text-black px-3 py-2 rounded-md text-sm font-lg font-semi-bold whitespace-nowrap"
                >
                  Log In
                </Link>
                <Link
                  to="/signup"
                  className="text-violet-50 hover:bg-gray-200 hover:text-black px-3 py-2 rounded-md text-sm font-lg font-semi-bold whitespace-nowrap"
                >
                  Sign Up
                </Link>
              </>
            )}
            {auth.hasRole("admin") && (
              <Link
                to="/admin-portal"
                className="text-violet-50 hover:bg-gray-200 hover:text-black px-3 py-2 rounded-md text-sm font-lg font-semi-bold whitespace-nowrap"
              >
                Admin Portal
              </Link>
            )}
            {auth.hasRole("user") && !auth.hasRole("admin") && (
              <Link
                to="/user-portal"
                className="text-violet-50 hover:bg-gray-200 hover:text-black px-3 py-2 rounded-md text-sm font-lg font-semi-bold whitespace-nowrap"
              >
                User Portal
              </Link>
            )}
            {auth.user.username && (
              <>
                <button
                  type="button"
                  className="text-white hover:bg-gray-100 hover:text-black px-3 py-2 rounded-md text-sm font-lg font-semi-bold whitespace-nowrap"
                  onClick={handleSignOut}
                >
                  Sign out
                </button>

                <span className="text-red-300 text-sm whitespace-nowrap">
                  Hello, {auth.user.username}!
                </span>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}

export default NavBar;

//- [ ] style navbar
// - [x] include dropdown for small screen sizes (add hamburger icon) when page is minimized and for phone browsers
// - [ ] add "Home", "Sign Up", "Log In"
// - [ ] add image for "Scr-AI-be" logo
// - [ ] add logo hyperlink to homepage
// - [ ] style drop down menu
// - [ ] add link to dropdown menu for: "Home", "Sign Up", "Log In"
