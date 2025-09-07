package com.nemo.rexus.Robot;

import com.nemo.rexus.Commands.StandardCommandType;
import com.nemo.rexus.Sock.Messages.JsonCommand;
import com.nemo.rexus.Sock.Messages.JsonMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class Robot {
    private final int id;
    private final WebSocketSession session;
    private RobotReadyState readyState = RobotReadyState.DISCONNECTED;

    public void sendCommands(List<JsonCommand> commands) throws IOException {
        session.sendMessage(JsonMessage.createMultipleMessages(commands));
    }

    public void sendStop() throws IOException {
        JsonCommand stopCommand = new JsonCommand(StandardCommandType.STOP, Map.of());
        session.sendMessage(stopCommand.toTextMessage());
    }

    public void sendMessage(TextMessage textMessage) throws IOException {
        session.sendMessage(textMessage);
    }

}
