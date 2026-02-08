import "@styles/App.css"
import {createBrowserRouter} from "react-router";
import HomePage from "./ui/pages/homePage.tsx";
import {RouterProvider} from "react-router/dom";
import LoginPage from "./ui/pages/loginPage.tsx";
import AuthProvider from "./auth/authProvider.tsx";
import {ToastContainer} from "react-toastify/unstyled";

const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/login", element: <LoginPage/>}
])

const App = () => {
    return (
        <AuthProvider>
            <RouterProvider router={router}/>
            <ToastContainer position="top-left" autoClose={5000} pauseOnHover={true}/>
        </AuthProvider>
    )
}

export default App
