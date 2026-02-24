import type {Account} from "./account.ts";

export type PostUpload = {
    content: string;
}

export type Post = {
    id: string;
    author: Account;
    content: string;
    numberOfLikes: number;
    numberOfComments: number;
    createdAt: Date;
    updatedAt: Date;
    likedByCurrentUser: boolean;
}

export type Comment = {
    id: string;
    postId: string;
    account: Account;
    content: string;
    createdAt: Date;
    updatedAt: Date;
    numberOfLikes: number;
    likedByCurrentUser: boolean;
}

export type CommentUpload = {
    content: string;
}