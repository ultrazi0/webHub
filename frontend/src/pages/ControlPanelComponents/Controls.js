import { useEffect, useState } from "react";

function CommandSelector( {selectedCommand, onSelect} ) {
    const [commands, setCommands] = useState([]);

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
            <select className="form-select" id="selectCommand" defaultValue={selectedCommand} onChange={e => onSelect(e.target.value)}>
                <option value={null}></option>
                {commands.map(command => (
                    <option key={command} value={command}>{command}</option>
                ))}
            </select>
            <label htmlFor="selectCommand">Type</label>
        </div>
    );
}

function CommandValues( {values, inputValues, onChange, onSubmit} ) {

    if (values == null) {
        return null;
    }

    return (<>
        {values.length > 0 ? <table className="table">
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
                            <input value={inputValues[value] || ""} onChange={e => onChange({
                                ...inputValues,
                                [value]: e.target.value ? e.target.value : 0,
                            })} placeholder="0" type="number" className="form-control" />
                        </td>
                    </tr>
                ))}
            </tbody>
        </table> : <><br/><br/></>}
        
        <button className="btn btn-primary" onClick={onSubmit} type="submit">Submit</button>
    </>);
}

export default function Commands( {sendCommand} ) {
    const [selectedCommand, setSelectedCommand] = useState(null);
    const [values, setValues] = useState(null);
    const [inputValues, setInputValues] = useState({});

    function handleCommandSelect(command) {
        fetch("/api/commandValues?commandType=" + command)
        .then(response => response.text())
        .then(text => {
            if (text.trim() === "") {
                setValues(null);
                return;
            }

            const json = JSON.parse(text);
            setValues(json);
            
            let newInputValues = {};
            json.forEach(element => {
                newInputValues[element] = 0;
            });
            setInputValues(newInputValues);
        });
        setSelectedCommand(command);
    }

    function handleCommandSend() {
        sendCommand({
            "messageType": "COMMAND",
            "command": selectedCommand,
            "values": inputValues,
        });
    }

    return (
        <form onSubmit={e => e.preventDefault()}>
            <CommandSelector onSelect={handleCommandSelect} />
            <CommandValues values={values} inputValues={inputValues} onChange={(newInputValues) => setInputValues(newInputValues)} onSubmit={handleCommandSend} />
        </form>
    );
}
