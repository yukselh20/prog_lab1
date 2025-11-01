package client.commands;

import common.dto.CommandType;
import common.dto.Request;
import common.exceptions.WrongAmountOfElementsException;

public class FilterStartsWithNameCommand implements ICommand {
    @Override
    public Request build(String args) throws WrongAmountOfElementsException {
        if (args.isEmpty()) throw new WrongAmountOfElementsException();
        return new Request(CommandType.FILTER_STARTS_WITH_NAME, args);
    }
}