import {
    Accordion,
    Button,
    FormControl,
    FormGroup,
    FormLabel,
    FormText,
    Modal,
    ModalBody,
    ModalFooter,
    ModalHeader,
    ModalTitle,
} from "react-bootstrap";
import { useEffect, useState } from "react";
import CsrfHiddenInput from "../../components/CsrfHiddenInput";
import { FetcherWithReset } from "../../hooks/useFetcherWithReset";
import { CsrfResponse, Robot } from "../../types";
import { CommandType } from "../ControlPanelComponents";

import "../../css/EditRobotModal.scss";
import DeleteButton from "../../components/DeleteButton";
import { Pencil, Plus } from "lucide-react";

type EditRobotModalProps = {
    fetcher: FetcherWithReset<boolean>,
    robotId: number | null,
    setRobotId: (robotId: number | null) => void,
    csrfToken: CsrfResponse,
}

export default function EditRobotModal({ fetcher, robotId, setRobotId, csrfToken }: EditRobotModalProps) {
    const [robot, setRobot] = useState<Robot | null>(null);

    const handleCloseModal = () => {
        setRobotId(null);
        setRobot(null);
        fetcher.reset();
    };

    useEffect(() => {
        let ignore = false;
        if (robotId != null) {
            fetch("/api/robots/" + robotId)
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error("No robot with this ID or bad request");
            })
            .then(json => {
                if (!ignore) {
                    setRobot(json);
                }
            })
            .catch(error => {
                console.log(error);
            });
        } else {
            setRobot(null);
        }

        return () => {
            ignore = true;
        };
    }, [ robotId ]);

    return (
        <Modal show={!!robot} onHide={handleCloseModal} backdrop="static" keyboard={true}>
            <ModalHeader closeButton>
                <ModalTitle>Edit robot</ModalTitle>
            </ModalHeader>
            <fetcher.Form method="put" action={"/edit/" + robotId}>
                <ModalBody>
                    <FormGroup className="mb-3" controlId="formName">
                        <FormLabel>Robot name</FormLabel>
                        <FormControl
                            type="text"
                            placeholder="Enter robot name"
                            name="name"
                            defaultValue={robot ? robot.name : ""}
                        />
                        {fetcher.data === false && (
                            <FormText className="text-danger-emphasis">This name is already taken</FormText>
                        )}
                        {csrfToken && <CsrfHiddenInput csrfToken={csrfToken} />}
                    </FormGroup>
                    <hr />
                    <RobotCommandsForm />
                </ModalBody>
                <ModalFooter>
                    <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
                    <Button type="submit" variant="primary">Save</Button>
                </ModalFooter>
            </fetcher.Form>
        </Modal>
    );
}

function RobotCommandsForm() {
    return (
        <div className="robot-commands-form">
            <Accordion alwaysOpen>
                {commands.map(command => (
                    <CommandTypeItem command={command} key={command.commandType} />
                ))}
            </Accordion>
            <Button variant="outline-primary" className="add-command-type-button" onClick={() => commands.push({ commandType: "new command type", keys: [ "new key" ] })}>
                <Plus size={18} />
            </Button>
        </div>
    );
}

function CommandTypeItem({ command }: { command: CommandType }) {

    return (
        <Accordion.Item eventKey={command.commandType} className="command-type-item">
            <Accordion.Header>
                <span>{command.commandType}</span>
            </Accordion.Header>
            <Accordion.Body className="command-type-details">
                <div className="command-type-keys">
                    {command.keys.join(", ")}
                </div>
                <span>
                    <span className="webHub-button command-type-edit-button">
                        <Pencil size={18} />
                    </span>
                    <DeleteButton onClick={() => console.log(command.commandType)} />
                </span>
            </Accordion.Body>
        </Accordion.Item>
    );
}

const commands = [
    {
        commandType: "Forward",
        keys: [ "W", "Up" ],
    },
    {
        commandType: "Backward",
        keys: [ "S", "Down" ],
    },
];
