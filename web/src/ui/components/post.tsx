import styles from "@styles/components/post.module.css"
import type {Post} from "../../http/types/post.ts";
import {useMemo} from "react";
import PostService from "../../http/services/postService.ts";
import {toast} from "react-toastify";
import 'material-symbols';

interface PostCardProps {
    post: Post
    refreshPost: (newPost: Post) => void;
    isOwner: boolean;
    onDelete: (postId: number) => void;
}

export function PostCard({post, refreshPost, isOwner, onDelete}: PostCardProps) {
    const date = useMemo(() =>
            post.createdAt === post.updatedAt ? new Date(post.createdAt).toLocaleDateString() : `${new Date(post.updatedAt).toLocaleDateString()} (edited)`,
        [post]
    )

    const handleLike = async () => {
        const response = await PostService.likePost(post.id);

        if (!response.success) {
            toast.error("Failed to like post");
            console.error("Error", response.error);
        } else {
            refreshPost(response.data);
        }
    }

    return (
        <div className={styles.postCard}>
            <div className={styles.container}>
                <p hidden>{post.id}</p>
                <div className={styles.author}>{post.author.username}</div>
                <div className="date">{date}</div>
            </div>
            <p>{post.content}</p>
            <div className={styles.buttonContainer}>
                <div className={styles.buttonGroup}>
                    <button disabled={post.likedByCurrentUser} onClick={handleLike}><span
                        className="material-symbols-outlined">thumb_up</span> {post.numberOfLikes}</button>
                    <button><span className="material-symbols-outlined">comment</span> {post.numberOfComments}</button>
                </div>
                {isOwner &&
                    <button onClick={() => onDelete(post.id)}><span
                        className="material-symbols-outlined">delete</span>
                    </button>}
            </div>
        </div>
    );
}
