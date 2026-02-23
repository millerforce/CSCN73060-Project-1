import {useNavigate} from "react-router";
import {useEffect} from "react";
import {setUnauthorizedHandler} from "../http/axiosInstance.ts";

export const AuthSetup = () => {
    const navigate = useNavigate();

    useEffect(() => {
        setUnauthorizedHandler(() => {
            navigate('/login', {replace: true})
        })
    }, [navigate])

    return null;
}