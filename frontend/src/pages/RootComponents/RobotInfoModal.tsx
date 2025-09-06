import {
    Button,
    FormControl,
    ListGroup,
    Modal,
    ModalBody,
    ModalFooter,
    ModalHeader,
    ModalTitle,
} from "react-bootstrap";
import { CsrfResponse, Robot, User } from "../../types";
import { useState } from "react";
import { ActionFunctionArgs, FetcherWithComponents, Link, useFetcher } from "react-router-dom";
import { Check, UserPlus } from "lucide-react";
import DeleteButton from "../../components/DeleteButton";
import { v4 as randomUUID } from "uuid";

import "../../css/Button.scss";
import "../../css/RobotInfoModal.scss";

type RobotInfoModalProps = {
    robot: Robot | null,
    setRobot: (robot: Robot | null) => void,
    user: User | null,
    csrfToken: CsrfResponse,
};

export default function RobotInfoModal({ robot, setRobot, user, csrfToken }: RobotInfoModalProps) {

    const [ modalExpanded, setModalExpanded ] = useState<boolean>(false);

    const handleCloseModal = () => setRobot(null);

    const userIsOwner = !!user && user?.id === robot?.owner.id;
    const expandedClass = modalExpanded && userIsOwner ? "expanded" : "";

    return (
        <Modal show={!!robot} onHide={handleCloseModal} className={`robot-info-modal ${expandedClass}`}>
            <ModalHeader closeButton>
                <ModalTitle>Robot Info</ModalTitle>
            </ModalHeader>
            <ModalBody>
                <div className={`info-section ${expandedClass}`}>
                    <RobotInfo robot={robot} />
                    {userIsOwner && (
                        <Button
                            id="robot-info-modal-hide-show-shared-users-button"
                            variant="outline-secondary"
                            onClick={() => setModalExpanded(value => !value)}
                        >
                            {modalExpanded ? "Hide" : "Show"} shared
                        </Button>
                    )}
                </div>
                <SharedUsers robot={robot} expandedClass={expandedClass} csrfToken={csrfToken} />
            </ModalBody>
            <ModalFooter>
                <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
            </ModalFooter>
        </Modal>
    );
}

function RobotInfo({ robot }: Pick<RobotInfoModalProps, "robot">) {
    return (
        <div>
            <p>
                <span className="text-info">
                    In order to get access to your robot, it has to provide its <b>ID</b> and its <b>password</b>.
                    This means that you should copy the data from here and paste it into the designated fields.
                </span>
                <br />
                <br />
                <b>Name:</b> <span id="robot-info-modal-robot-name-span">{robot?.name}</span>
                <br />
                <b>ID:</b> <span id="robot-info-modal-robot-id-span">{robot?.id}</span>
                <br />
                <b>Password:</b> <span id="robot-info-modal-robot-password-span">{robot?.password}</span>
                <br />
                <b>Owned by:</b> <span id="robot-info-modal-robot-owner-username-span">{robot?.owner.username}</span>
                <br />
                {robot && (
                    <>
                        <b>Robot was created at: </b>
                        <span id="robot-info-modal-robot-created-at-span">{new Date(robot.createdAt).toLocaleString()}</span>
                        <br />
                    </>
                )}
                <i>
                    This robot is currently {robot?.online ? (
                        <span id="robot-info-modal-robot-online-status-span" className="text-success">online</span>
                    ) : (
                        <span id="robot-info-modal-robot-online-status-span" className="text-danger">offline</span>
                    )}
                </i>
            </p>
        </div>
    );
}

type SharedUsersProps = Pick<RobotInfoModalProps, "robot" | "csrfToken"> & { expandedClass: "expanded" | "" }
type UserToShareIdType = string;

