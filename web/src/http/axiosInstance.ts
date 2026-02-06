import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: "https://the-hero.dev"
})

export default axiosInstance;