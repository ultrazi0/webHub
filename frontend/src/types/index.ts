export type Robot = {
    id: number,
    name: string,
    password: string,
    createdAt: Date,
    owner: User
    online: boolean,
    sharedUsers: User[]
}

export type User = {
    id: number,
    username?: string | null,
}

export type RestListResponse<T, K extends string> = {
    _embedded: {
        [key in K]: T[] | null | undefined;
    } | null | undefined;
}

export type CsrfResponse = {
    parameterName: string;
    headerName: string;
    token: string;
}
