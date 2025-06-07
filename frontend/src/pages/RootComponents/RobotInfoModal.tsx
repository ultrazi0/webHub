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

import "../../css/RobotInfoModal.scss";
import { ActionFunctionArgs, FetcherWithComponents, Link, useFetcher } from "react-router-dom";
import { Check, Trash2, UserPlus } from "lucide-react";

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
                            variant="outline-secondary"
                            onClick={() => setModalExpanded(value => !value)
                            }>
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
                <b>Name:</b> <span>{robot?.name}</span>
                <br />
                <b>ID:</b> <span>{robot?.id}</span>
                <br />
                <b>Password:</b> <span>{robot?.password}</span>
                <br />
                <b>Owned by:</b> <span>{robot?.owner.username}</span>
                <br />
                {robot && (
                    <>
                        <b>Robot was created at: </b>
                        <span>{new Date(robot.createdAt).toLocaleString()}</span>
                        <br />
                    </>
                )}
                <i>This robot is currently {robot?.online ? <span className="text-success">online</span> : <span className="text-danger">offline</span>}</i>
            </p>
        </div>
    );
}

type SharedUsersProps = Pick<RobotInfoModalProps, "robot" | "csrfToken"> & { expandedClass: "expanded" | "" }
type UserToShareIdType = ReturnType<Crypto["randomUUID"]>

function SharedUsers({ robot, expandedClass, csrfToken }: SharedUsersProps) {

    const [ usersToShare, setUsersToShare ] = useState<UserToShareIdType[]>([]);

    const fetcher = useFetcher<User[]>();

    const addUserToShare = () => setUsersToShare(prevIds => [
        self.crypto.randomUUID(),
        ...prevIds,
    ]);

    const removeUserToShare = (id: UserToShareIdType) =>
        setUsersToShare(prevUsers => prevUsers.filter(userId => userId !== id));

    const sharedUsers = fetcher.data === undefined ? robot?.sharedUsers : fetcher.data;

    return (
        <div className={`shared-users-block ${expandedClass}`}>
            <span id="shared-users-header">
                <span>Shared with:</span>
                <Button variant="outline-primary" size="sm" onClick={addUserToShare}>
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
        <fetcher.Form>
            <FormControl
                type="text"
                name="username"
                size="sm"
                placeholder="Username"
                onChange={event => setUsername(event.target.value)}
            />
            <div className="share-user-actions">
                <span className="share-button" onClick={onSubmit}>
                    <Check size={18} />
                </span>
                <span className="unshare-button" onClick={() => removeUser(userId)}>
                    <Trash2 size={18} />
                </span>
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
            <span className="unshare-button" onClick={onSubmit}>
                <Trash2 size={18} />
            </span>
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
