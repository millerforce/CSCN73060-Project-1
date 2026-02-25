import { createBrowserRouter, Navigate } from "react-router";
import PostPage from "./ui/pages/postPage.tsx";
import { RouterProvider } from "react-router/dom";
import LoginPage from "./ui/pages/loginPage";
import AuthProvider from "./auth/authProvider";
import { ToastContainer } from "react-toastify";
import Layout from "./ui/pages/layout.tsx";
import SignupPage from "./ui/pages/signupPage.tsx";
import { AuthSetup } from "./auth/authSetup.tsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: (
      <>
        <AuthSetup />
        <Layout />
      </>
    ),
    children: [
      { index: true, element: <Navigate to="posts" replace /> },
      { path: "posts/:postId?", Component: PostPage },
      { path: "login", Component: LoginPage },
      { path: "signup", Component: SignupPage },
    ],
  },
]);

const App = () => {
  return (
    <AuthProvider>
      <RouterProvider router={router} />
      <ToastContainer
        position="top-left"
        autoClose={5000}
        pauseOnHover={true}
      />
    </AuthProvider>
  );
};

export default App;
