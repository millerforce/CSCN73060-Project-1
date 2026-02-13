import axios, {type AxiosError} from 'axios';

const axiosInstance = axios.create({
    baseURL: "http://localhost:6204"
})

// Automatically intercept unauthorized responses
axiosInstance.interceptors.response.use(undefined, async (error: AxiosError) => {
    if (error.response?.status === 401) {
        window.location.href = '/login';
    }
})

export default axiosInstance;