import styles from "@styles/components/header.module.css";
import { useAuth } from "../../auth/authProvider.tsx";
import ImageWrapper from "./imageWrapper.tsx";
import { NavLink, useNavigate } from "react-router";

export default function Header() {
  const authContext = useAuth();

  const { user, logout } = authContext;

  const navigate = useNavigate();

  return (
    <div className={styles.wrapper}>
      <NavLink to={"/posts"}>
        <ImageWrapper
          src={"/images/froggyBanner.png"}
          alt={"Froggy Banner"}
          height={"40px"}
          width={"150px"}
        />
      </NavLink>

      <div className={styles.userBox}>
        <div className={styles.username}>
          {user ? user.username : "Unknown Username"}
        </div>
        <button
          className={styles.logout}
          onClick={() => {
            logout().catch(console.error);
            navigate("/login");
          }}
        >
          Logout
        </button>
      </div>
    </div>
  );
}
