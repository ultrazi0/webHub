import { useContext, useState } from "react";
import { AuthenticationContext } from "../contexts";
import { Form, Navigate, redirect, useActionData, useLoaderData, useNavigate } from "react-router-dom";
import { Button, Container, Row, Col, Image, FormGroup, FormLabel, FormControl, FormText } from "react-bootstrap";
import CsrfHiddenInput from "../components/CsrfHiddenInput";


export async function editUserAction({ request }) {
    const errors = {};
    
    const user = await fetch("/api/user", {
        method: "PUT",
        body: await request.formData()
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 404) {
            errors.oldPasswordDoesNotMatch = "Old password does not match";
        } else if (response.status === 409) {
            errors.usernameTaken = "This username is already taken"
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.error(error);
        return null;
    })

    if (user) {
        return redirect("/user/" + user.id);
    }

    return errors;
}

export default function EditUserPage() {
    const navigate = useNavigate();

    const user = useContext(AuthenticationContext);
    const { csrfToken } = useLoaderData();
    const errors = useActionData();

    const [username, setUsername] = useState(user ? user.username : "");
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [newPasswordRepeat, setNewPasswordRepeat] = useState("");

    const passwordsDoNotMatch = newPassword !== newPasswordRepeat && newPassword.length > 0 && newPasswordRepeat.length > 0;
    const newAndOldPasswordsAreTheSame = newPassword.length > 0 && newPassword === oldPassword && newPassword === newPasswordRepeat
    const allowSubmit = newPassword.length > 0
        ? newPassword === newPasswordRepeat && newPassword !== oldPassword
        : user && username.length > 0 && user.username !== username && oldPassword.length > 0;

    return (
        <>
            {user === null && <Navigate to={"/"} replace={true} />}
            <Container>
                <Row>
                    <Col className="text-center">
                        {user && <Image src={"https://robohash.org/" + user.username + ".png?set=set5&size=360x360"} className="mt-2" fluid roundedCircle />}
                    </Col>
                    <Col>
                        <Form method="post">
                            <h1 className="mt-4">Edit profile</h1>
                            <hr />
                            <FormGroup className="mb-3 mt-3" controlId="formEditUsername">
                                <FormLabel>Username</FormLabel>
                                <FormControl type="text" placeholder="Enter your username" name="username" value={username} onChange={(event) => setUsername(event.target.value)} />
                                {errors?.usernameTaken && <FormText className="text-danger-emphasis">{errors.usernameTaken}</FormText>}
                                </FormGroup>
                            <FormGroup className="mb-3" controlId="formOldPassword">
                                <FormLabel>Old Password</FormLabel>
                                <FormControl type="password" placeholder="Old password" name="oldPassword" value={oldPassword} onChange={(event) => setOldPassword(event.target.value)} />
                                {newAndOldPasswordsAreTheSame && <FormText className="text-danger-emphasis">New and old passwords are the same</FormText>}
                                {errors?.oldPasswordDoesNotMatch && <FormText className="text-danger-emphasis">{errors.oldPasswordDoesNotMatch}</FormText>}
                            </FormGroup>
                            <FormGroup className="mb-3" controlId="formNewPassword">
                                <FormLabel>Password</FormLabel>
                                <FormControl type="password" placeholder="New password" name="newPassword" value={newPassword} onChange={(event) => setNewPassword(event.target.value)} />
                                <FormText>If you do not wish to update your password, leave this and the next fields empty</FormText>
                            </FormGroup>
                            <FormGroup className="mb-3" controlId="formNewPasswordRepeat">
                                <FormLabel>Repeat Password</FormLabel>
                                <FormControl type="password" placeholder="Repeat the new password" name="newPasswordRepeat" 
                                    value={newPasswordRepeat} onChange={(event) => setNewPasswordRepeat(event.target.value)} />
                                {passwordsDoNotMatch && <FormText className="text-danger-emphasis">Passwords do not match</FormText>}
                            </FormGroup>
                            {csrfToken && <FormGroup className="mb-3" controlId="formEditCsrfToken"><CsrfHiddenInput csrfToken={csrfToken} /></FormGroup>}
                            <Button variant="secondary" onClick={() => navigate(-1)}>Cancel</Button>
                            {" "}
                            <Button type="submit" variant="primary" disabled={!allowSubmit}>Submit</Button>
                        </Form>
                    </Col>
                </Row>
            </Container>
        </>
    );
}