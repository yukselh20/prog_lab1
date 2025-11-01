package client.commands;

import common.dto.CommandType;
import common.dto.Request;
import common.exceptions.WrongAmountOfElementsException;

public class CountGreaterThanDiscountCommand implements ICommand {
    @Override
    public Request build(String args) throws WrongAmountOfElementsException {
        if (args.isEmpty()) throw new WrongAmountOfElementsException();
        // Basic validation, server will do the full parsing
        try {
            Long.parseLong(args);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Numeric argument expected.");
        }
        return new Request(CommandType.COUNT_GREATER_THAN_DISCOUNT, args);
    }
}