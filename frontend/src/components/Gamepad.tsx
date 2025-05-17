import useGamepad, { onChangeCallbacks, Layout } from "../hooks/useGamepad";

export default function Gamepad<T extends Layout>({ layout, callbacks }: { layout: T, callbacks: onChangeCallbacks<T>}) {

    useGamepad<T>(layout, callbacks);

    return (
        <></>
    );
}