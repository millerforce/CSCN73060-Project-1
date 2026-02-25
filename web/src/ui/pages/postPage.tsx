import styles from "@styles/pages/postPage.module.css";
import { useEffect, useMemo, useState } from "react";
import type { Post } from "../../http/types/post.ts";
import PostService from "../../http/services/postService.ts";
import { toast } from "react-toastify";
import PostCollection from "../components/postCollection.tsx";
import AddPost from "../components/addPost.tsx";
import { useAuth } from "../../auth/authProvider.tsx";
import { useParams } from "react-router";
import FocusedPost from "../components/focusedPost.tsx";

export default function PostPage() {
  const authContext = useAuth();

  const [posts, setPosts] = useState<Post[]>([]);

  const { postId } = useParams();

  // If the user browsed to a specific post

  const focusedPost = useMemo(() => {
    if (!postId) return undefined;
    return posts.find((post) => post.id === postId);
  }, [postId, posts]);

  const isFocused = Boolean(postId);

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const result = await PostService.getPosts({
          maxResults: 10,
          offset: 0,
        });

        if (!result.success) {
          toast.error("Failed to fetch posts");
          return;
        }

        setPosts(result.data);
      } catch (error) {
        toast.error("Unexpected error fetching posts");
        console.error(error);
      }
    };

    fetchPosts().catch(console.error);
  }, []);

  const handlePostDelete = async (postId: string) => {
    const response = await PostService.deletePost(postId);

    if (!response.success) {
      toast.error("Failed to delete post");
      console.error("Error:", response.error);
    } else {
      toast.success("Post deleted successfully");

      setPosts((prev) => prev.filter((post) => post.id !== postId));
    }
  };

  const handlePostReplacement = (newPost: Post) => {
    setPosts((prev) =>
      prev.map((post) => (post.id === newPost.id ? newPost : post)),
    );
  };

  const handlePostAdd = (post: Post) => {
    setPosts((prevPosts) => [post, ...prevPosts]);
  };

  return (
    <div className={styles.wrapper}>
      <div className={`${styles.content} ${isFocused ? styles.hidden : ""}`}>
        <AddPost onAdd={handlePostAdd} />
        <PostCollection
          posts={posts}
          user={authContext.user}
          onDelete={handlePostDelete}
          refreshPost={handlePostReplacement}
        />
      </div>

      {focusedPost && (
        <FocusedPost user={authContext.user} post={focusedPost} />
      )}
    </div>
  );
}
