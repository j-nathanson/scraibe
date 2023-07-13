import { useLocation } from "react-router-dom";

function NotFound() {

  const location = useLocation();

  return <div className="container-fluid">
    NOT FOUND {location.state && location.state.msg}
  </div>
}

export default NotFound;