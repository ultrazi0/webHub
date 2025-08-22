import {
    Button,
    FormControl,
    FormGroup,
    FormLabel,
    FormText,
    ListGroup,
    Modal,
    ModalBody,
    ModalFooter,
    ModalHeader,
    ModalTitle,
} from "react-bootstrap";
import { Ref, useEffect, useRef, useState } from "react";
import CsrfHiddenInput from "../../components/CsrfHiddenInput";
import { FetcherWithReset } from "../../hooks/useFetcherWithReset";
import { CsrfResponse, Robot } from "../../types";
import { CommandType, StandardCommandTypeEnum } from "../ControlPanelComponents";

import "../../css/EditRobotModal.scss";
import DeleteButton from "../../components/DeleteButton";
import { ChevronRight, Plus } from "lucide-react";

type CommandIdType = ReturnType<Crypto["randomUUID"]>;

type CommandWithId = CommandType & {
    id: CommandIdType,
}

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
        <Modal
            show={!!robot}
            onHide={handleCloseModal}
            backdrop="static"
            keyboard
        >
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
                    <div>
                        <FormLabel>Commands</FormLabel>
                        <RobotCommandsBlock robot={robot} />
                    </div>
                </ModalBody>
                <ModalFooter>
                    <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
                    <Button type="submit" variant="primary">Save</Button>
                </ModalFooter>
            </fetcher.Form>
        </Modal>
    );
}

function RobotCommandsBlock({ robot }: {
    robot: Robot | null
}) {
    const [ commands, setCommands ] = useState<CommandWithId[]>(() =>
        (robot?.commands ?? []).map(command => ({
            ...command,
            id: self.crypto.randomUUID(),
        })),
    );
    const [commandFormOpen, setCommandFormOpen] = useState(false);
    const [selectedCommandId, setSelectedCommandId] = useState<CommandIdType | null>(null);
    const commandTypeInputRef = useRef<HTMLInputElement>(null);

    const selectedCommand = commands.find(command => command.id === selectedCommandId) ?? null;

    if (!robot) {
        return null;
    }

    return (
        <div className={`robot-commands-block ${commandFormOpen && selectedCommand !== null ? "expanded" : ""}`}>
            <div className="robot-commands-details-list">
                <ListGroup>
                    {commands.map((command: CommandWithId) => (
                        <ListGroup.Item
                            action
                            key={command.id}
                            onClick={(event) => {
                                event.preventDefault();
                                setSelectedCommandId(command.id);
                                setCommandFormOpen(true);
                            }}
                            style={{ display: "flex", justifyContent: "space-between" }}
                            className={commandFormOpen && command.id === selectedCommandId ? "selected" : undefined}
                        >
                            <span>{command.commandType}</span>
                            {!Object.keys(StandardCommandTypeEnum).includes(command.commandType) && (
                                <DeleteButton
                                    onClick={() => setCommands(commands.filter(it => it.id !== command.id))}
                                />
                            )}
                        </ListGroup.Item>
                    ))}
                </ListGroup>
                <Button variant="outline-success" size="sm" className="add-command-type-button mt-2" onClick={() => {
                    const id = self.crypto.randomUUID();
                    setCommands([ ...commands, {
                        id: id,
                        commandType: "",
                        keys: [],
                    } ]);
                    setSelectedCommandId(id);
                    setCommandFormOpen(true);
                    if (commandTypeInputRef?.current?.disabled) {
                        commandTypeInputRef.current.disabled = false;
                    }
                    commandTypeInputRef?.current?.focus();
                }}>
                    <Plus size={20} />
                </Button>
            </div>
            <RobotCommandForm
                command={selectedCommand}
                clearSelectedCommand={() => setCommandFormOpen(false)}
                updateCommand={command => setCommands(prevCommands => prevCommands.map(c => c.id === command.id ? command : c))}
                commandTypeInputRef={commandTypeInputRef}
            />
        </div>
    );
}

function RobotCommandForm({ command, clearSelectedCommand, updateCommand, commandTypeInputRef }: {
    command: CommandWithId | null,
    clearSelectedCommand: () => void,
    updateCommand: (command: CommandWithId) => void,
    commandTypeInputRef: Ref<HTMLInputElement>,
}) {
    const isStandardCommand = command ? Object.keys(StandardCommandTypeEnum).includes(command.commandType) : undefined;

    return (
        <div className="robot-command-edit-form">
            <div className="robot-command-edit-form-body">
                <FormControl
                    size="sm"
                    type="text"
                    placeholder="Command"
                    ref={commandTypeInputRef}
                    value={command?.commandType ?? ""}
                    onChange={event => {
                        if (command) {
                            updateCommand({
                                ...command,
                                commandType: event.target.value,
                            });
                        }
                    }}
                    disabled={isStandardCommand ?? true}
                />
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", margin: "0.5rem 0" }}>
                    <div>Keys:</div>
                    {!isStandardCommand && (
                        <Button
                            variant="outline-primary"
                            size="sm"
                            onClick={() => {
                                if (command) {
                                    updateCommand({
                                        ...command,
                                        keys: [ ...command.keys, "" ],
                                    });
                                }
                            }}
                        >
                            <Plus size={20}/>
                        </Button>
                    )}
                </div>
                <ListGroup variant="flush" className="robot-command-edit-form-body-keys-list">
                    {command?.keys.map((key, index) => (
                        <ListGroup.Item key={index}>
                            {/*TODO: should not use index as key*/}
                            <FormControl
                                size="sm"
                                type="text"
                                placeholder="Key"
                                value={key}
                                onChange={event => {
                                    if (command) {
                                        updateCommand({
                                            ...command,
                                            keys: command.keys.map((k, keyIndex) => index === keyIndex ? event.target.value : k),
                                        });
                                    }
                                }}
                                disabled={isStandardCommand ?? true}
                            />
                            {!isStandardCommand && (
                                <DeleteButton
                                    onClick={() => {
                                        if (command) {
                                            updateCommand({
                                                ...command,
                                                keys: command.keys.flatMap((k, kIndex) => index === kIndex ? [] : [ k ]),
                                            });
                                        }
                                    }}
                                />
                            )}
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            </div>
            <div className="robot-command-edit-form-footer">
                <Button variant="outline-primary" size="sm" onClick={clearSelectedCommand}><ChevronRight size={20} /></Button>
            </div>
        </div>
    );
}
