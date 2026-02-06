import type { Author } from "./author.ts";

export interface Post {
    id: string;
    author: Author;
    content: string;
    numberOfLikes: number;
    numberOfComments: number;
    createdAt: string;
    updatedAt: string;
}
