import { SendJsonMessage } from "react-use-websocket/dist/lib/types";
import { CommandType, MessageType } from "./index";

export default function handleKeyPress(event: KeyboardEvent, sendJsonMessage: SendJsonMessage) {
    if ((document.activeElement?.tagName === "INPUT") || (document.activeElement?.tagName === "TEXTAREA")) {
        return;
    }

    handleKey(event, sendJsonMessage);

}

function handleKey(event: KeyboardEvent, sendJsonMessage: SendJsonMessage) {
    let keyUp = false;
    if (event.type === "keyup") {
        keyUp = true;
    }

    const sendCommand = (commandType: CommandType, values: { [ key: string ]: number }) => sendJsonMessage({
        "messageType": MessageType.Command,
        "command": commandType,
        "values": values,
    });

    switch (event.key) {
        case "a":
            sendCommand(CommandType.MOVE, { turn: keyUp ? 0 : -0.5, speed: 0 });
            break;
        case "d":
            sendCommand(CommandType.MOVE, { turn: keyUp ? 0 : 0.5, speed: 0 });
            break;
        case "w":
            sendCommand(CommandType.MOVE, { speed: keyUp ? 0 : 0.5, turn: 0});
            break;
        case "s":
            sendCommand(CommandType.MOVE, { speed: keyUp ? 0 : -0.5, turn: 0 });
            break;
        case "ArrowUp":
            sendCommand(CommandType.TURRET, { tilt: keyUp ? 0 : 1, turn: 0 });
            break;
        case "ArrowDown":
            sendCommand(CommandType.TURRET, { tilt: keyUp ? 0 : -1, turn: 0});
            break;
        case "ArrowLeft":
            sendCommand(CommandType.TURRET, { turn: keyUp ? 0 : -1, tilt: 0 });
            break;
        case "ArrowRight":
            sendCommand(CommandType.TURRET, { turn: keyUp ? 0 : 1, tilt: 0 });
            break;
        case "Escape":
            sendCommand(CommandType.STOP, {});
            break;
        default:
            console.log(event.key);
    }
}
