import { useRouteError } from "react-router-dom";

export default function ErrorPage() {
    const error = useRouteError() as { message?: string };
    console.error("The following error caused the application to die:\n", error);

    return (
        <>
            <h1>Flatlined!</h1>
            <h4>Congratulations! You have just flatlined the program!</h4>
            <p>You have succeeded using:</p>
            <p>
                <i>
                    {error.message}
                </i>
            </p>
        </>
    );
}
