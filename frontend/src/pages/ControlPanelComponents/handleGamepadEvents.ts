import { SendJsonMessage } from "react-use-websocket/dist/lib/types";
import { CommandMessage, CommandType, MessageType } from "./index";

const sendCommand = (
    sendJsonMessage: SendJsonMessage,
    commandMessage: Pick<CommandMessage, "command" | "values">,
) => {
    sendJsonMessage({
        messageType: MessageType.Command,
        command: commandMessage.command,
        values: commandMessage.values ? Object.fromEntries(commandMessage.values.entries()) : undefined,
    });
};

export const getCommands = (sendJsonMessage: SendJsonMessage) => ({
    move: (speed: number, turn: number) => sendCommand(sendJsonMessage, {
        command: CommandType.MOVE,
        values: new Map([
            [ "Speed", speed ],
            [ "Turn", turn ],
        ]),
    }),
    turret: (tilt: number, turn: number) => sendCommand(sendJsonMessage, {
        command: CommandType.TURRET,
        values: new Map([
            [ "Tilt", tilt ],
            [ "Turn", turn ],
        ]),
    }),
    aim: () => sendCommand(sendJsonMessage, { command: CommandType.AIM, values: null }),
    shoot: () => sendCommand(sendJsonMessage, { command: CommandType.SHOOT, values: null }),
    stop: () => sendCommand(sendJsonMessage, { command: CommandType.STOP, values: null }),
});
