import type { Post } from "../types/post.ts";
import '../styles/post.css';

export function PostCard(post: Post) {
    const {
        id, author, content, numberOfLikes, numberOfComments, createdAt, updatedAt
    } = post;
    const date = createdAt === updatedAt ? new Date(createdAt).toLocaleDateString() : `${new Date(updatedAt).toLocaleDateString()} (edited)`;

    return (
        <div className="post-card">
            <div className="container">
                <div className="author">{author.username}</div>
                <div className="date">{date}</div>
            </div>
            <p>{content}</p>
            <div className="container">
                <button><span className="material-symbols-outlined">thumb_up</span> {numberOfLikes}</button>
                <div><span className="material-symbols-outlined">comment</span> {numberOfComments}</div>
            </div>
        </div>
    );
}
