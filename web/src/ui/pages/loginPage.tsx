import styles from "@styles/pages/loginPage.module.css";
import { useForm } from "react-hook-form";
import type { AccountCredentials } from "../../http/types/account.ts";
import { useAuth } from "../../auth/authProvider.tsx";
import { NavLink, useNavigate } from "react-router";

export default function LoginPage() {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<AccountCredentials>();

  const auth = useAuth();
  const navigate = useNavigate();

  const onSubmit = async (upload: AccountCredentials) => {
    const success = await auth?.login(upload);

    if (success) navigate("/");
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.content}>
        <div className={styles.title}>Welcome to Froggy</div>
        <form className={styles.form} onSubmit={handleSubmit(onSubmit)}>
          <div className={styles.item}>
            <label>Username</label>
            <input
              className={styles.input}
              type="username"
              {...register("username", {
                required: "Username is required",
              })}
            />
            {errors.username && (
              <div className={styles.error}>{errors.username.message}</div>
            )}
          </div>

          <div className={styles.item}>
            <label>Password</label>
            <input
              className={styles.input}
              type="password"
              {...register("password", {
                required: "Password is required",
                minLength: {
                  value: 6,
                  message: "Minimum 6 characters",
                },
              })}
            />
            {errors.password && (
              <div className={styles.error}>{errors.password.message}</div>
            )}
          </div>

          <button
            className={styles.button}
            type="submit"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Logging in..." : "Login"}
          </button>

          <div className={styles.signup}>
            <div>Don't have an account yet?</div>
            <NavLink to={"/signup"}>Click Here</NavLink>
          </div>
        </form>
      </div>
    </div>
  );
}
