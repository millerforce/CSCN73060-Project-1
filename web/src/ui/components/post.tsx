import styles from "@styles/components/post.module.css";
import type { Post } from "../../http/types/post.ts";
import { useMemo } from "react";
import PostService from "../../http/services/postService.ts";
import { toast } from "react-toastify";
import "material-symbols";
import { useNavigate } from "react-router";
import Button from "./buttons/button.tsx";

interface PostCardProps {
  post: Post;
  refreshPost: (newPost: Post) => void;
  isOwner: boolean;
  onDelete: (postId: string) => void;
}

export function PostCard({
  post,
  refreshPost,
  isOwner,
  onDelete,
}: Readonly<PostCardProps>) {
  const date = useMemo(
    () =>
      post.createdAt === post.updatedAt
        ? new Date(post.createdAt).toLocaleDateString()
        : `${new Date(post.updatedAt).toLocaleDateString()} (edited)`,
    [post],
  );

  const navigate = useNavigate();

  const handleLike = async () => {
    const response = await PostService.likePost(post.id);

    if (response.success) {
      refreshPost(response.data);
    } else {
      toast.error("Failed to like post");
      console.error("Error", response.error);
    }
  };

  return (
    <div className={styles.postCard}>
      <button
        onClick={() => navigate(`/posts/${post.id}`)}
        className={styles.clickable}
      >
        <div className={styles.container}>
          <p hidden>{post.id}</p>
          <div className={styles.author}>{post.author.username}</div>
          <div className={styles.date}>{date}</div>
        </div>
        <p>{post.content}</p>
      </button>

      <div className={styles.buttonContainer}>
        <div className={styles.buttonGroup}>
          <Button
            iconString={"thumb_up"}
            onClick={handleLike}
            count={post.numberOfLikes}
            disabled={post.likedByCurrentUser}
          />
          <Button
            clickable={false}
            count={post.numberOfComments}
            iconString={"comment"}
          />
        </div>
        {isOwner && (
          <Button iconString={"delete"} onClick={() => onDelete(post.id)} />
        )}
      </div>
    </div>
  );
}
