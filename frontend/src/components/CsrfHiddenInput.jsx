import { FormControl } from "react-bootstrap";

export default function CsrfHiddenInput({ csrfToken }) {
    return <FormControl name={csrfToken.parameterName} value={csrfToken.token} hidden={true} readOnly={true}/>;
}