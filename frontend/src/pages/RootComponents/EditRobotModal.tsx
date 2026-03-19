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
    OverlayTrigger,
    Tooltip,
} from "react-bootstrap";
import { Dispatch, Ref, SetStateAction, useEffect, useRef, useState } from "react";
import { FetcherWithReset } from "../../hooks/useFetcherWithReset";
import { CsrfResponse, Robot } from "../../types";
import { CommandType, StandardCommandTypeEnum } from "../ControlPanelComponents";
import DeleteButton from "../../components/DeleteButton";
import { ChevronRight, Plus } from "lucide-react";
import { setError } from "../../utils/validationUtils";
import { v4 as randomUUID } from "uuid";

import "../../css/EditRobotModal.scss";
import FormButton from "../../components/FormButton";

type CommandIdType = string;

type CommandWithId = Pick<CommandType, "commandType"> & {
    id: CommandIdType,
    keys: {
        id: CommandIdType,
        key: string,
    }[],
    isSaved: boolean,
}

type EditRobotModalProps = {
    fetcher: FetcherWithReset<boolean>,
    robotId: number | null,
    setRobotId: (robotId: number | null) => void,
    csrfToken: CsrfResponse,
}

type FormErrors = {
    name?: string
    commands?: Record<CommandIdType, { commandType?: string, keys?: Record<CommandIdType, string> } | undefined>
}

