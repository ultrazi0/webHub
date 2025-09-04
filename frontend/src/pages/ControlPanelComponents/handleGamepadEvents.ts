import { SendJsonMessage } from "react-use-websocket/dist/lib/types";
import {
    sendCommand,
    sendCommands,
    StandardCommand,
    StandardCommandTypeEnum,
} from "./index";

export const getCommands = (sendJsonMessage: SendJsonMessage) => ({
    move: (speed: number, turn: number) => sendCommand<StandardCommand>(sendJsonMessage, {
        command: StandardCommandTypeEnum.MOVE,
        values: {
            Speed: speed,
            Turn: turn,
        },
    }),
    turret: (tilt: number, turn: number) => sendCommand<StandardCommand>(sendJsonMessage, {
        command: StandardCommandTypeEnum.TURRET,
        values: {
            Tilt: tilt,
            Turn: turn,
        },
    }),
    moveAndTurret: (moveValues: [ number, number ], turnValues: [ number, number ]) => sendCommands<StandardCommand>(sendJsonMessage, [
        {
            command: StandardCommandTypeEnum.MOVE,
            values: {
                Speed: moveValues[0],
                Turn: moveValues[1],
            },
        },
        {
            command: StandardCommandTypeEnum.TURRET_CONTINUOUS,
            values: {
                Tilt: turnValues[0],
                Turn: turnValues[1],
            },
        },
    ]),
    aim: () => sendCommand(sendJsonMessage, { command: StandardCommandTypeEnum.AIM, values: null }),
    shoot: () => sendCommand(sendJsonMessage, { command: StandardCommandTypeEnum.SHOOT, values: null }),
    stop: () => sendCommand(sendJsonMessage, { command: StandardCommandTypeEnum.STOP, values: null }),
});
