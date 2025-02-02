import { useState } from "react";
import { Alert } from "react-bootstrap";

export default function RootAlert({ variant, message, }) {
    const [ show, setShow ] = useState(true);

    if (!show) {
        return null;
    }

    return (
        <Alert variant={variant} onClose={() => {
            setShow(false);
        }} dismissible>
            {message}
        </Alert>
    );
}