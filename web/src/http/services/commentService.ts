import type {Comment, PostUpload} from "../types/post.ts";
import {apiRequest, type ApiResponse} from "../httpRequest.ts";

export default class CommentService {
    private static BASE_URL = "/comment";

    public static async createComment(postId: number, upload: PostUpload): Promise<ApiResponse<Comment>> {
        return await apiRequest<Comment>({
            method: "POST",
            url: this.BASE_URL + "/" + postId.toString(),
            data: upload
        });
    }

    public static async updateComment(commentId: number, upload: PostUpload): Promise<ApiResponse<Comment>> {
        return await apiRequest<Comment>({
            method: "PATCH",
            url: this.BASE_URL + "/" + commentId.toString(),
            data: upload
        })
    }

    public static async getComments(postId: number): Promise<ApiResponse<Comment[]>> {
        return await apiRequest<Comment[]>({
            method: "GET",
            url: this.BASE_URL + "/" + postId.toString()
        })
    }

    public static async deleteComment(commentId: number): Promise<ApiResponse<void>> {
        return await apiRequest({
            method: "DELETE",
            url: this.BASE_URL + "/" + commentId.toString()
        })
    }

    public static async likeComment(commentId: number): Promise<ApiResponse<Comment>> {
        return await apiRequest<Comment>({
            method: "PUT",
            url: this.BASE_URL + "/" + commentId.toString()
        })
    }
}