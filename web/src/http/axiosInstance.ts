import axios, {type AxiosError} from 'axios';

const axiosInstance = axios.create({
    baseURL: "/api",
    withCredentials: true
})

let onUnauthorized: (() => void) | null = null;

export const setUnauthorizedHandler = (handler: () => void) => {
    onUnauthorized = handler;
}

// Automatically intercept unauthorized responses
axiosInstance.interceptors.response.use(response => response, (error: AxiosError) => {
    if (error.response?.status === 401 && onUnauthorized) {
        onUnauthorized();
    }

    return Promise.reject(error);
})

export default axiosInstance;