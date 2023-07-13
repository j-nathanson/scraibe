export default function Errors({ errors }) {

    const getErrorList = (err) => {
      if (Array.isArray(err)) {
        return err;
      } else {
        return [err];
      }
    }
  
    return <>
      {getErrorList(errors).length > 0 && 
        <div className="alert alert-error bg-accent text-white w-1/2">
          <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
          <div>
            The following errors were encountered:
            <ul className="list-disc pl-10">
              {getErrorList(errors).map(err => <li key={err}>{err}</li>)}
            </ul>
          </div>
        </div>
      }
    </>
  }

