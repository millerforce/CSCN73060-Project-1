import type { Comment, Post } from "../../http/types/post.ts";
import styles from "@styles/components/focusedPost.module.css";
import { useEffect, useMemo, useState } from "react";
import CommentService from "../../http/services/commentService.ts";
import { toast } from "react-toastify";
import CommentCard from "./comment.tsx";
import AddComment from "./addComment.tsx";
import type { Account } from "../../http/types/account.ts";
import { useNavigate } from "react-router";
import Button from "./buttons/button.tsx";
import PostService from "../../http/services/postService.ts";

interface FocusedPostProps {
  post: Post;
  user: Account | null;
  onRefresh: (newPost: Post) => void;
  onDelete: (postId: string) => void;
}

export default function FocusedPost({
  post,
  user,
  onRefresh,
  onDelete,
}: Readonly<FocusedPostProps>) {
  const navigate = useNavigate();

  const [isEditing, setIsEditing] = useState(false);
  const [editedContent, setEditedContent] = useState(post.content);
  const [isSaving, setIsSaving] = useState(false);
  const [trendingScore, setTrendingScore] = useState<number | null>(null);

  const date = useMemo(
    () =>
      post.createdAt === post.updatedAt
        ? new Date(post.createdAt).toLocaleDateString()
        : `${new Date(post.updatedAt).toLocaleDateString()} (edited)`,
    [post],
  );

  const isOwner = user ? post.author.id === user.id : false;

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

    const fetchScore = async () => {
      const response = await PostService.getPostTrendingScore(post.id);

      if (response.success) {
        setTrendingScore(response.data.trendingScore);
      } else {
        toast.error("Failed to fetch trending score");
        console.error(response.error);
      }
    };

    fetchComments().catch(console.error);
    fetchScore().catch(console.error);
  }, [post]);

  const handleCommentDelete = async (commentId: string) => {
    const response = await CommentService.deleteComment(commentId);

    if (response.success) {
      toast.success("Comment deleted successfully");

      setComments(comments.filter((comment) => comment.id !== commentId));
    } else {
      toast.error("Failed to delete comment");
      console.error("Error:", response.error);
    }
  };

  const handleCommentRefresh = (newComment: Comment) => {
    setComments(
      comments.map((comment) =>
        comment.id === newComment.id ? newComment : comment,
      ),
    );
  };

  const handlePostLike = async () => {
    const response = await PostService.likePost(post.id);

    if (response.success) {
      onRefresh(response.data);
    } else {
      toast.error("Failed to like post");
      console.error("Error", response.error);
    }
  };

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSave = async () => {
    setIsSaving(true);

    try {
      const response = await PostService.editPost(post.id, {
        content: editedContent,
      });

      if (response.success) {
        onRefresh(response.data);
        setIsEditing(false);
        toast.success("Leap updated successfully!");
      } else {
        toast.error("Failed to update Leap");
        console.error("Error:", response.error);
      }
    } catch (err) {
      toast.error("An error occurred while saving");
      console.error(err);
    } finally {
      setIsSaving(false);
    }
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

      <div className={styles.content}>
        {isEditing ? (
          <div className={styles.editContainer}>
            <textarea
              value={editedContent}
              onChange={(e) => setEditedContent(e.target.value)}
              className={styles.editTextArea}
            />
            <div className={styles.buttonContainer}>
              <Button
                text={isSaving ? "Saving..." : "Save"}
                iconString="check"
                onClick={handleSave}
              />
              <Button
                text={"Close"}
                iconString="close"
                onClick={() => setIsEditing(false)}
              />
            </div>
          </div>
        ) : (
          post.content
        )}
      </div>
      <div className={styles.buttonContainer}>
        <div className={styles.buttonContainer}>
          <Button
            disabled={post.likedByCurrentUser}
            onClick={handlePostLike}
            iconString={"thumb_up"}
            count={post.numberOfLikes}
          />
          <Button iconString={"comment"} count={post.numberOfComments} />
          <Button
            clickable={false}
            iconString={"trending_up"}
            count={trendingScore ?? undefined}
            text={"Pond Presence"}
          />
        </div>

        {isOwner && (
          <div className={styles.buttonContainer}>
            <Button iconString={"edit"} onClick={handleEdit} />
            <Button iconString={"delete"} onClick={() => onDelete(post.id)} />
          </div>
        )}
      </div>

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
              refreshComment={handleCommentRefresh}
              onDelete={handleCommentDelete}
              isOwner={user ? user.id === post.author.id : false}
              comment={comment}
            />
          ))
        )}
      </div>
    </div>
  );
}
