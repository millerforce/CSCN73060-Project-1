import type {Account, AccountUpload} from "./types/account.ts";
import {apiRequest, type ApiResponse} from "./httpRequest.ts";

export default class AuthApi {
    private static BASE_URL = "/auth";

    public static async signup(upload: AccountUpload): Promise<ApiResponse<Account>> {
        return await apiRequest<Account>({
            method: "POST",
            url: this.BASE_URL + "/signup",
            data: upload
        })
    }

}