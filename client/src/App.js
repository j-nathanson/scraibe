import { useCallback, useEffect, useState } from "react";
import LoginForm from "./components/LoginForm";
import Home from "./components/Home";
import SignUpForm from "./components/SignUpForm";
import AdminPortal from "./components/AdminPortal";
import UserPortal from "./components/UserPortal";
import NotePage from "./components/NotePage";
import NoteForm from "./components/NoteForm";
import CoursePage from "./components/CoursePage";
import ViewNote from "./components/ViewNote";
import NavBar from "./components/NavBar";
import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import AuthContext from "./context/AuthContext";
import { refreshToken, signOut } from "./services/authApi.js";
import Footer from "./components/Footer";
import NavBarStyle from "./components/NavBar";

const EMPTY_USER = {
  username: '',
  roles: []
};

const WAIT_TIME = 1000 * 60 * 29;

function App() {
  const [user, setUser] = useState(EMPTY_USER);

  const refreshUser = useCallback(() => {
    refreshToken()
      .then(existingUser => {
        setUser(existingUser);
        setTimeout(refreshUser, WAIT_TIME);
      })
      .catch(err => {
        console.log(err);
      });
  }, []);

  useEffect(() => {
    refreshUser();
  }, [refreshUser]);

  const auth = {
    user: user,
    isLoggedIn() {
      return !!user.username;
    },
    hasRole(role) {
      return user.roles.includes(role);
    },
    onAuthenticated(user) {
      setUser(user);
      setTimeout(refreshUser, WAIT_TIME);
    },
    signOut() {
      setUser(EMPTY_USER);
      signOut();
    }
  };

  const maybeRedirect = (component, role) => {
    if (!auth.isLoggedIn() || (role && !auth.hasRole(role))) {
      return <Navigate to="/" />;
    }
    return component;
  }

  return (
    <div className="min-h-screen">
      <AuthContext.Provider value={auth}>
        <Router>
          <NavBar />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<LoginForm />} />
            <Route path="/signup" element={<SignUpForm />} />
            <Route path="/admin-portal" element={maybeRedirect(<AdminPortal />, 'admin')} />
            <Route path="/user-portal" element={maybeRedirect(<UserPortal />, 'user')} />
            <Route path="/notes/:courseId" element={maybeRedirect(<CoursePage />, 'user')} />
            <Route path="/add-note/:courseId" element={maybeRedirect(<NoteForm />, 'user')} />
            <Route path="/edit-note/:courseId/:noteId" element={maybeRedirect(<NoteForm />, 'user')} />
            <Route path="/view-note/:noteId" element={maybeRedirect(<ViewNote />, 'user')} />
          </Routes>
          <Footer />
        </Router>
      </AuthContext.Provider>
    </div>
  );
}

export default App;
