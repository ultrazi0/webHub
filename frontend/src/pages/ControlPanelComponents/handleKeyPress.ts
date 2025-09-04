import { SendJsonMessage } from "react-use-websocket/dist/lib/types";
import { getCommands } from "./handleGamepadEvents";

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

    const commands = getCommands(sendJsonMessage);

    switch (event.key) {
        case "a":
            commands.move(0, keyUp ? 0 : -0.5);
            break;
        case "d":
            commands.move(0, keyUp ? 0 : 0.5);
            break;
        case "w":
            commands.move(keyUp ? 0 : 0.5, 0);
            break;
        case "s":
            commands.move(keyUp ? 0 : -0.5, 0);
            break;
        case "ArrowUp":
            commands.turret(keyUp ? 0 : 1, 0);
            break;
        case "ArrowDown":
            commands.turret(keyUp ? 0 : -1, 0);
            break;
        case "ArrowLeft":
            commands.turret(0, keyUp ? 0 : -1);
            break;
        case "ArrowRight":
            commands.turret(0, keyUp ? 0 : 1);
            break;
        case "Escape":
            commands.stop();
            break;
        default:
            console.log(event.key);
    }
}
