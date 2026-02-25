import styles from "@styles/components/addPost.module.css";
import { useForm } from "react-hook-form";
import type { Post, PostUpload } from "../../http/types/post.ts";
import PostService from "../../http/services/postService.ts";
import { toast } from "react-toastify";

interface AddPostProps {
  onAdd: (post: Post) => void;
}

export default function AddPost({ onAdd }: Readonly<AddPostProps>) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<PostUpload>();

  const onSubmit = async (upload: PostUpload) => {
    const response = await PostService.createPost(upload);

    if (response.success) {
      toast.success("Post created");

      reset({ content: "" });

      onAdd(response.data);
    } else {
      toast.error("Failed to add your post");
      console.error("Error:", response.error);
    }
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.header}>Add a new Leap to the pond</div>
      <form className={styles.form} onSubmit={handleSubmit(onSubmit)}>
        <div className={styles.post}>
          <label>
            What do you want to say?{" "}
            <textarea
              className={styles.textarea}
              {...register("content", {
                required: "You must say something to leap my friend",
              })}
            />
          </label>

          {errors.content && <div>{errors.content.message}</div>}
        </div>

        <button className={styles.button} type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Sending your Leap to the pond" : "Leap"}
        </button>
      </form>
    </div>
  );
}
