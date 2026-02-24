import type {Comment, Post} from "../../http/types/post.ts";
import styles from "@styles/components/focusedPost.module.css"
import {useEffect, useMemo, useState} from "react";
import CommentService from "../../http/services/commentService.ts";
import {toast} from "react-toastify";
import CommentCard from "./comment.tsx";
import AddComment from "./addComment.tsx";
import type {Account} from "../../http/types/account.ts";

interface FocusedPostProps {
    post: Post;
    user: Account | null;
}

export default function FocusedPost({post, user}: FocusedPostProps) {
    const date = useMemo(() =>
            post.createdAt === post.updatedAt ? new Date(post.createdAt).toLocaleDateString() : `${new Date(post.updatedAt).toLocaleDateString()} (edited)`,
        [post]
    )

    const [comments, setComments] = useState<Comment[]>([]);

    const handleAdd = (comment: Comment) => {
        setComments(prevComments => [comment, ...prevComments]);
    }

    useEffect(() => {
        const fetchComments = async () => {
            const response = await CommentService.getComments(post.id);

            if (!response.success) {
                toast.error("Failed to load comments");
            } else {
                setComments(response.data)
            }
        }

        fetchComments();
    }, [post]);

    const handleDelete = async (commentId: string) => {
        const response = await CommentService.deleteComment(commentId);

        if (!response.success) {
            toast.error("Failed to delete comment");
            console.error("Error:", response.error);
        } else {
            toast.success("Comment deleted successfully");

            setComments(comments.filter((comment) => comment.id !== commentId));
        }
    }

    const handleRefresh = (newComment: Comment) => {
        setComments(comments.map((comment) => comment.id === newComment.id ? newComment : comment))
    }

    return <div className={styles.wrapper}>
        <div>
            <button><span className="material-symbols-outlined">arrow_back </span>
                Back
            </button>

        </div>
        <div className={styles.header}>
            <div>{post.author.username}</div>
            <div>{date}</div>
        </div>

        <p className={styles.content}>{post.content}</p>

        <AddComment onAdd={handleAdd} postId={post.id}/>
        <div className={styles.commentSection}>
            {comments.map((comment) => <CommentCard refreshComment={handleRefresh} onDelete={handleDelete}
                                                    isOwner={user ? user.id === post.author.id : false}
                                                    comment={comment}/>)}
        </div>
    </div>
}