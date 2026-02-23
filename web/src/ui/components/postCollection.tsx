import {PostCard} from "./post.tsx";
import type {Post} from "../../http/types/post.ts";
import styles from "@styles/components/postCollection.module.css"

interface PostCollectionProps {
    posts: Post[];
}

export default function PostCollection({posts}: PostCollectionProps) {
    return <div className={styles.wrapper}>
        <div>The Pond</div>
        {posts.map((post) => <PostCard key={post.id} post={post}/>)}
    </div>
}