export default function EditRobotModal({ fetcher, robotId, setRobotId, csrfToken }: EditRobotModalProps) {
    const [ robot, setRobot ] = useState<Robot | null>(null);
    const [ robotName, setRobotName ] = useState<string | null>(null);
    const [ commands, setCommands ] = useState<CommandWithId[]>(() => getDefaultCommands(robot));

    const validateForm = () => {
        const errors: FormErrors = {};
        if ((robotName?.trim() ?? "") === "") {
            setError(errors, "name", "Robot name must not be empty");
        }
        for (const command of commands) {
            if (command.commandType.trim() === "") {
                setError(errors, `commands.${command.id}.commandType`, "Command must not be blank");
            } else if (commands.filter(c => c.commandType === command.commandType).length > 1) {
                setError(errors, `commands.${command.id}.commandType`, "Commands must be unique");
            }
            for (const key of command.keys) {
                if (command.keys.filter(k => k.key === key.key).length > 1) {
                    setError(errors, `commands.${command.id}.keys.${key.id}`, "Keys must be unique");
                }
            }
        }

        return errors;
    };

    const errors = validateForm();

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
            .then((json: Robot) => {
                if (!ignore) {
                    setRobot(json);
                    setRobotName(json.name);
                    setCommands(getDefaultCommands(json));
                }
            })
            .catch(error => {
                console.error(error);
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
            <fetcher.Form onSubmit={event => {
                event.preventDefault();

                fetcher.submit(
                    {
                        name: robotName,
                        commands: commands.map(c => ({ ...c, keys: c.keys.map(k => k.key) })),
                        csrf: csrfToken,
                    },
                    { method: "PUT", action: `/edit/${robotId}`, encType: "application/json" },
                );
            }}>
                <ModalBody>
                    <FormGroup className="mb-3" controlId="formName">
                        <FormLabel>Robot name</FormLabel>
                        <FormControl
                            type="text"
                            placeholder="Enter robot name"
                            name="name"
                            defaultValue={robotName ?? ""}
                            onChange={event => setRobotName(event.target.value)}
                            isInvalid={errors.name !== undefined || fetcher.data === false}
                        />
                        {fetcher.data === false && (
                            <FormText className="text-danger-emphasis">This name is already taken</FormText>
                        )}
                        {errors.name !== undefined && (
                            <FormText className="text-danger-emphasis">{errors.name}</FormText>
                        )}
                    </FormGroup>
                    <hr />
                    <div>
                        <FormLabel>Commands</FormLabel>
                        <RobotCommandsBlock
                            robot={robot}
                            commands={commands}
                            setCommands={setCommands}
                            errors={errors}
                        />
                    </div>
                </ModalBody>
                <ModalFooter>
                    <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
                    <FormButton isLoading={fetcher.state !== "idle"} disabled={Object.keys(errors).length > 0}>Save</FormButton>
                </ModalFooter>
            </fetcher.Form>
        </Modal>
    );
}

function RobotCommandsBlock({ robot, commands, setCommands, errors }: {
    robot: Robot | null,
    commands: CommandWithId[],
    setCommands: Dispatch<SetStateAction<CommandWithId[]>>,
    errors: FormErrors,
}) {

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
                            variant={errors.commands?.[command.id] ? "danger" : undefined}
                        >
                            <span>{command.commandType}</span>
                            {(!command.isSaved || !Object.keys(StandardCommandTypeEnum).includes(command.commandType)) && (
                                <DeleteButton
                                    onClick={() => setCommands(commands.filter(it => it.id !== command.id))}
                                />
                            )}
                        </ListGroup.Item>
                    ))}
                </ListGroup>
                <Button variant="outline-success" size="sm" className="add-command-type-button mt-2" onClick={() => {
                    const id = randomUUID();
                    setCommands([ ...commands, {
                        id: id,
                        commandType: "",
                        keys: [],
                        isSaved: false,
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
                errors={errors}
            />
        </div>
    );
}

function RobotCommandForm({ command, clearSelectedCommand, updateCommand, commandTypeInputRef, errors }: {
    command: CommandWithId | null,
    clearSelectedCommand: () => void,
    updateCommand: (command: CommandWithId) => void,
    commandTypeInputRef: Ref<HTMLInputElement>,
    errors: FormErrors,
}) {
    const isStandardCommand = command ? command.isSaved && Object.keys(StandardCommandTypeEnum).includes(command.commandType) : undefined;

    return (
        <div className="robot-command-edit-form">
            <div className="robot-command-edit-form-body">
                <FormGroup>
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
                        isInvalid={!!command?.id && errors.commands?.[command.id]?.commandType !== undefined}
                    />
                    {command?.id && errors.commands?.[command.id]?.commandType !== undefined && (
                        <FormText className="text-danger-emphasis">{errors.commands[command?.id]?.commandType}</FormText>
                    )}
                </FormGroup>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", margin: "0.5rem 0" }}>
                    <div>Keys:</div>
                    {!isStandardCommand && (
                        <Button
                            id="robot-command-edit-form-add-key-button"
                            variant="outline-primary"
                            size="sm"
                            onClick={() => {
                                if (command) {
                                    updateCommand({
                                        ...command,
                                        keys: [ ...command.keys, { id: randomUUID(), key: "" } ],
                                    });
                                }
                            }}
                        >
                            <Plus size={20}/>
                        </Button>
                    )}
                </div>
                <ListGroup variant="flush" className="robot-command-edit-form-body-keys-list">
                    {command?.keys.map(key => (
                        <ListGroup.Item key={key.id}>
                            <FieldWithTooltipError placement="bottom" errorMessage={errors.commands?.[command?.id]?.keys?.[key.id]}>
                                <FormControl
                                    size="sm"
                                    type="text"
                                    placeholder="Key"
                                    value={key.key}
                                    isInvalid={errors.commands?.[command?.id]?.keys?.[key.id] !== undefined}
                                    onChange={event => {
                                        if (command) {
                                            updateCommand({
                                                ...command,
                                                keys: command.keys.map(k => key.id === k.id ? { id: k.id, key: event.target.value } : k),
                                            });
                                        }
                                    }}
                                    disabled={isStandardCommand ?? true}
                                />
                            </FieldWithTooltipError>
                            {!isStandardCommand && (
                                <DeleteButton
                                    onClick={() => {
                                        if (command) {
                                            updateCommand({
                                                ...command,
                                                keys: command.keys.flatMap(k => key.id === k.id ? [] : [ k ]),
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

const FieldWithTooltipError = ({ errorMessage, children, ...props }: {
    errorMessage: string | undefined
} & Omit<Parameters<typeof OverlayTrigger>[0], "overlay">) => {
    if (errorMessage === undefined) {
        if (typeof children === "function") {
            return children({ ref: null }) ?? null;
        }
        return children;
    }

    return (
        <OverlayTrigger overlay={<Tooltip className="error-tooltip">{errorMessage}</Tooltip>} {...props}>
            {children}
        </OverlayTrigger>
    );
};

const getDefaultCommands = (robot: Robot | null): CommandWithId[] =>
    (robot?.commands ?? []).sort((a, b) => {
        if (Object.keys(StandardCommandTypeEnum).includes(a.commandType)) {
            if (!Object.keys(StandardCommandTypeEnum).includes(b.commandType)) {
                return -1;
            }
        } else if (Object.keys(StandardCommandTypeEnum).includes(b.commandType)) {
            return 1;
        }
        return a.commandType.localeCompare(b.commandType);
    }).map(command => ({
        ...command,
        id: randomUUID(),
        keys: command.keys.map(key => ({ id: randomUUID(), key: key })),
        isSaved: true,
    }));