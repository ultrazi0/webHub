import { useEffect, useState } from "react";
import { CommandType, CustomCommandType, MessageType, StandardCommand } from "./index";
import { Button, FormSelect, Table } from "react-bootstrap";
import { SendJsonMessage } from "react-use-websocket/dist/lib/types";
import { useNavigate } from "react-router-dom";

import "../../css/Controls.scss";

type CommandSelectorProps = {
    robotId: string | null,
    selectedCommand: CommandType | null,
    onSelect: (command: CommandType | null) => void,
}

function CommandSelector({ robotId, selectedCommand, onSelect }: CommandSelectorProps) {
    const [commands, setCommands] = useState<readonly CommandType[]>([]);
    const navigate = useNavigate();

    useEffect(() => {
        let ignore = false;
        fetch(`/api/robots/${robotId}/commands`)
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else if (response.status === 401) {
                    navigate("/login");
                } else {
                    throw new Error(response.statusText);
                }
            })
            .then((json: readonly CommandType[]) => {
                if (!ignore) {
                    setCommands(json);
                }
            })
            .catch(error => {
                console.error("Error fetching commands:", error);
            });

        return () => {
            ignore = true;
        };
    }, [ navigate, robotId ]);

    return (
        <div className="form-floating">
            <FormSelect
                id="selectCommand"
                defaultValue={selectedCommand?.commandType ?? undefined}
                onChange={event =>
                    onSelect(commands.find(command => command.commandType === event.target.value) ?? null)}
            >
                <option></option>
                {commands.map(command => (
                    <option key={command.commandType} value={command.commandType}>{command.commandType}</option>
                ))}
            </FormSelect>
            <label htmlFor="selectCommand">Type</label>
        </div>
    );
}

type InputValuesType<T extends CommandType> = {
    [K in T["keys"][number]]: number | null;
}

type GenericCommandValuesProp<T extends CommandType> = {
    inputValues: InputValuesType<T> | null,
    onChange: (newInputValues: InputValuesType<T>) => void,
}

type CommandValuesProps = GenericCommandValuesProp<StandardCommand> | GenericCommandValuesProp<CustomCommandType>

function CommandValues({ inputValues, onChange }: CommandValuesProps) {

    if (inputValues == null || Object.keys(inputValues).length < 1) {
        return null;
    }

    return (
        <Table>
            <thead>
                <tr>
                    <th>Argument/key</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
            {Object.entries(inputValues).map((entry) => (
                <tr key={entry[0]}>
                    <td>{entry[0]}</td>
                    <td>
                        <input
                            value={entry[1] ?? ""}
                            onChange={e => onChange({
                                ...inputValues,
                                [entry[0]]: e.target.value ? Number(e.target.value) : 0,
                            })}
                            placeholder="0"
                            type="number"
                            className="form-control"
                        />
                    </td>
                </tr>
            ))}
            </tbody>
        </Table>
    );
}

export default function Commands({ robotId, sendCommand }: { robotId: string | null, sendCommand: SendJsonMessage }) {
    const [ selectedCommand, setSelectedCommand ] = useState<CommandType | null>(null);
    const [ inputValues, setInputValues ] = useState<InputValuesType<CommandType> | null>(null);

    function handleCommandSend() {
        if (selectedCommand != null) {
            sendCommand({
                messageType: MessageType.Command,
                command: selectedCommand.commandType,
                values: inputValues,
            });
        }
    }

    return (
        <>
            <CommandSelector robotId={robotId} selectedCommand={selectedCommand} onSelect={(command) => {
                setSelectedCommand(command);
                setInputValues(command ? getInitialInputValues(command) : null);
            }} />
            <CommandValues inputValues={inputValues} onChange={(newInputValues: InputValuesType<CommandType>) => setInputValues(newInputValues)} />
            <Button className="send-command-button" variant="primary" onClick={handleCommandSend}>Send</Button>
        </>
    );
}

function getInitialInputValues(command: CommandType): InputValuesType<CommandType> {
    const values = {} as InputValuesType<CommandType>;
    for (const key of command.keys) {
        values[key as CommandType["keys"][number]] = null;
    }
    return values;
}