import { useEffect, useState } from "react";
import { useFetcher } from "react-router-dom";

export default function useFetcherWithReset() {
    const fetcher = useFetcher();
    const [data, setData] = useState(fetcher.data);

    useEffect(() => {
        if (fetcher.state === "idle") {
            setData(fetcher.data);
        }
    }, [fetcher.state, fetcher.data]);

    return {
        ...fetcher,
        data: data,
        reset: () => setData(undefined)
    };
}