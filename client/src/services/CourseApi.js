// const courseUrl = 'http://localhost:8080/courses';
const courseUrl = 'http://18.118.122.152:8080/courses'

//courses
export async function getAllCourses() {
    const jwtToken = localStorage.getItem('jwt_token');
  
    const init = {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Authorization': `Bearer ${jwtToken}`
      }
    };
    const response = await fetch(courseUrl, init);

    if (response.status === 200 || response.status < 300) {
        const data = await response.json();
        return data;
      } else {
        return Promise.reject('Error');
    }
}


//getcoursebyid
export async function getCoursesById(id) {
    const jwtToken = localStorage.getItem('jwt_token');
  
    const init = {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Authorization': `Bearer ${jwtToken}`
      }
    };

    const response = await fetch(`${courseUrl}/${id}`, init);
    if (response.status === 200 || response.status < 300) {
      return response.json();
    } else {
      return Promise.reject(`Course: ${id} was not found.`);
    }
  }

//getcoursebyUserId
export async function getCoursesByUserId(id) {
    const jwtToken = localStorage.getItem('jwt_token');
  
    const init = {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Authorization': `Bearer ${jwtToken}`
      }
    };

    const response = await fetch(`${courseUrl}/from-user/${id}`, init);
    if (response.status === 200 || response.status < 300) {
      return response.json();
    } else {
      throw new Error(`User: ${id} was not found.`);
    }
  }

//addcourse **
export async function addCourse(course) {
    const jwtToken = localStorage.getItem('jwt_token');
  
    const init = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${jwtToken}`
      },
      body: JSON.stringify(course)
    };

    const response = await fetch(`${courseUrl}`, init);
    if (response.status === 200 || response.status < 300) {
      return response.json();
    } else if (response.status >= 400 || response.status < 500) {
        return response.json();
    }
  
  }

//editcourse
export async function editCourse(id, course) {  
    // debugger;
    const jwtToken = localStorage.getItem('jwt_token');
    const init = {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${jwtToken}`
      },
      body: JSON.stringify(course)
    };
    const response = await fetch(`${courseUrl}/${id}`, init);
    // debugger;
    if (response.status === 404) {
      return Promise.reject(`Course: ${id} was not found.`);
    } else if (response.status === 400) {
      const errors = await response.json();
      return errors;
    } else if (response.status === 409) {
      return Promise.reject('Oopsie');
    } else if (response.status === 403) {
      return Promise.reject('Permission Error');
    } else if (response.status === 204) {
      return null;
    }
  }

//deletecoursebycourseId
export async function deleteCoursesById(id) {
    const jwtToken = localStorage.getItem('jwt_token');
    const init = {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${jwtToken}`
      },
    }
    const response = await fetch(`${courseUrl}/${id}`, init);
  
    if (response.status === 404) {
      return Promise.reject(`Course: ${id} was not found.`);
    } else if (response.status === 403) {
      return Promise.reject(`Permission Error`);
    }
  }
