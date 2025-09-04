import { ReactNode, useState } from "react";
import { Alert, AlertProps } from "react-bootstrap";

export type AlertMessage = {
    id: number,
    variant: AlertProps["variant"],
    message: ReactNode,
    createdAt: Date | number,
};

export default function RootAlert({ variant, message }: Pick<AlertMessage, "variant" | "message">) {
    const [ show, setShow ] = useState(true);

    if (!show) {
        return null;
    }

    return (
        <Alert
            variant={variant}
            onClose={() => setShow(false)}
            dismissible
        >
            {message}
        </Alert>
    );
}