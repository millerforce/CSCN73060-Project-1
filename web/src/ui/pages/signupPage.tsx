import styles from "@styles/pages/signupPage.module.css";
import type {AccountUpload} from "../../http/types/account.ts";
import {useForm} from "react-hook-form";
import AuthService from "../../http/services/authService.ts";
import {toast} from "react-toastify";
import {useNavigate} from "react-router";

export default function SignupPage() {
    const {
        register,
        handleSubmit,
        formState: {errors, isSubmitting},
    } = useForm<AccountUpload>();

    const navigate = useNavigate();

    const onSubmit = async (upload: AccountUpload) => {
        const response = await AuthService.signup(upload);

        if (!response.success) {
            if (response.error.status === 409) {
                toast.warning("The provided username is already taken. Please choose another one");
            } else {
                toast.error("Invalid username or password");
                console.error("Error:", response.error)
            }
        } else {
            toast.success("Account created successfully, welcome to Froggy!");
            navigate("/");
        }
    }

    return <div className={styles.wrapper}>
        <div className={styles.title}>Create Your Froggy Account!</div>
        <form className={styles.form} onSubmit={handleSubmit(onSubmit)}>
            <div className={styles.item}>
                <label>Username</label>
                <input className={styles.input} type="username" {...register("username", {
                    required: "Username is required",
                })}/>
                {errors.username && <div className={styles.error}>{errors.username.message}</div>}
            </div>

            <div className={styles.item}>
                <label>Password</label>
                <input className={styles.input} type="password" {...register("password", {
                        required: "Password is required",
                        minLength: {
                            value: 6,
                            message: "Minimum 6 characters"
                        }
                    }
                )}/>
                {errors.password && <div className={styles.error}>{errors.password.message}</div>}
            </div>

            <button className={styles.button} type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Creating your account..." : "Signup"}
            </button>
        </form>
    </div>
}