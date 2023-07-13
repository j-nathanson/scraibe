// const NOTES_URL = 'http://localhost:8080/notes';
// const OPENAI_API_URL = 'http://localhost:8080/generate-completion';

const NOTES_URL = 'http://18.118.122.152:8080/notes'
const OPENAI_API_URL = 'http://18.118.122.152:8080/generate-completion'

//get all notes from db
export async function getAllNotes() {
    const jwtToken = localStorage.getItem('jwt_token');

    const init = {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        }
    };
    const response = await fetch(NOTES_URL, init);

    if (response.status >= 200 && response.status < 300) {
        const data = await response.json();
        return data;
    } else if (response.status === 403) {
        return Promise.reject("Unauthorized");
    } else {
        return Promise.reject('Error');
    }
}

export async function getByNoteId(id) {
    const jwtToken = localStorage.getItem('jwt_token');

    const init = {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        }
    };
    const response = await fetch(`${NOTES_URL}/${id}`, init);

    if (response.status >= 200 && response.status < 300) {
        const data = await response.json();
        return data;
    } else if (response.status === 403) {
        return Promise.reject("Unauthorized");
    } else if (response.status === 404) {
        return Promise.reject(`Note at id: ${id} was not found`);
    } else {
        return Promise.reject('Error');
    }
}

export async function getNotesByCourseId(id) {
    const jwtToken = localStorage.getItem('jwt_token');
    const init = {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        }
    };
    const response = await fetch(`${NOTES_URL}/from-course/${id}`, init);

    if (response.status >= 200 && response.status < 300) {
        const data = await response.json();
        return data;
    } else if (response.status === 403) {
        return Promise.reject("Unauthorized");
    } else {
        return Promise.reject('Error');
    }
}

export async function addNote(note) {
    const jwtToken = localStorage.getItem('jwt_token');
    const init = {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        },
        body: JSON.stringify(note)
    };
    const response = await fetch(NOTES_URL, init);
    if (response.status === 201) {
        const data = await response.json();
        return data;
    } else if (response.status === 403) {
        return Promise.reject("Unauthorized");
    } else {
        const errors = await response.json();
        return Promise.reject(errors);
    }
}

export async function editNote(note) {
    const jwtToken = localStorage.getItem('jwt_token');
    const init = {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        },
        body: JSON.stringify(note)
    };

    const response = await fetch(`${NOTES_URL}/${note.noteId}`, init);
    if(response.status === 204){
        return;
    }
    else if (response.status === 409) {
        return Promise.reject("id in url and note do not match");
    } else if (response.status === 404) {
        return Promise.reject(`Note was not found at id ${note.noteId}`);
    } else if (response.status === 403) {
        return Promise.reject("Unauthorized");
    } else {
        const errors = await response.json();
        return Promise.reject(errors);
    }
}

export async function deleteNote(note) {
    debugger;
    const jwtToken = localStorage.getItem('jwt_token');
    const init = {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    };

    const response = await fetch(`${NOTES_URL}/${note.noteId}`, init);
    if (response.status === 404) {
        return Promise.reject(`Note was not found at id ${note.noteId}`);
    } else if (response.status === 403) {
        return Promise.reject("Unauthorized");
    }
}

export async function generateNote(note) {
    const jwtToken = localStorage.getItem('jwt_token');
    const init = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            'Authorization': `Bearer ${jwtToken}`
        },
        body: JSON.stringify(note)
    };

    const response = await fetch(OPENAI_API_URL, init);

    if (response.status >= 200 && response.status < 300) {
        const data = await response.json();
        return data;
    } else {
        return Promise.reject("Error");
    }


}