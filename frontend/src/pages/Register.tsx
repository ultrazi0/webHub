import { Col, Container, FormControl, FormGroup, FormLabel, FormText, Image, Row } from "react-bootstrap";
import { ActionFunctionArgs, Form, redirect, useActionData, useLoaderData } from "react-router-dom";

import logo from "../logo.svg";
import CsrfHiddenInput from "../components/CsrfHiddenInput";
import { useState } from "react";
import { LoginLoaderData } from "./Login";
import FormButton from "../components/FormButton";
import { useNavigation } from "react-router";

export async function registerAction({ request }: ActionFunctionArgs) {
    const response = await fetch("/api/register", {
        method: "POST",
        body: await request.formData(),
    });

    return response.ok ? redirect("/") : response;
}

export default function RegisterPage() {
    const { csrfToken } = useLoaderData<LoginLoaderData>();
    const response = useActionData<{ error?: string }>();
    const { state } = useNavigation();

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
                            {response?.error && <FormText className="text-danger-emphasis">{response.error}</FormText>}
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
                        <FormButton isLoading={state !== "idle"} disabled={!allowSubmit}>Register</FormButton>
                    </Form>
                </Col>
            </Row>
        </Container>
    );
}
