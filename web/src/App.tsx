import "@styles/App.css"
import {createBrowserRouter} from "react-router";
import HomePage from "./ui/pages/homePage.tsx";
import {RouterProvider} from "react-router/dom";
import LoginPage from "./ui/pages/loginPage.tsx";

const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/login", element: <LoginPage/>}
])

function App() {
  return (
    <>
        <RouterProvider router={router}/>
    </>
  )
}

export default App
