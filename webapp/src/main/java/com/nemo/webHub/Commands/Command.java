package com.nemo.webHub.Commands;

import com.nemo.webHub.Sock.Messages.JsonCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Deprecated
public class Command {  // TODO: maybe could be rewritten as a record + JsonCommand could extend it
    private CommandType commandType;
    private Map<String, Double> values = null;

    private List<CommandRow> valueRows = new LinkedList<>();

    public void addValue(String key, Double value) {
        this.values.put(key, value);
    }

    public void addValueRow(CommandRow row) {
        this.valueRows.add(row);
    }

    public JsonCommand createJsonCommand() {
        return new JsonCommand(this.commandType, this.values);
    }
}
