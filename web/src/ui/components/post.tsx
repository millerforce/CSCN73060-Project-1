import styles from "@styles/components/post.module.css"
import type {Post} from "../../http/types/post.ts";
import {useMemo} from "react";

interface PostCardProps {
    post: Post
}

export function PostCard({post}: PostCardProps) {
    const date = useMemo(() =>
            post.createdAt === post.updatedAt ? new Date(post.createdAt).toLocaleDateString() : `${new Date(post.updatedAt).toLocaleDateString()} (edited)`,
        [post]
    )

    // const handleLike = () => {
    //
    // }

    return (
        <div className={styles.postCard}>
            <div className={styles.container}>
                <p hidden>{post.id}</p>
                <div className={styles.author}>{post.author.username}</div>
                <div className="date">{date}</div>
            </div>
            <p>{post.content}</p>
            <div className={styles.buttonContainer}>
                <button><span className="material-symbols-outlined">thumb_up</span> {post.numberOfLikes}</button>
                <button><span className="material-symbols-outlined">comment</span> {post.numberOfComments}</button>
            </div>
        </div>
    );
}
