package com.nemo.webHub.Robot;

import com.nemo.webHub.Commands.CommandType;
import com.nemo.webHub.Sock.Messages.JsonCommand;
import com.nemo.webHub.Sock.Messages.JsonMessage;
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
        JsonCommand stopCommand = new JsonCommand(CommandType.STOP, Map.of());
        session.sendMessage(stopCommand.toTextMessage());
    }

    public void sendMessage(TextMessage textMessage) throws IOException {
        session.sendMessage(textMessage);
    }

}
