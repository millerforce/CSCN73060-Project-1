import type {Account} from "./account.ts";

export type PostUpload = {
    content: string;
}

export type Post = {
    id: number;
    author: Account;
    content: string;
    numberOfLikes: number;
    numberOfComments: number;
    createdAt: Date;
    updatedAt: Date;
}

export type Comment = {
    id: number;
    postId: number;
    account: Account;
    content: string;
    createdAt: Date;
    updatedAt: Date;
}