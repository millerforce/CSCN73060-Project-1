import styles from "@styles/components/comment.module.css";
import type {Comment} from "../../http/types/post.ts";
import {useMemo} from "react";
import CommentService from "../../http/services/commentService.ts";
import {toast} from "react-toastify";

interface CommentProps {
    comment: Comment;
    isOwner: boolean;
    refreshComment: (newComment: Comment) => void;
    onDelete: (commentId: string) => void;
}

export default function CommentCard({comment, isOwner, refreshComment, onDelete}: CommentProps) {

    const date = useMemo(() =>
            comment.createdAt === comment.updatedAt ? new Date(comment.createdAt).toLocaleDateString() : `${new Date(comment.updatedAt).toLocaleDateString()} (edited)`,
        [comment]
    )

    const handleLike = async () => {
        const response = await CommentService.likeComment(comment.id);

        if (!response.success) {
            toast.error("Failed to like comment");
            console.error("Error:", response.error);
        } else {
            refreshComment(response.data);
        }
    }

    return <div className={styles.wrapper}>
        <div className={styles.header}>
            <div>{comment.account.username}</div>
            <div>{date}</div>
        </div>

        <p>{comment.content}</p>

        <div className={styles.buttonContainer}>
            <button disabled={comment.likedByCurrentUser} onClick={handleLike}><span
                className="material-symbols-outlined">thumb_up</span> {comment.numberOfLikes}</button>
            {isOwner && <button onClick={() => onDelete(comment.id)}>
                <span className="material-symbols-outlined">delete</span>
            </button>}
        </div>

    </div>
}