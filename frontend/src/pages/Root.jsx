import { useContext, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useLoaderData, useRevalidator } from "react-router-dom";
import RootAlert from "./RootComponents/RootAlert";
import AddRobotModal from "./RootComponents/AddRobotModal";
import useFetcherWithReset from "../hooks/useFetcherWithReset";
import RobotCard from "./RootComponents/RobotCard";
import EditRobotModal from "./RootComponents/EditRobotModal";
import DeleteRobotModal from "./RootComponents/DeleteRobotModal";
import { AuthenticationContext } from "../contexts";
import RobotInfoModal from "./RootComponents/RobotInfoModal";


export async function allRobotsLoader() {

    const robots = await fetch("/api/robots")
    .then(response => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 401) {
            return null;
        }
        throw new Error(response.statusText);
    })
    .catch(error => {
        console.log(error);
        return null;
    });

    const csrfToken = await fetch("/api/csrf")
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error(response.statusText);
    })
    .catch(error => console.log(error));

    return { robots, csrfToken };
}

export async function addRobotAction({ request }) {
    const formData = await request.formData();

    const success = await fetch("/api/robots", {
        method: "post",
        body: formData,
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

export async function editRobotAction({ request, params }) {
    const formData = await request.formData();

    const success = await fetch("/api/robots/" + params.robotId, {
        method: "put",
        body: formData,
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

export async function deleteRobotAction({ request, params }) {

    const success = await fetch("/api/robots/" + params.robotId, {
        method: "delete",
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

let nextMessageId = 0;

export default function Root() {
    const user = useContext(AuthenticationContext);

    const { robots, csrfToken } = useLoaderData();

    const revalidator = useRevalidator();
    
    const [showAddModal, setShowAddModal] = useState(false);
    const [editRobotId, setEditRobotId] = useState(null);
    const [deleteRobotId, setDeleteRobotId] = useState(null);
    const [robotInfo, setRobotInfo] = useState(null);

    const addRobotFetcher = useFetcherWithReset();
    const editRobotFetcher = useFetcherWithReset();
    const deleteRobotFetcher = useFetcherWithReset();

    const [messages, setMessages] = useState([]);

    if (addRobotFetcher.data === true) {
        setShowAddModal(false);
        setMessages([
            ...messages,
            {
                id: nextMessageId++,
                variant: "success",
                message: "New robot has been created!",
                createdAt: Date.now(),
            },
        ]);
        addRobotFetcher.reset();
    }

    if (editRobotFetcher.data === true) {
        setEditRobotId(null);
        setMessages([
            ...messages,
            {
                id: nextMessageId++,
                variant: "success",
                message: "Robot has been updated!",
                createdAt: Date.now(),
            },
        ]);
        editRobotFetcher.reset();
    }

    if (deleteRobotFetcher.data === true) {
        setDeleteRobotId(null);
        setMessages([
            ...messages,
            {
                id: nextMessageId++,
                variant: "success",
                message: "Robot has been deleted",
                createdAt: Date.now(),
            },
        ]);
        deleteRobotFetcher.reset();
    }
    
    if (deleteRobotFetcher.data === false) {
        setDeleteRobotId(null);
        setMessages([
            ...messages,
            {
                id: nextMessageId++,
                variant: "danger",
                message: "Something went wrong while deleting this robot...",
                createdAt: Date.now(),
            },
        ]);
        deleteRobotFetcher.reset();
    }

    return (
        <>
            <AddRobotModal fetcher={addRobotFetcher} showModal={showAddModal} setShowModal={setShowAddModal} csrfToken={csrfToken} />
            <EditRobotModal fetcher={editRobotFetcher} robotId={editRobotId} setRobotId={setEditRobotId} csrfToken={csrfToken} />
            <DeleteRobotModal fetcher={deleteRobotFetcher} robotId={deleteRobotId} setRobotId={setDeleteRobotId} csrfToken={csrfToken} />
            <RobotInfoModal robot={robotInfo} setRobot={setRobotInfo} />
            <Container>
                {
                    messages.map(message => (
                        <Row key={message.id}>
                            <Col>
                                <RootAlert variant={message.variant} message={message.message} />
                            </Col>
                        </Row>
                    ))
                }
                <Row>
                    <Col>
                        <h1>{user ? "Hi, " + user.username : "Home"}</h1>
                    </Col>
                </Row>
                {user ? <>
                    <Row>
                        <Col md="auto">
                            <Button onClick={() => setShowAddModal(true)}>Add robot</Button>
                        </Col>
                        <Col md="auto">
                            <Button onClick={() => revalidator.revalidate()}>
                                <div style={{rotate: "30deg"}}>
                                    <i className="bi bi-arrow-repeat"></i>
                                </div>
                            </Button>
                        </Col>
                    </Row>
                    <Row className="g-4 my-1" xs={1} sm={1} md={2} lg={3} xl={3} xxl={4}>
                        {robots && robots._embedded && robots._embedded.robotEntityList.length ? robots._embedded.robotEntityList.map((robot) => (
                            <Col key={robot.id}>
                                <RobotCard user={user} robot={robot} setRobotInfo={setRobotInfo} setEditRobotId={setEditRobotId} setDeleteRobotId={setDeleteRobotId} />
                            </Col>
                        )) : <Col><p><i>No robots</i></p></Col>}
                    </Row>
                </> : <Row><Col><p>Please, log in to access your robots</p></Col></Row>}
            </Container>
        </>
    );
}
