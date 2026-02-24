import styles from "@styles/components/addComment.module.css"
import type {Comment, CommentUpload} from "../../http/types/post.ts";
import {useForm} from "react-hook-form";
import CommentService from "../../http/services/commentService.ts";
import {toast} from "react-toastify";

interface AddCommentProps {
    onAdd: (comment: Comment) => void;
    postId: string;
}

export default function AddComment({onAdd, postId}: AddCommentProps) {
    const {
        register,
        handleSubmit,
        reset,
        formState: {errors, isSubmitting}
    } = useForm<CommentUpload>();

    const onSubmit = async (upload: CommentUpload) => {
        const response = await CommentService.createComment(postId, upload);

        if (!response.success) {
            toast.error("Failed to add comment");
        } else {
            toast.success("Croak added");

            reset({content: ""});

            onAdd(response.data);
        }
    }

    return <div className={styles.wrapper}>
        <form className={styles.form} onSubmit={handleSubmit(onSubmit)}>
            <label>Add a Croak</label>
            <textarea {...register("content", {required: "You must say something to croak"})}/>
            {errors.content && <div>{errors.content.message}</div>}
            <button type="submit" disabled={isSubmitting}>{isSubmitting ? "Croaking..." : "Croak"}</button>
        </form>
    </div>
}