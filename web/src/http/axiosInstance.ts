import axios, {type AxiosError} from 'axios';

const axiosInstance = axios.create({
    baseURL: "/api",
    withCredentials: true
})

// Automatically intercept unauthorized responses
axiosInstance.interceptors.response.use(undefined, async (error: AxiosError) => {
    if (error.response?.status === 401) {
        window.location.href = '/login';
    }

    throw error;
})

export default axiosInstance;