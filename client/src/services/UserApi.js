// const usersUrl = 'http://localhost:8080/users';
const usersUrl = 'http://18.118.122.152:8080/users'
const jwtToken = localStorage.getItem('jwt_token');

const mapAuthoritiesToRoles = (user) => {
  if (user.authorities) {
    let roles = user.authorities.map(auth => auth.authority);
    delete user.authorities;
    user.roles = roles;
  }
  return user;
}
//getusers
export async function getUsers() {
  const jwtToken = localStorage.getItem('jwt_token');

  const init = {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      'Authorization': `Bearer ${jwtToken}`
    }
  };
  const response = await fetch(usersUrl, init);

  if (response.status === 200 || response.status < 300) {
    const data = await response.json();
    return data;
  } else if (response.status >= 400 || response.status < 500) {
    return await response.json();
  } else {
    return Promise.reject("Error")
  }
}

// getByEmail
export async function getByEmail(email) {
  const init = {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      'Authorization': `Bearer ${jwtToken}`
    }
  };

  const response = await fetch(`${usersUrl}/get-email/${email}`, init);
  if (response.status === 200 && response.status < 300) {
    return response.json();
  } else {
    return Promise.reject(`User email: ${email} was not found.`);
  }
}

// getByUsername
export async function getByUsername(username) {
  const init = {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
      'Authorization': `Bearer ${jwtToken}`
    }
  };

  const response = await fetch(`${usersUrl}/get-username/${username}`, init);
  if (response.status === 200 && response.status < 300) {
    return response.json();
  } else {
    return Promise.reject(`Username: ${username} was not found.`);
  }
}

//edit
export async function editUser(user) {
  const jwtToken = localStorage.getItem('jwt_token');

  const init = {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'Authorization': `Bearer ${jwtToken}`
    },
    body: JSON.stringify(mapAuthoritiesToRoles(user))
  };

  const response = await fetch(`${usersUrl}/edit/${user.appUserId}`, init);

  if (response.status === 404) {
    return Promise.reject(`User ID: ${user.appUserId} was not found.`);
  } else if (response.status === 400) {
    const errors = await response.json();
    return Promise.reject(errors);
  } else if (response.status === 409) {
    return Promise.reject('conflict');
  } else if (response.status === 403) {
    return Promise.reject('Error');
  }
}

//deleteById

export async function deleteByUserId(id) {
  const init = {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${jwtToken}`
    },
  }
  const response = await fetch(`${usersUrl}/delete/${id}`, init);

  if (response.status === 404) {
    return Promise.reject(`User ID: ${id} was not found.`);
  } else if (response.status === 403) {
    return Promise.reject('Unauthorized');
  }
}

  // two messages - one for unfound and unauthorized 