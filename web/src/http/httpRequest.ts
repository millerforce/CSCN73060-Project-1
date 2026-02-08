import axiosInstance from "./axiosInstance.ts";
import axios, {type AxiosError, type AxiosResponse} from "axios";

export type RequestMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE" | "OPTIONS";

export type RequestParams = Record<string, string | number | boolean | undefined>;

export type RequestOptions = {
    method: RequestMethod;
    url: string;
    params?: RequestParams;
    data?: any;
}

type ValidationError = {
    message: string;
    field: string;
}

export type ApiError = {
    type: string;
    title: string;
    status: number;
    detail: string;
    instance: string;
    errors?: ValidationError[];
}

export type ApiErrorResponse = {
    success: false;
    error: ApiError;
    headers: Record<string, string>;
}

export type ApiSuccessResponse<T = void> = {
    success: true;
    data: T;
    headers: Record<string, string>;
}

export type ApiResponse<T> = ApiSuccessResponse<T> | ApiErrorResponse;

const extractApiError = (error: unknown): ApiError => {
    // Received a response from the server
    if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError<any>;

        // If the server responded with a structured error
        if (axiosError.response?.data) {
            const data = axiosError.response.data;

            // Confirm general structure of response conforms to expected structure
            if (typeof data === "object" && data.type && data.title && typeof data.status === "number") {
                return data as ApiError;
            }

            // If it did not send a structured error body then build an error message based on response status
            const status = axiosError.response?.status;

            return {
                type: status ? "https://httpstatuses.com/" + status : "unknown",
                title: "Request failed",
                status: status ?? 0,
                detail: typeof data === "string" ? data : "The server returned an invalid error response",
                instance: axiosError.config?.url ?? ""
            }
        }
    }

    // Request received no response from the server
    return {
        type: "unknown",
        title: "Network Error",
        status: 0,
        detail: error instanceof Error ? error.message : "Unknown error occurred",
        instance: ""
    }
}

const parseResponse = <T>(response: AxiosResponse<T>): ApiResponse<T> => {
    return {
        success: true,
        data: response.data as T,
        headers: response.headers as unknown as Record<string, string>
    }
}

export const apiRequest = async  <T>(options: RequestOptions): Promise<ApiResponse<T>> => {
    try {
        const response = await axiosInstance(options);

        return parseResponse<T>(response);
    } catch (err: any) {
        const apiError = extractApiError(err);
        return {
            success: false,
            error: apiError,
            headers: {}
        }
    }
}