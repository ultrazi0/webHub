import { useContext, useState } from "react";
import { Button, Col, Container, Image, Row } from "react-bootstrap";
import { useLoaderData, useNavigate } from "react-router-dom";
import { AuthenticationContext } from "../contexts";
import DeleteUserModal from "../components/DeleteUserModal";
import useFetcherWithReset from "../hooks/useFetcherWithReset";


export async function loadUser({ params }) {
    const user = await fetch("/api/user/" + params.userId)
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.error(error);
        return null;
    });
    
    return { user };
}

export default function UserPage() {
    const navigate = useNavigate();

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const deleteUserFetcher = useFetcherWithReset();

    const currentUser = useContext(AuthenticationContext);
    let { user } = useLoaderData();    

    if (deleteUserFetcher.data === true) {
        setShowDeleteModal(false);
        deleteUserFetcher.reset();
    }

    if (user === null) {
        user = {
            id: null,
            username: "Anonym",
        };
    }

    return (
        <>
            <DeleteUserModal fetcher={deleteUserFetcher} showModal={showDeleteModal} setShowModal={setShowDeleteModal} />
            <Container>
                <Row>
                    <Col className="text-center">
                        <Image src={"https://robohash.org/" + user.username + ".png?set=set5&size=360x360"} fluid roundedCircle className="mt-2" />
                    </Col>
                    <Col>
                        <h1 className="mt-4">{currentUser?.id === user.id ? "Hi there, " + user.username + "!" : user.username}</h1>
                        <hr />
                        <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Odit at, repellat tenetur voluptatibus ratione quas minima reprehenderit? Beatae, voluptatibus praesentium eaque nam sed voluptatem, dolor optio ratione magni eos ex?</p>
                        {currentUser?.id === user.id && 
                            <span className="d-flex justify-content-between">
                                <Button onClick={() => navigate("/user/edit")}>Edit</Button>
                                <Button variant="danger" onClick={() => setShowDeleteModal(true)}>Delete</Button>
                            </span>}
                    </Col>
                </Row>
            </Container>
        </>
    );
}