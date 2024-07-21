package com.nemo.webHub.Commands;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Command {  // TODO: maybe could be rewritten as a record + JsonCommand could extend it
    private CommandType commandType;
    private Map<String, Double> values = null;

    private List<CommandRow> valueRows = new LinkedList<>();

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public Map<String, Double> getValues() {
        return values;
    }

    public void setValues(Map<String, Double> values) {
        this.values = values;
    }

    public void addValue(String key, Double value) {
        this.values.put(key, value);
    }

    public List<CommandRow> getValueRows() {
        return valueRows;
    }

    public void setValueRows(List<CommandRow> valueRows) {
        this.valueRows = valueRows;
    }

    public void addValueRow(CommandRow row) {
        this.valueRows.add(row);
    }

    public JsonCommand createJsonCommand() {
        return new JsonCommand(this.commandType, this.values);
    }
}
