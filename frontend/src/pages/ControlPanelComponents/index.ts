import { SendJsonMessage } from "react-use-websocket/dist/lib/types";

export enum MessageType {
    RegularMessage = "REGULAR_MESSAGE",
    Command = "COMMAND",
    Feedback = "FEEDBACK",
    Image = "IMAGE",
    AimImage = "AIM_IMAGE",
}

export enum StandardCommandTypeEnum {
    MOVE = "MOVE",
    TURRET = "TURRET",
    TURRET_CONTINUOUS = "TURRET_CONTINUOUS",
    AIM = "AIM",
    SHOOT = "SHOOT",
    STOP = "STOP",
}

type StandardCommandMapping = {
    readonly [StandardCommandTypeEnum.MOVE]: readonly [ "Speed", "Turn" ],
    readonly [StandardCommandTypeEnum.TURRET]: readonly [ "Tilt", "Turn" ],
    readonly [StandardCommandTypeEnum.TURRET_CONTINUOUS]: readonly [ "Tilt", "Turn" ],
    readonly [StandardCommandTypeEnum.AIM]: readonly [],
    readonly [StandardCommandTypeEnum.SHOOT]: readonly [],
    readonly [StandardCommandTypeEnum.STOP]: readonly [],
}

export type StandardCommand = {
    [K in keyof StandardCommandMapping]: {
        commandType: K;
        keys: StandardCommandMapping[K];
    }
}[keyof StandardCommandMapping]

export type CustomCommandType = {
    commandType: string,
    keys: readonly string[]
};

export type CommandType = StandardCommand | CustomCommandType;

interface JsonMessage<T extends MessageType> {
    messageType: T;
}

export type RegularMessage = JsonMessage<MessageType.RegularMessage> & {
    message: string;
};

export type CommandMessage<T extends CommandType = StandardCommand> = JsonMessage<MessageType.Command> & (T extends StandardCommand ? {
    [K in StandardCommandTypeEnum]: {
        command: K;
        values: Record<StandardCommandMapping[K][number], number> | null;
    }
}[keyof StandardCommandMapping] : {
    command: T["commandType"];
    values: Record<T["keys"][number], number> | null;
});

export type FeedbackMessage = JsonMessage<MessageType.Feedback> & {
    feedback: string;
};

export type ImageMessage = JsonMessage<MessageType.Image> & {
    image: string;
};

export type AimImageMessage = JsonMessage<MessageType.AimImage> & {
    image: string;
}

export const sendCommand = <T extends CommandType>(
    sendJsonMessage: SendJsonMessage,
    commandMessage: Omit<CommandMessage<T>, "messageType">,
) => sendCommands(sendJsonMessage, [ commandMessage ]);

export const sendCommands = <T extends CommandType>(
    sendJsonMessage: SendJsonMessage,
    commandMessages: Array<Omit<CommandMessage<T>, "messageType">>,
)=> sendJsonMessage(commandMessages.map(commandMessage => ({
    messageType: MessageType.Command,
    command: commandMessage.command,
    values: commandMessage.values,
})));
