import { useEffect, useState } from "react";
import { CommandType, MessageType } from "./index";
import { Button, FormSelect } from "react-bootstrap";
import { SendJsonMessage } from "react-use-websocket/dist/lib/types";

type CommandSelectorProps = {
    selectedCommand: CommandType | null,
    onSelect: (command: CommandType) => void,
}

function CommandSelector({ selectedCommand, onSelect }: CommandSelectorProps) {
    const [commands, setCommands] = useState<readonly CommandType[]>([]);

    useEffect(() => {
        let ignore = false;
        fetch("/api/getAllCommands")
        .then(response => response.json())
        .then(json => {
            if (!ignore) {
                setCommands(json);
            }
        });

        return () => {
            ignore = true;
        };
    }, []);

    return (
        <div className="form-floating">
            <FormSelect
                id="selectCommand"
                defaultValue={selectedCommand ?? undefined}
                onChange={event => onSelect(event.target.value as CommandType)}
            >
                <option></option>
                {commands.map(command => (
                    <option key={command} value={command}>{command}</option>
                ))}
            </FormSelect>
            <label htmlFor="selectCommand">Type</label>
        </div>
    );
}

type ValuesType = readonly string[] | null;

type InputValuesType = {
    [key: string]: number,
}

type CommandValuesProps = {
    values: ValuesType,
    inputValues: InputValuesType,
    onChange: (newInputValues: { [key: string]: number }) => void,
    onSubmit: () => void,
}

function CommandValues({ values, inputValues, onChange, onSubmit }: CommandValuesProps) {

    if (values == null) {
        return null;
    }

    return (<>
        {values.length > 0 ? (
            <table className="table">
                <thead>
                    <tr>
                        <th>Argument/key</th>
                        <th>Value</th>
                    </tr>
                </thead>
                <tbody>
                    {values.map(value => (
                        <tr key={value}>
                            <td>{value}</td>
                            <td>
                                <input
                                    value={inputValues[value] || ""}
                                    onChange={e => onChange({
                                        ...inputValues,
                                        [value]: e.target.value ? Number(e.target.value) : 0,
                                    })}
                                    placeholder="0"
                                    type="number"
                                    className="form-control"
                                />
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        ) : <><br/><br/></>}

        <Button variant="primary" onClick={onSubmit}>Submit</Button>
    </>);
}

export default function Commands({ sendCommand }: { sendCommand: SendJsonMessage }) {
    const [selectedCommand, setSelectedCommand] = useState<CommandType | null>(null);
    const [values, setValues] = useState<ValuesType>(null);
    const [inputValues, setInputValues] = useState<InputValuesType>({});

    function handleCommandSelect(command: CommandType) {
        fetch("/api/commandValues?commandType=" + command)
        .then(response => response.text())
        .then(text => {
            if (text.trim() === "") {
                setValues(null);
                return;
            }

            const json = JSON.parse(text) as readonly string[];
            setValues(json);
            
            const newInputValues: InputValuesType = {};
            json.forEach(element => {
                newInputValues[element] = 0;
            });
            setInputValues(newInputValues);
        });
        setSelectedCommand(command);
    }

    function handleCommandSend() {
        sendCommand({
            "messageType": MessageType.Command,
            "command": selectedCommand,
            "values": inputValues,
        });
    }

    return (
        <form onSubmit={e => e.preventDefault()}>
            <CommandSelector selectedCommand={selectedCommand} onSelect={handleCommandSelect} />
            <CommandValues values={values} inputValues={inputValues} onChange={(newInputValues) => setInputValues(newInputValues)} onSubmit={handleCommandSend} />
        </form>
    );
}
