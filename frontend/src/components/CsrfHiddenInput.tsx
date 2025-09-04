import { FormControl } from "react-bootstrap";
import { CsrfResponse } from "../types";

export default function CsrfHiddenInput({ csrfToken }: { csrfToken: CsrfResponse }) {
    return <FormControl name={csrfToken.parameterName} value={csrfToken.token} hidden readOnly />;
}