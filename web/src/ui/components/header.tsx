import styles from "@styles/components/header.module.css"
import {useAuth} from "../../auth/authProvider.tsx";

export default function Header() {

    const authContext = useAuth();

    // For now return nothing if no auth context
    if (!authContext) {
        return null;
    }

    const {user, logout} = authContext;

    return <div className={styles.wrapper}>
        <div className={styles.username}>{user ? user.username : "Unknown Username"}</div>
        <button className={styles.logout} onClick={logout}>Logout</button>
    </div>
}