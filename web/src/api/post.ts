import type {Account} from "./account.ts";

export type PostUpload = {
    content: string;
}

export type Post = {
    id: string;
    author: Account;
    content: string;
    numberOfLikes: number;
    createdAt: Date;
    updatedAt: Date;
}