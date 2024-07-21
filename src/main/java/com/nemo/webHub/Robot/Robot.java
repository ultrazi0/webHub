package com.nemo.webHub.Robot;

import com.nemo.webHub.Commands.CommandType;
import com.nemo.webHub.Commands.JsonCommand;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public class Robot {
    private final int id;
    private final WebSocketSession session;
    private RobotReadyState readyState = RobotReadyState.DISCONNECTED;

    private final HashMap<CommandType, Map<String, Double>> state = new HashMap<>();

    public Robot(int id, WebSocketSession session) {
        this.id = id;
        this.session = session;

        // TODO: this is a placeholder, Map.of() yields an immutable map - figure something out, me from the future :0
        this.state.put(CommandType.MOVE, Map.of(
                CommandType.MOVE.getKeys()[0].toLowerCase(), 0d,
                CommandType.MOVE.getKeys()[1].toLowerCase(), 0d
        ));

        this.state.put(CommandType.TURRET, Map.of(
                CommandType.TURRET.getKeys()[0].toLowerCase(), 0d,
                CommandType.TURRET.getKeys()[1].toLowerCase(), 0d
        ));

    }

    public int getId() {
        return id;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public RobotReadyState getReadyState() {
        return readyState;
    }

    public void setReadyState(RobotReadyState readyState) {
        this.readyState = readyState;
    }

    public HashMap<CommandType, Map<String, Double>> getState() {
        return state;
    }

    public void setStateField(CommandType command, String key, double value) {
        Map<String, Double> innerMap = state.get(command);

        // If the inner map doesn't exist, create it
        if (innerMap == null) {
            innerMap = new HashMap<>();
            state.put(command, innerMap);
        } else if (!(innerMap instanceof HashMap)) {
            // If the inner map is immutable, create a new mutable map and copy the contents
            Map<String, Double> mutableMap = new HashMap<>(innerMap);
            state.put(command, mutableMap);
            innerMap = mutableMap;
        }

        // Update the inner map with the key-value pair
        innerMap.put(key.toLowerCase(), value);

    }

    public void sendRobotState() throws IOException {
        /* Sends the complete state */

        LinkedList<JsonCommand> updateCommands = new LinkedList<>();

        getState().forEach((command, valuesMap) -> {
            updateCommands.add(new JsonCommand(command, valuesMap));
        });

        session.sendMessage(new TextMessage(JsonCommand.jsonifyMultipleCommands(updateCommands)));
    }

    public void sendRobotState(CommandType commandType) throws IOException {
        /* Sends only one command of the specified type */

        JsonCommand jsonCommand = new JsonCommand(commandType, getState().get(commandType));

        session.sendMessage(new TextMessage(jsonCommand.jsonify()));
    }

    public void sendStop() throws IOException {
        state.forEach((command, values) -> {
            if (values instanceof HashMap<String, Double>) {
                // Map is mutable
                values.replaceAll((key, value) -> 0d);
            } else {
                // Map is immutable
                Map<String, Double> mutableCopy = new HashMap<>(values);
                mutableCopy.replaceAll((key, value) -> 0d);
                state.put(command, mutableCopy);
            }
        });

        JsonCommand stopCommand = new JsonCommand(CommandType.STOP, new HashMap<>());
        session.sendMessage(new TextMessage(stopCommand.jsonify()));
    }

    public void sendMessage(TextMessage textMessage) throws IOException {
        session.sendMessage(textMessage);
    }

}
