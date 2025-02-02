export default function handleKeyPress(event, sendJsonMessage) {
    if ((document.activeElement.tagName === "INPUT") || (document.activeElement.tagName === "TEXTAREA")) {
        return;
    }

    handleKey(event, sendJsonMessage);

}

function handleKey(event, sendJsonMessage) {
    let keyUp = false;
    if (event.type === "keyup") {
        keyUp = true;
    }

    const sendCommand = (commandType, valueType, value) => sendJsonMessage({
        "messageType": "command",
        "command": commandType,
        "values": {
            [valueType]: value
        }
    });

    switch (event.key) {
        case 'a':
            sendCommand("MOVE", "turn", keyUp ? 0 : -0.5);
            break;
        case 'd':
            sendCommand("MOVE", "turn", keyUp ? 0 : 0.5);
            break;
        case 'w':
            sendCommand("MOVE", "speed", keyUp ? 0 : 0.5);
            break;
        case 's':
            sendCommand("MOVE", "speed", keyUp ? 0 : -0.5);
            break;
        case 'ArrowUp':
            sendCommand("TURRET", "tilt", keyUp ? 0 : 1);
            break;
        case 'ArrowDown':
            sendCommand("TURRET", "tilt", keyUp ? 0 : -1);
            break;
        case 'ArrowLeft':
            sendCommand("TURRET", "turn", keyUp ? 0 : -1);
            break;
        case 'ArrowRight':
            sendCommand("TURRET", "turn", keyUp ? 0 : 1);
            break;
        case "Escape":
            sendJsonMessage({
                "messageType": "command",
                "command": "STOP"
            });
            break;
        default:
            console.log(event.key);
    }
}
