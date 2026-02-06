export type Account = {
    id: string;
    username: string;
    createdAt: Date;
}

export type AccountCredentials = {
    username: string;
    password: string;
}

export type AccountUpload = {
    username: string;
    password: string;
}

export type AccountSession = {
    accountId: string;
    token: string;
}