import { PostCard } from "./post.tsx";
import type { Post } from "../../http/types/post.ts";
import styles from "@styles/components/postCollection.module.css";
import type { Account } from "../../http/types/account.ts";
import { useEffect, useRef } from "react";

interface PostCollectionProps {
  posts: Post[];
  user: Account | null;
  onDelete: (postId: string) => void;
  refreshPost: (newPost: Post) => void;
  onLoadMore: () => void;
  hasMore: boolean;
}

export default function PostCollection({
  posts,
  user,
  onDelete,
  refreshPost,
  onLoadMore,
  hasMore,
}: Readonly<PostCollectionProps>) {
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const container = containerRef.current;

    if (!container) return;

    const handleScroll = () => {
      if (!hasMore) return; // stop if no more posts
      if (
        container.scrollTop + container.clientHeight >=
        container.scrollHeight - 200
      ) {
        onLoadMore();
      }
    };

    container.addEventListener("scroll", handleScroll);
    return () => container.removeEventListener("scroll", handleScroll);
  }, [onLoadMore]);

  return (
    <div className={styles.wrapper}>
      <div className={styles.header}>The Pond</div>
      <div className={styles.posts} ref={containerRef}>
        {posts.length === 0 ? (
          <div className={styles.message}>
            No Leaps yet - be the first to enter the pond!
          </div>
        ) : (
          posts.map((post) => (
            <PostCard
              key={post.id}
              post={post}
              isOwner={user ? post.author.id === user.id : false}
              onDelete={onDelete}
              refreshPost={refreshPost}
            />
          ))
        )}
        {!hasMore && posts.length > 0 && (
          <div className={styles.message}>
            You’ve reached the end of the pond!
          </div>
        )}
      </div>
    </div>
  );
}
