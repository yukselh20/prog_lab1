package client.commands;

import client.forms.TicketForm;
import client.utility.console.Console;
import common.dto.CommandType;
import common.dto.Request;
import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.InvalidFormException;
import common.exceptions.WrongAmountOfElementsException;
import common.models.Ticket;

public class ReplaceIfLowerCommand implements ICommand {
    private final Console console;

    public ReplaceIfLowerCommand(Console console) {
        this.console = console;
    }

    @Override
    public Request build(String args) throws IncorrectInputInScriptException, InvalidFormException, WrongAmountOfElementsException {
        if (args.isEmpty()) throw new WrongAmountOfElementsException();
        try {
            long key = Long.parseLong(args);
            console.println("=> Enter Ticket data for replacement (key: " + key + "):");
            Ticket ticket = new TicketForm(console).build();
            return new Request(CommandType.REPLACE_IF_LOWER, args, ticket);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Key must be a number.");
        }
    }
}