import type {Post, PostUpload} from "../types/post.ts";
import {apiRequest, type ApiResponse} from "../httpRequest.ts";
import type {PaginationQuery} from "../types/general.ts";

export default class PostService {
    private static BASE_URL = "/post";

    public static async createPost(upload: PostUpload): Promise<ApiResponse<Post>> {
        return await apiRequest<Post>({
            method: "POST",
            url: this.BASE_URL,
            data: upload
        });
    }

    public static async deletePost(postId: string): Promise<ApiResponse<void>> {
        return await apiRequest({
            method: "DELETE",
            url: this.BASE_URL + "/" + postId
        });
    }

    public static async editPost(postId: string, upload: PostUpload): Promise<ApiResponse<Post>> {
        return await apiRequest<Post>({
            method: "PATCH",
            url: this.BASE_URL + "/" + postId,
            data: upload
        });
    }

    public static async getPosts(params: PaginationQuery): Promise<ApiResponse<Post[]>> {
        return await apiRequest<Post[]>({
            method: "GET",
            url: this.BASE_URL + "/posts",
            params
        })
    }

    public static async likePost(postId: string): Promise<ApiResponse<Post>> {
        return await apiRequest<Post>({
            method: "PUT",
            url: this.BASE_URL + "/" + postId
        })
    }
}