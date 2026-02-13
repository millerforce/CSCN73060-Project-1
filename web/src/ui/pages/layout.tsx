import styles from "@styles/pages/layout.module.css"
import {Outlet} from "react-router/internal/react-server-client";

export default function Layout() {

    return <div className={styles.wrapper}><Outlet/></div>
}