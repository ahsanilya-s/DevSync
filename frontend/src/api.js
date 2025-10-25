import axios from 'axios';

const api = axios.create({
    baseURL: '/api', // vite proxy routes /api -> localhost:8080
    withCredentials: true // if you use cookies/sessions later
});

export default api;
