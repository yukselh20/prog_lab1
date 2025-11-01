package client.commands;

import common.dto.CommandType;
import common.dto.Request;
import common.exceptions.WrongAmountOfElementsException;

public class CountGreaterThanDiscountCommand implements ICommand {
    @Override
    public Request build(String args) throws WrongAmountOfElementsException {
        if (args.isEmpty()) throw new WrongAmountOfElementsException();
        try {
            Long.parseLong(args);
            return new Request(CommandType.COUNT_GREATER_THAN_DISCOUNT, args);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Numeric argument expected.");
        }
    }
}