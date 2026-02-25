import * as React from "react";
import { createContext, useContext, useEffect, useState } from "react";
import type { Account, AccountCredentials } from "../http/types/account.ts";
import AuthService from "../http/services/authService.ts";
import { toast } from "react-toastify";

export type AuthContextData = {
  user: Account | null;
  login: (user: AccountCredentials) => Promise<boolean>;
  logout: () => Promise<void>;
  checkAuth: () => Promise<boolean>;
  loading: boolean;
};

const AuthContext = createContext<AuthContextData | undefined>(undefined);

const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<Account | null>(null);
  const [loading, setLoading] = useState(true);

  const login = async (upload: AccountCredentials): Promise<boolean> => {
    const response = await AuthService.login(upload);

    if (response.success) {
      const authed = await checkAuth();

      if (!authed) {
        toast.error("Failed to retrieve account after login");
      }

      toast.success(`Logged in successfully. Welcome back!`);
      return true;
    } else {
      toast.error("Incorrect username or password.");
      return false;
    }
  };

  const logout = async () => {
    const response = await AuthService.logout();

    if (response.success) {
      toast.success("Logged out successfully");
    } else {
      toast.error("Failed to logout, Error message logged to console");
      console.error("Logout Error:", response.error);
    }

    setUser(null);
  };

  const checkAuth = async (): Promise<boolean> => {
    const response = await AuthService.getAuth();

    if (!response.success) {
      console.error("Auth Error:", response.error);
      setUser(null);
      return false;
    }

    setUser(response.data);
    return true;
  };

  // Check if the user is authed which will redirect to login page or set user properly for use later
  useEffect(() => {
    const initAuth = async () => {
      await checkAuth();
      setLoading(false);
    };

    initAuth().catch(console.error);
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, logout, checkAuth, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthProvider;

export const useAuth = () => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }

  return context;
};
