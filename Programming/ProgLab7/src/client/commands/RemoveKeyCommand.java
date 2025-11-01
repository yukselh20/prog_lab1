package client.commands;

import common.dto.CommandType;
import common.dto.Request;
import common.exceptions.WrongAmountOfElementsException;

public class RemoveKeyCommand implements ICommand {
    @Override
    public Request build(String args) throws WrongAmountOfElementsException {
        if (args.isEmpty()) throw new WrongAmountOfElementsException();
        try {
            Long.parseLong(args); // Basic validation
            return new Request(CommandType.REMOVE_KEY, args);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Key must be a number.");
        }
    }
}