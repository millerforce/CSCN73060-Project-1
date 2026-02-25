import styles from "@styles/components/comment.module.css";
import type { Comment } from "../../http/types/post.ts";
import { useMemo } from "react";
import CommentService from "../../http/services/commentService.ts";
import { toast } from "react-toastify";
import Button from "./buttons/button.tsx";

interface CommentProps {
  comment: Comment;
  isOwner: boolean;
  refreshComment: (newComment: Comment) => void;
  onDelete: (commentId: string) => void;
}

export default function CommentCard({
  comment,
  isOwner,
  refreshComment,
  onDelete,
}: Readonly<CommentProps>) {
  const date = useMemo(
    () =>
      comment.createdAt === comment.updatedAt
        ? new Date(comment.createdAt).toLocaleDateString()
        : `${new Date(comment.updatedAt).toLocaleDateString()} (edited)`,
    [comment],
  );

  const handleLike = async () => {
    const response = await CommentService.likeComment(comment.id);

    if (response.success) {
      refreshComment(response.data);
    } else {
      toast.error("Failed to like comment");
      console.error("Error:", response.error);
    }
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.content}>
        <div className={styles.header}>
          <div>{comment.account.username}</div>
          <div>{date}</div>
        </div>

        <p>{comment.content}</p>
      </div>

      <div className={styles.buttonContainer}>
        <Button
          count={comment.numberOfLikes}
          disabled={comment.likedByCurrentUser}
          onClick={handleLike}
          iconString="thumb_up"
        />
        {isOwner && (
          <Button iconString="delete" onClick={() => onDelete(comment.id)} />
        )}
      </div>
    </div>
  );
}
