import * as React from "react";
import {createContext, useContext, useState} from "react";
import type {Account, AccountCredentials} from "../http/types/account.ts";
import AuthService from "../http/services/authService.ts";
import {toast} from "react-toastify";
import {useNavigate} from "react-router";

type AuthContextData = {
    user: Account | null;
    login: (user: AccountCredentials) => Promise<boolean>;
    logout: () => Promise<void>;
    checkAuth: () => Promise<boolean>;
}

const AuthContext = createContext<AuthContextData | undefined>(undefined);

const AuthProvider = ({children}: { children: React.ReactNode }) => {
    const [user, setUser] = useState<Account | null>(null);

    const navigate = useNavigate();

    const login = async (upload: AccountCredentials): Promise<boolean> => {

        const response = await AuthService.login(upload);

        if (!response.success) {
            toast.error("Incorrect username or password.");
            return false;
        } else {
            setUser(response.data);

            toast.success(`Logged in successfully. Welcome back ${response.data.username}`);
            return true;
        }
    }

    const logout = async () => {
        const response = await AuthService.logout();

        if (!response.success) {
            toast.error("Failed to logout, Error message logged to console");
            console.error("Logout Error:", response.error);
        } else {
            toast.success("Logged out successfully");
            setUser(null);

            navigate("/login");
        }
    }

    const checkAuth = async (): Promise<boolean> => {
        const response = await AuthService.getAuth();

        if (!response.success) {
            console.error("Auth Error:", response.error);
        } else {
            setUser((response.data));
        }

        return response.success;
    }

    // Check if the user is authed which will redirect to login page or set user properly for use later
    await checkAuth();

    return (<AuthContext.Provider value={{user, login, logout, checkAuth}}>{children}</AuthContext.Provider>);
};

export default AuthProvider;

export const useAuth = () => {
    return useContext(AuthContext);
}