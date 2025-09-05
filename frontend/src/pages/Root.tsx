import { useContext, useState } from "react";
import { Button, Card, Col, Container, Row } from "react-bootstrap";
import { ActionFunctionArgs, useLoaderData, useRevalidator } from "react-router-dom";
import RootAlert, { AlertMessage } from "./RootComponents/RootAlert";
import AddRobotModal from "./RootComponents/AddRobotModal";
import useFetcherWithReset from "../hooks/useFetcherWithReset";
import RobotCard from "./RootComponents/RobotCard";
import EditRobotModal from "./RootComponents/EditRobotModal";
import DeleteRobotModal from "./RootComponents/DeleteRobotModal";
import { AuthenticationContext } from "../contexts";
import RobotInfoModal from "./RootComponents/RobotInfoModal";
import { CsrfResponse, RestListResponse, Robot } from "../types";
import { Plus, RefreshCcw } from "lucide-react";

import "../css/Root.scss";

type LoadedRobots = {
    robots: RestListResponse<Robot, "robotEntityList"> | null;
    csrfToken: CsrfResponse;
}

export async function allRobotsLoader(): Promise<LoadedRobots>  {

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
            console.error(error);
            return null;
        });

    const csrfToken = await fetch("/api/csrf")
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error(response.statusText);
        })
        .catch(error => console.error(error));

    return { robots, csrfToken };
}

export async function addRobotAction({ request }: ActionFunctionArgs) {
    const formData = await request.formData();

    return await fetch("/api/robots", {
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
}

export async function editRobotAction({ request, params }: ActionFunctionArgs) {
    const body: Record<string, unknown> & { csrf?: CsrfResponse } = await request.json();

    if (body.csrf) {
        request.headers.append(body.csrf.headerName, body.csrf.token);
        delete body.csrf;
    }

    return await fetch("/api/robots/" + params.robotId, {
        method: "put",
        body: JSON.stringify(body),
        headers: request.headers,
    }).then(response => {
        if (response.ok) {
            return true;
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.log(error);
        return false;
    });
}

export async function deleteRobotAction({ request, params }: ActionFunctionArgs) {

    return await fetch("/api/robots/" + params.robotId, {
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
}

let nextMessageId = 0;

export default function Root() {
    const user = useContext(AuthenticationContext);

    const { robots, csrfToken } = useLoaderData<LoadedRobots>();

    const revalidator = useRevalidator();

    const [showAddModal, setShowAddModal] = useState<boolean>(false);
    const [editRobotId, setEditRobotId] = useState<number | null>(null);
    const [deleteRobotId, setDeleteRobotId] = useState<number | null>(null);
    const [robotInfo, setRobotInfo] = useState<Robot | null>(null);

    const addRobotFetcher = useFetcherWithReset<boolean>();
    const editRobotFetcher = useFetcherWithReset<boolean>();
    const deleteRobotFetcher = useFetcherWithReset<boolean>();

    const [messages, setMessages] = useState<Array<AlertMessage>>([]);

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
            <RobotInfoModal robot={robotInfo} setRobot={setRobotInfo} user={user} csrfToken={csrfToken} />
            <Container className="mt-3">
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
                {user ? (
                    <>
                        <Row>
                            <Col md="auto">
                                <Button id="home-refresh-robots-button" disabled={revalidator.state !== "idle"} onClick={() => revalidator.revalidate()}>
                                    <RefreshCcw className={`home-refresh-button-icon ${revalidator.state !== "idle" ? "loading" : ""}`} size={16} />
                                </Button>
                            </Col>
                        </Row>
                        <Row className="g-4 my-1" xs={1} sm={1} md={2} lg={3} xl={3} xxl={4}>
                            {robots && robots._embedded?.robotEntityList?.length
                                && robots._embedded.robotEntityList.map((robot) => (
                                    <Col key={robot.id}>
                                        <RobotCard
                                            user={user}
                                            robot={robot}
                                            setRobotInfo={setRobotInfo}
                                            setEditRobotId={setEditRobotId}
                                            setDeleteRobotId={setDeleteRobotId}
                                        />
                                    </Col>
                                ))
                            }
                            <Col>
                                <Card className="robot-card robot-card-add" onClick={() => setShowAddModal(true)}>
                                    <Plus />
                                </Card>
                            </Col>
                        </Row>
                    </>
                ) : (
                    <Row>
                        <Col>
                            <p>Please, log in to access your robots</p>
                        </Col>
                    </Row>
                )}
            </Container>
        </>
    );
}
