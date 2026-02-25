import styles from "@styles/pages/layout.module.css"
import {Outlet, useLocation} from "react-router/internal/react-server-client";
import Header from "../components/header.tsx";

export default function Layout() {
    const location = useLocation();

    const hideHeaderPaths = ["/login", "/signup"];

    const shouldHideHeader = hideHeaderPaths.some((path) => location.pathname.includes(path));

    return <div className={styles.wrapper}>
        {!shouldHideHeader && <Header/>}
        <div className={styles.content}>
            <Outlet/>
        </div>
    </div>
}