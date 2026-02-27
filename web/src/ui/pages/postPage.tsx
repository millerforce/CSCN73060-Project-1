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

  const FETCHSIZE = 10;

  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);

  const { postId } = useParams();

  // If the user browsed to a specific post
  const focusedPost = useMemo(() => {
    if (!postId) return undefined;
    return posts.find((post) => post.id === postId);
  }, [postId, posts]);

  const isFocused = Boolean(postId);

  const fetchPosts = async () => {
    if (loading || !hasMore) return;

    setLoading(true);

    try {
      const result = await PostService.getPosts({
        maxResults: FETCHSIZE,
        offset: posts.length, // ← derive offset here
      });

      if (!result.success) {
        toast.error("Failed to fetch posts");
        return;
      }

      if (result.data.length < FETCHSIZE) {
        setHasMore(false);
      }

      setPosts((prev) => {
        const existing = new Set(prev.map((p) => p.id));
        const filtered = result.data.filter((p) => !existing.has(p.id));
        return [...prev, ...filtered];
      });
    } catch (error) {
      toast.error("Unexpected error fetching posts");
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setPosts([]);
    fetchPosts().catch(console.error);
  }, []);

  const handlePostDelete = async (postId: string) => {
    const response = await PostService.deletePost(postId);

    if (response.success) {
      toast.success("Leap deleted successfully");

      setPosts((prev) => prev.filter((post) => post.id !== postId));
    } else {
      toast.error("Failed to delete Leap");
      console.error("Error:", response.error);
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
          hasMore={hasMore}
          onLoadMore={fetchPosts}
          posts={posts}
          user={authContext.user}
          onDelete={handlePostDelete}
          refreshPost={handlePostReplacement}
        />
      </div>

      {focusedPost && (
        <FocusedPost
          onDelete={handlePostDelete}
          onRefresh={handlePostReplacement}
          user={authContext.user}
          post={focusedPost}
        />
      )}
    </div>
  );
}
