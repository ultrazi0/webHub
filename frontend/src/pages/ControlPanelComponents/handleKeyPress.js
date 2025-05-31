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

    const sendCommand = (commandType, values) => sendJsonMessage({
        "messageType": "COMMAND",
        "command": commandType,
        "values": values,
    });

    switch (event.key) {
        case "a":
            sendCommand("MOVE", { turn: keyUp ? 0 : -0.5, speed: 0 });
            break;
        case "d":
            sendCommand("MOVE", { turn: keyUp ? 0 : 0.5, speed: 0 });
            break;
        case "w":
            sendCommand("MOVE", { speed: keyUp ? 0 : 0.5, turn: 0});
            break;
        case "s":
            sendCommand("MOVE", { speed: keyUp ? 0 : -0.5, turn: 0 });
            break;
        case "ArrowUp":
            sendCommand("TURRET", { tilt: keyUp ? 0 : 1, turn: 0 });
            break;
        case "ArrowDown":
            sendCommand("TURRET", { tilt: keyUp ? 0 : -1, turn: 0});
            break;
        case "ArrowLeft":
            sendCommand("TURRET", { turn: keyUp ? 0 : -1, tilt: 0 });
            break;
        case "ArrowRight":
            sendCommand("TURRET", { turn: keyUp ? 0 : 1, tilt: 0 });
            break;
        case "Escape":
            sendJsonMessage({
                "messageType": "COMMAND",
                "command": "STOP",
            });
            break;
        default:
            console.log(event.key);
    }
}
