import logo from "../logo.svg";

import { Button, Container, FormControl, FormGroup, FormLabel, FormText, Row, Col, Image } from "react-bootstrap";
import { Form, redirect, useActionData, useLoaderData } from "react-router-dom";
import CsrfHiddenInput from "../components/CsrfHiddenInput";

export async function loginLoader() {

    const csrfToken = await fetch("/api/csrf")
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error(response.statusText);
    }).catch(error => console.log(error));

    return { csrfToken };
}

export async function loginAction({ request }) {
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
    const { csrfToken } = useLoaderData();
    const user = useActionData();

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
                            {user === null && <FormText className="text-danger-emphasis">Wrong username and/or password</FormText>}
                        </FormGroup>
                        {csrfToken && <FormGroup className="mb-3" controlId="formLoginCsrfToken"><CsrfHiddenInput csrfToken={csrfToken} /></FormGroup>}
                        <Button type="submit" variant="primary">Log in</Button>
                    </Form>
                </Col>
            </Row>
        </Container>
    );
}