function SharedUsers({ robot, expandedClass, csrfToken }: SharedUsersProps) {

    const [ usersToShare, setUsersToShare ] = useState<UserToShareIdType[]>([]);

    const fetcher = useFetcher<User[]>();

    const addUserToShare = () => setUsersToShare(prevIds => [
        randomUUID(),
        ...prevIds,
    ]);

    const removeUserToShare = (id: UserToShareIdType) =>
        setUsersToShare(prevUsers => prevUsers.filter(userId => userId !== id));

    const sharedUsers = fetcher.data === undefined ? robot?.sharedUsers : fetcher.data;
    console.log(sharedUsers);

    return (
        <div className={`shared-users-block ${expandedClass}`}>
            <span id="shared-users-header">
                <span>Shared with:</span>
                <Button id="robot-info-shared-users-add-user" variant="outline-primary" size="sm" onClick={addUserToShare}>
                    <UserPlus size={18} />
                </Button>
            </span>

            <ListGroup variant="flush">
                {usersToShare.map(userId => (
                    <ListGroup.Item key={userId}>
                        <NewUser
                            userId={userId}
                            removeUser={removeUserToShare}
                            fetcher={fetcher}
                            actionUri={`/share/${robot?.id ?? 0}`}
                            csrfToken={csrfToken}
                        />
                    </ListGroup.Item>
                ))}

                {sharedUsers?.map(user => (
                    <ListGroup.Item key={user.id}>
                        <SharedUser
                            user={user}
                            fetcher={fetcher}
                            actionUri={`/unshare/${robot?.id ?? 0}`}
                            csrfToken={csrfToken}
                        />
                    </ListGroup.Item>
                ))}
            </ListGroup>
        </div>
    );
}

interface WithFetcher {
    fetcher: FetcherWithComponents<User[]>,
    actionUri: `/${"share" | "unshare"}/${number}`,
    csrfToken: CsrfResponse,
}

function NewUser({ userId, removeUser, fetcher, actionUri, csrfToken }: WithFetcher & {
    userId: UserToShareIdType,
    removeUser: (id: UserToShareIdType) => void,
}) {

    const [ username, setUsername ] = useState<string | null>(null);

    const onSubmit = () => {
        if (!username) {
            return;
        }

        const formData = new FormData();
        formData.set("users", username);
        formData.set(csrfToken.parameterName, csrfToken.token);

        fetcher.submit(
            formData,
            { method: "post", action: actionUri },
        ).then(() => {
            removeUser(userId);
        });
    };

    return (
        <fetcher.Form onSubmit={(event) => {
            event.preventDefault();
            onSubmit();
        }}>
            <FormControl
                type="text"
                name="username"
                size="sm"
                placeholder="Username"
                onChange={event => setUsername(event.target.value)}
            />
            <div className="share-user-actions">
                <span className="rexus-button share-button" onClick={onSubmit}>
                    <Check size={18} />
                </span>
                <DeleteButton onClick={() => removeUser(userId)} />
            </div>

        </fetcher.Form>
    );
}

function SharedUser({ user, fetcher, actionUri, csrfToken }: { user: User } & WithFetcher) {

    const onSubmit = () => {
        if (!user.id) {
            return;
        }

        const formData = new FormData();
        formData.set("users", String(user.id));
        formData.set(csrfToken.parameterName, csrfToken.token);

        fetcher.submit(
            formData,
            { method: "delete", action: actionUri },
        ).then(() => {});
    };


    return (
        <>
            <Link to={"user/" + user.id} className="link-light">{user.username}</Link>
            <DeleteButton onClick={onSubmit} />
        </>
    );
}

export const sendShareRobotRequest = async ({ request, params }: ActionFunctionArgs): Promise<User[]> => {
    const formData = await request.formData();

    return await fetch("/api/robots/" + params.robotId + "/share", {
        method: "post",
        body: formData,
    }).then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.log(error);
        return [];
    });
};

export const sendUnshareRobotRequery = async ({ request, params }: ActionFunctionArgs): Promise<User[]> => {
    const formData = await request.formData();

    return await fetch("/api/robots/" + params.robotId + "/unshare", {
        method: "delete",
        body: formData,
    }).then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.log(error);
        return [];
    });
};
