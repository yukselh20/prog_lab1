package client.commands;

import common.dto.CommandType;
import common.dto.Request;
import common.exceptions.WrongAmountOfElementsException;

public class InfoCommand implements ICommand {
    @Override
    public Request build(String args) throws WrongAmountOfElementsException {
        if (!args.isEmpty()) throw new WrongAmountOfElementsException();
        return new Request(CommandType.INFO);
    }
}