import { Button, ButtonGroup, Container, Dropdown, Image, Nav, Navbar } from "react-bootstrap";
import { ActionFunctionArgs, Link, Outlet, useLoaderData, useNavigate } from "react-router-dom";
import useFetcherWithReset from "../hooks/useFetcherWithReset";
import { useState } from "react";
import LogoutModal from "./LogoutModal";
import { AuthenticationContext } from "../contexts";
import { User } from "../types";

import logoNoText from "../images/logo-no-text.png";

type UserLoaderData = {
    user: User | null;
}

export async function userLoader(): Promise<UserLoaderData> {
    const user = await fetch("/api/user")
    .then(response => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 401) {
            return null;
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.error(error);
        return null;
    });

    return { user };
}

export async function logoutAction({ request }: ActionFunctionArgs) {
    return await fetch("/api/logout", {
        method: "POST",
        body: await request.formData(),
    }).then(response => {
        if (response.ok) {
            return true;
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.error(error);
        return false;
    });
}

export default function NavigationBar() {
    const navigate = useNavigate();
    const { user } = useLoaderData<UserLoaderData>();
    const [showLogoutModal, setShowLogoutModal] = useState<boolean>(false);

    const logoutFetcher = useFetcherWithReset<boolean>();

    if (logoutFetcher.data === true) {
        setShowLogoutModal(false);
        logoutFetcher.reset();
    }

    return (
        <>
            <LogoutModal fetcher={logoutFetcher} showModal={showLogoutModal} setShowModal={setShowLogoutModal} />
            <Navbar className="bg-body-tertiary">
                <Container>
                    <Link to="/" className="navbar-brand d-flex align-items-center">
                        <Image src={logoNoText} width={32} height={32} className="mx-2" fluid />
                        Rexus
                    </Link>
                    <Nav>
                        {user ? <Dropdown as={ButtonGroup} align="end">
                            <Link className="btn btn-outline-info d-flex align-items-center" to={"/user/" + user.id}>
                                <Image src={"https://robohash.org/" + user.username + ".png?set=set5&size=32x32"} roundedCircle/>
                                {" " + user.username}
                            </Link>
                            
                            <Dropdown.Toggle split variant="outline-info" id="user-dropdown" />

                            <Dropdown.Menu>
                                <Dropdown.Item onClick={() => setShowLogoutModal(true)}>Log out</Dropdown.Item>
                            </Dropdown.Menu>
                        </Dropdown> :
                        <ButtonGroup aria-label="Log in or register">
                            <Button variant="outline-success" onClick={() => navigate("/register")}>Register</Button>
                            <Button variant="primary" onClick={() => navigate("/login")}>Log in</Button>
                        </ButtonGroup>}
                    </Nav>                   
                </Container>
            </Navbar>
            <AuthenticationContext.Provider value={user}>
                <Outlet />
            </AuthenticationContext.Provider>
        </>
    );
}
