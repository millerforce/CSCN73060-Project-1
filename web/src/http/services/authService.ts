import type {Account, AccountCredentials, AccountUpload} from "../types/account.ts";
import {apiRequest, type ApiResponse} from "../httpRequest.ts";

export default class AuthService {
    private static BASE_URL = "/auth";

    public static async signup(upload: AccountUpload): Promise<ApiResponse<Account>> {
        return await apiRequest<Account>({
            method: "POST",
            url: this.BASE_URL + "/signup",
            data: upload
        });
    }

    public static async login(upload: AccountCredentials): Promise<ApiResponse<void>> {
        return await apiRequest({
            method: "POST",
            url: this.BASE_URL + "/login",
            data: upload
        });
    }

    public static async logout(): Promise<ApiResponse<void>> {
        return await apiRequest({
            method: "DELETE",
            url: this.BASE_URL + "/logout"
        })
    }

    public static async getAuth(): Promise<ApiResponse<Account>> {
        return await apiRequest<Account>({
            method: "GET",
            url: this.BASE_URL
        })
    }
}