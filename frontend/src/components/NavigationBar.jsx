import { Button, Navbar, Container, Image, Nav, ButtonGroup, Dropdown } from "react-bootstrap";
import { Link, Outlet, useLoaderData, useNavigate } from "react-router-dom";
import useFetcherWithReset from "../hooks/useFetcherWithReset";
import { useState } from "react";
import LogoutModal from "./LogoutModal";
import { AuthenticationContext } from "../contexts";

export async function userLoader() {
    const user = await fetch("/api/user")
    .then(response => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 401) {
            return null;
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.log(error);
        return null;
    });

    return { user };
}

export async function logoutAction({ request }) {
    const success = await fetch("/api/logout", {
        method: "POST",
        body: await request.formData(),
    }).then(response => {
        if (response.ok) {
            return true;
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.log(error);
        return false;
    });

    return success;
}

export default function NavigationBar() {
    const navigate = useNavigate();
    const { user } = useLoaderData();
    const [showLogoutModal, setShowLogoutModal] = useState(false);

    const logoutFetcher = useFetcherWithReset();

    if (logoutFetcher.data === true) {
        setShowLogoutModal(false);
        logoutFetcher.reset();
    }

    return (
        <>
            <LogoutModal fetcher={logoutFetcher} showModal={showLogoutModal} setShowModal={setShowLogoutModal} />
            <Navbar className="bg-body-tertiary">
                <Container>
                    <Link to="/" className="navbar-brand">
                        webHub
                    </Link>
                    <Nav>
                        {user ? <Dropdown as={ButtonGroup} align="end">
                            <Link variant="outline-info" className="btn btn-outline-info d-flex align-items-center" to={"/user/" + user.id}>
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
