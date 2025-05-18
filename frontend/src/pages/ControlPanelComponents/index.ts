export enum MessageType {
    RegularMessage = "REGULAR_MESSAGE",
    Command = "COMMAND",
    Feedback = "FEEDBACK",
    Image = "IMAGE",
    AimImage = "AIM_IMAGE",
}

export enum CommandType {
    MOVE = "MOVE",
    TURRET = "TURRET",
    AIM = "AIM",
    SHOOT = "SHOOT",
    STOP = "STOP",
}

interface JsonMessage<T extends MessageType> {
    messageType: T;
}

export type RegularMessage = JsonMessage<MessageType.RegularMessage> & {
    message: string;
};

export type CommandMessage = JsonMessage<MessageType.Command> & {
    command: CommandType;
    values: Map<string, number> | null;
};

export type FeedbackMessage = JsonMessage<MessageType.Feedback> & {
    feedback: string;
};

export type ImageMessage = JsonMessage<MessageType.Image> & {
    image: string;
};

export type AimImageMessage = JsonMessage<MessageType.AimImage> & {
    image: string;
}
