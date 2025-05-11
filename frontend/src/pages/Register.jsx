import { Col, Container, Row, Image, FormGroup, FormLabel, FormControl, Button, FormText } from "react-bootstrap";
import { Form, redirect, useActionData, useLoaderData } from "react-router-dom";

import logo from "../logo.svg";
import CsrfHiddenInput from "../components/CsrfHiddenInput";
import { useState } from "react";


export async function registerAction({ request }) {
    const success = await fetch("/api/register", {
        method: "POST",
        body: await request.formData()
    }).then(response => {
        if (response.ok) {
            console.log("New user registered!");
            return true;
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.error(error);
        return false;
    })

    if (success) {
        return redirect("/");
    }

    const errors = {};
    errors.usernameTaken = "This username is already taken";

    return errors;
}

export default function RegisterPage() {
    const { csrfToken } = useLoaderData();
    const errors = useActionData();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [passwordRepeat, setPasswordRepeat] = useState("");

    const passwordsDoNotMatch = password !== passwordRepeat && password.length > 0 && passwordRepeat.length > 0;
    const allowSubmit = password === passwordRepeat && username.length > 0 && password.length > 0;

    return (
        <Container>
            <Row>
                <Col className="text-center">
                    <Image src={logo} fluid />
                    <p>Welcome to webHub!</p>
                </Col>
                <Col>
                    <Form method="post">
                        <FormGroup className="mb-3 mt-3" controlId="formRegisterUsername">
                            <FormLabel>Username</FormLabel>
                            <FormControl type="text" placeholder="Enter your username" name="username" value={username} onChange={(event) => setUsername(event.target.value)} />
                            {errors?.usernameTaken && <FormText className="text-danger-emphasis">{errors.usernameTaken}</FormText>}
                        </FormGroup>
                        <FormGroup className="mb-3" controlId="formRegisterPassword">
                            <FormLabel>Password</FormLabel>
                            <FormControl type="password" placeholder="Password" name="password" value={password} onChange={(event) => setPassword(event.target.value)} />
                        </FormGroup>
                        <FormGroup className="mb-3" controlId="formRegisterRepeatPassword">
                            <FormLabel>Repeat Password</FormLabel>
                            <FormControl type="password" placeholder="Repeat password" name="passwordRepeat" 
                                value={passwordRepeat} onChange={(event) => setPasswordRepeat(event.target.value)} />
                            {passwordsDoNotMatch && <FormText className="text-danger-emphasis">Passwords do not match</FormText>}
                        </FormGroup>
                        {csrfToken && <FormGroup className="mb-3" controlId="formRegisterCsrfToken"><CsrfHiddenInput csrfToken={csrfToken} /></FormGroup>}
                        <Button type="submit" variant="primary" disabled={!allowSubmit}>Register</Button>
                    </Form>
                </Col>
            </Row>
        </Container>
    );
}