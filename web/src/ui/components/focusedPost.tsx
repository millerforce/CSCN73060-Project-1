import type { Comment, Post } from "../../http/types/post.ts";
import styles from "@styles/components/focusedPost.module.css";
import { useEffect, useMemo, useState } from "react";
import CommentService from "../../http/services/commentService.ts";
import { toast } from "react-toastify";
import CommentCard from "./comment.tsx";
import AddComment from "./addComment.tsx";
import type { Account } from "../../http/types/account.ts";
import { useNavigate } from "react-router";

interface FocusedPostProps {
  post: Post;
  user: Account | null;
}

export default function FocusedPost({
  post,
  user,
}: Readonly<FocusedPostProps>) {
  const navigate = useNavigate();

  const date = useMemo(
    () =>
      post.createdAt === post.updatedAt
        ? new Date(post.createdAt).toLocaleDateString()
        : `${new Date(post.updatedAt).toLocaleDateString()} (edited)`,
    [post],
  );

  const [comments, setComments] = useState<Comment[]>([]);

  const handleAdd = (comment: Comment) => {
    setComments((prevComments) => [comment, ...prevComments]);
  };

  useEffect(() => {
    const fetchComments = async () => {
      const response = await CommentService.getComments(post.id);

      if (response.success) {
        setComments(response.data);
      } else {
        toast.error("Failed to load comments");
      }
    };

    fetchComments().catch(console.error);
  }, [post]);

  const handleDelete = async (commentId: string) => {
    const response = await CommentService.deleteComment(commentId);

    if (response.success) {
      toast.success("Comment deleted successfully");

      setComments(comments.filter((comment) => comment.id !== commentId));
    } else {
      toast.error("Failed to delete comment");
      console.error("Error:", response.error);
    }
  };

  const handleRefresh = (newComment: Comment) => {
    setComments(
      comments.map((comment) =>
        comment.id === newComment.id ? newComment : comment,
      ),
    );
  };

  return (
    <div className={styles.wrapper}>
      <div>
        <button
          className={styles.backButton}
          onClick={() => navigate("/posts")}
        >
          <span className="material-symbols-outlined">arrow_back </span> Back
        </button>
      </div>
      <div className={styles.header}>
        <div>{post.author.username}</div>
        <div>{date}</div>
      </div>

      <p className={styles.content}>{post.content}</p>

      <AddComment onAdd={handleAdd} postId={post.id} />
      <div className={styles.commentSection}>
        {comments.length === 0 ? (
          <div className={styles.emptyMessage}>
            No one has croaked to this leap yet
          </div>
        ) : (
          comments.map((comment) => (
            <CommentCard
              key={comment.id}
              refreshComment={handleRefresh}
              onDelete={handleDelete}
              isOwner={user ? user.id === post.author.id : false}
              comment={comment}
            />
          ))
        )}
      </div>
    </div>
  );
}
