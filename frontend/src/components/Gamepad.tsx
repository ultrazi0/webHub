import useGamepad, { Callbacks, Layout } from "../hooks/useGamepad";

export default function Gamepad<T extends Layout>({ layout, callbacks }: { layout: T, callbacks: Callbacks<T>}) {

    useGamepad<T>(layout, callbacks);

    return (
        <></>
    );
}