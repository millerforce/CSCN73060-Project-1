import type {Comment, PostUpload} from "../types/post.ts";
import {apiRequest, type ApiResponse} from "../httpRequest.ts";

export default class CommentService {
    private static BASE_URL = "/comment";

    public static async createComment(postId: string, upload: PostUpload): Promise<ApiResponse<Comment>> {
        return await apiRequest<Comment>({
            method: "POST",
            url: this.BASE_URL + "/" + postId,
            data: upload
        });
    }

    public static async updateComment(commentId: string, upload: PostUpload): Promise<ApiResponse<Comment>> {
        return await apiRequest<Comment>({
            method: "PATCH",
            url: this.BASE_URL + "/" + commentId,
            data: upload
        })
    }

    public static async getComments(postId: string): Promise<ApiResponse<Comment[]>> {
        return await apiRequest<Comment[]>({
            method: "GET",
            url: this.BASE_URL + "/" + postId
        })
    }

    public static async deleteComment(commentId: string): Promise<ApiResponse<void>> {
        return await apiRequest({
            method: "DELETE",
            url: this.BASE_URL + "/" + commentId
        })
    }

    public static async likeComment(commentId: string): Promise<ApiResponse<Comment>> {
        return await apiRequest<Comment>({
            method: "PUT",
            url: this.BASE_URL + "/" + commentId
        })
    }
}