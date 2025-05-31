import { SendJsonMessage } from "react-use-websocket/dist/lib/types";
import { CommandMessage, CommandType, MessageType } from "./index";

const sendCommand = (
    sendJsonMessage: SendJsonMessage,
    commandMessage: Pick<CommandMessage, "command" | "values">,
) => sendCommands(sendJsonMessage, [ commandMessage ]);

const sendCommands = (
    sendJsonMessage: SendJsonMessage,
    commandMessages: Array<Pick<CommandMessage, "command" | "values">>,
)=> sendJsonMessage(commandMessages.map(commandMessage => ({
        messageType: MessageType.Command,
        command: commandMessage.command,
        values: commandMessage.values ? Object.fromEntries(commandMessage.values.entries()) : undefined,
})));

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
    moveAndTurret: (moveValues: [ number, number ], turnValues: [ number, number ]) => sendCommands(sendJsonMessage, [
        {
            command: CommandType.MOVE,
            values: new Map([
                [ "Speed", moveValues[0] ],
                [ "Turn", moveValues[1] ],
            ]),
        },
        {
            command: CommandType.TURRET,
            values: new Map([
                [ "Tilt", turnValues[0] ],
                [ "Turn", turnValues[1] ],
            ]),
        },
    ]),
    aim: () => sendCommand(sendJsonMessage, { command: CommandType.AIM, values: null }),
    shoot: () => sendCommand(sendJsonMessage, { command: CommandType.SHOOT, values: null }),
    stop: () => sendCommand(sendJsonMessage, { command: CommandType.STOP, values: null }),
});
