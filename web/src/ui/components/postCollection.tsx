import {PostCard} from "./post.tsx";
import type {Post} from "../../http/types/post.ts";
import styles from "@styles/components/postCollection.module.css"
import type {Account} from "../../http/types/account.ts";

interface PostCollectionProps {
    posts: Post[];
    user: Account | null;
    onDelete: (postId: number) => void; // Pass delete action to parent
    refreshPost: (newPost: Post) => void;
}

export default function PostCollection({posts, user, onDelete, refreshPost}: PostCollectionProps) {

    return <div className={styles.wrapper}>
        <div className={styles.header}>The Pond</div>
        <div className={styles.posts}>
            {posts.map((post) => <PostCard key={post.id} post={post} isOwner={user ? post.author.id === user.id : false}
                                           onDelete={onDelete} refreshPost={refreshPost}/>)}
        </div>

    </div>
}