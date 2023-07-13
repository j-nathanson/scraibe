// const notesurl = 'http://localhost:8080/notes';
// const coursesurl = 'http://localhost:8080/courses';
// const usersurl = 'http://localhost:8080/users';
// const openaiapiurl = 'http://localhost:8080/generate-completion';


// //get notes from db
// export async function getNotes() {
//     const jwtToken = localStorage.getItem('jwt_token');
  
//     const init = {
//       method: 'GET',
//       headers: {
//         'Accept': 'application/json',
//         'Authorization': `Bearer ${jwtToken}`
//       }
//     };
//     const response = await fetch(notesurl, init);

//     if (response.status >= 200 && response.status < 300) {
//         const data = await response.json();
//         return data;
//       } else {
//         return Promise.reject('Error');
//     }
// }

// //courses
// export async function getCourses() {
//     const jwtToken = localStorage.getItem('jwt_token');
  
//     const init = {
//       method: 'GET',
//       headers: {
//         'Accept': 'application/json',
//         'Authorization': `Bearer ${jwtToken}`
//       }
//     };
//     const response = await fetch(coursesurl, init);

//     if (response.status === 200 && response.status < 300) {
//         const data = await response.json();
//         return data;
//       } else {
//         return Promise.reject('Error');
//     }
// }

// //users
// export async function getUsers() {
//     const jwtToken = localStorage.getItem('jwt_token');
  
//     const init = {
//       method: 'GET',
//       headers: {
//         'Accept': 'application/json',
//         'Authorization': `Bearer ${jwtToken}`
//       }
//     };
//     const response = await fetch(usersurl, init);

//     if (response.status === 200 && response.status < 300) {
//         const data = await response.json();
//         return data;
//       } else {
//         return Promise.reject('Error');
//     }
// }

// //make rq to openaiapi controller
// export async function makeRequestToOpenAIApi(note) {
//     const jwtToken = localStorage.getItem('jwt_token');
  
//     const init = {
//       method: 'POST',
//       headers: {
//         'Accept': 'application/json',
//         'Authorization': `Bearer ${jwtToken}`
//       },
//       body: JSON.stringify(note)
//     };
//     const response = await fetch(openaiapiurl, init);

//     if (response.status >= 200 && response.status < 300) {
//         const data = await response.json();
//         return data;
//       } else {
//         return Promise.reject('Error');
//     }
// }

