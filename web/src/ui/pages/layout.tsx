import styles from "@styles/pages/layout.module.css"
import {Outlet} from "react-router/internal/react-server-client";
import Header from "../components/header.tsx";

export default function Layout() {

    return <div className={styles.wrapper}>
        <Header/>
        <div className={styles.content}>
            <Outlet/>
        </div>
    </div>
}