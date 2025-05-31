export type Robot = {
    id: number,
    name: string,
    password: string,
    createdAt: Date,
    ownerId: number,
    ownerName: string,
    online: boolean,
}

export type User = {
    id: number,
    username: string,
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
