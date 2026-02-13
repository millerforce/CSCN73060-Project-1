import "@styles/app.css"
import {createBrowserRouter} from "react-router";
import HomePage from "./ui/pages/homePage";
import {RouterProvider} from "react-router/dom";
import LoginPage from "./ui/pages/loginPage";
import AuthProvider from "./auth/authProvider";
import {ToastContainer} from "react-toastify/unstyled";
import Layout from "./ui/pages/layout.tsx";

const router = createBrowserRouter([
    {
        path: "/", Component: Layout,
        children: [
            {index: true, Component: HomePage},
            {path: "login", Component: LoginPage}
        ]
    },
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
