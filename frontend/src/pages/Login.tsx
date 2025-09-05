import logo from "../logo.svg";

import { Container, FormControl, FormGroup, FormLabel, FormText, Row, Col, Image } from "react-bootstrap";
import { ActionFunctionArgs, Form, redirect, useActionData, useLoaderData } from "react-router-dom";
import CsrfHiddenInput from "../components/CsrfHiddenInput";
import { CsrfResponse } from "../types";
import FormButton from "../components/FormButton";
import { useNavigation } from "react-router";

export type LoginLoaderData = {
    csrfToken: CsrfResponse;
}

export async function loginLoader(): Promise<LoginLoaderData> {

    const csrfToken = await fetch("/api/csrf")
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error(response.statusText);
    }).catch(error => console.log(error));

    return { csrfToken };
}

type ActionData = Response | null;

export async function loginAction({ request }: ActionFunctionArgs): Promise<ActionData> {
    const success = await fetch("/api/login", {
        method: "POST",
        body: await request.formData(),
    }).then(response => {
        if (response.ok) {
            console.log("Logged in!");
            return true;
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.log("UNAUTHORIZED!");
        console.log(error);
        return false;
    });

    return success ? redirect("/") : null;
}

export default function LoginPage() {
    const { csrfToken } = useLoaderData<LoginLoaderData>();
    const user = useActionData<ActionData>();
    const { state } = useNavigation();

    return (
        <Container>
            <Row>
                <Col className="text-center">
                    <Image src={logo} fluid />
                    <p>Welcome to webHub!</p>
                </Col>
                <Col>
                    <Form method="post">
                        <FormGroup className="mb-3 mt-3" controlId="formLoginUsername">
                            <FormLabel>Username</FormLabel>
                            <FormControl type="text" placeholder="Enter your username" name="username" />
                        </FormGroup>
                        <FormGroup className="mb-3" controlId="formLoginPassword">
                            <FormLabel>Password</FormLabel>
                            <FormControl type="password" placeholder="Password" name="password" />
                            {user === null && (
                                <FormText className="text-danger-emphasis">Wrong username and/or password</FormText>
                            )}
                        </FormGroup>
                        {csrfToken && (
                            <FormGroup className="mb-3" controlId="formLoginCsrfToken">
                                <CsrfHiddenInput csrfToken={csrfToken} />
                            </FormGroup>
                        )}
                        <FormButton isLoading={state !== "idle"}>Log in</FormButton>
                    </Form>
                </Col>
            </Row>
        </Container>
    );
}