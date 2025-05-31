import { useEffect, useState } from "react";
import { useFetcher } from "react-router-dom";

export type FetcherWithReset<T> = ReturnType<typeof useFetcher<T>> & {
    reset: () => void,
}

export default function useFetcherWithReset<T>(): FetcherWithReset<T> {
    const fetcher = useFetcher<T>();
    const [data, setData] = useState(fetcher.data);

    useEffect(() => {
        if (fetcher.state === "idle") {
            setData(fetcher.data);
        }
    }, [ fetcher.state, fetcher.data ]);

    return {
        ...fetcher,
        data: data,
        reset: () => setData(undefined),
    };
}