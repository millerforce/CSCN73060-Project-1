import styles from "@styles/pages/homePage.module.css"
import {useEffect, useState} from "react";
import type {Post} from "../../http/types/post.ts";
import PostService from "../../http/services/postService.ts";
import {toast} from "react-toastify";
import PostCollection from "../components/postCollection.tsx";
import AddPost from "../components/addPost.tsx";

export default function HomePage() {
    const [posts, setPosts] = useState<Post[]>([]);

    useEffect(() => {
        const fetchPosts = async () => {
            const result = await PostService.getPosts({maxResults: 10, offset: 0});

            if (!result.success) {
                toast.error("Failed to fetch posts");
            } else {
                setPosts(result.data);
            }
        }

        fetchPosts();
    }, []);

    const handlePostAdd = (post: Post) => {
        setPosts(prevPosts => [post, ...prevPosts]);
    }

    return <div className={styles.wrapper}>
        <div className={styles.content}>
            <AddPost onAdd={handlePostAdd}/>
            <PostCollection posts={posts}/>
        </div>
    </div>
}