import {createContext, useContext, useState} from "react";
import * as React from "react";
import type {AccountSession} from "../api/account.ts";

type AuthContextData = {
    user: AccountSession | null;
    loading: boolean;
    login: (user: AccountSession) => void;
    logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextData | undefined>(undefined);

const AuthProvider = ({children}: {children: React.ReactNode}) => {
    const [user, setUser] = useState<AccountSession | null>(null);
    const [loading, setLoading] = useState(true);

    const login = (user: AccountSession) => {
        setLoading(false);
        setUser(user);
    }

    const logout = async () => {

    }

    return (<AuthContext.Provider value={{user, login, logout, loading}}>{children}</AuthContext.Provider>);
};

export default AuthProvider;

export const useAuth = () => {
    return useContext(AuthContext);
}