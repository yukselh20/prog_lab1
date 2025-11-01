package client.commands;

import client.forms.TicketForm;
import client.utility.console.Console;
import common.dto.CommandType;
import common.dto.Request;
import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.InvalidFormException;
import common.exceptions.WrongAmountOfElementsException;
import common.models.Ticket;

public class RemoveLowerCommand implements ICommand {
    private final Console console;

    public RemoveLowerCommand(Console console) {
        this.console = console;
    }

    @Override
    public Request build(String args) throws IncorrectInputInScriptException, InvalidFormException, WrongAmountOfElementsException {
        if (!args.isEmpty()) throw new WrongAmountOfElementsException();
        console.println("=> Enter reference Ticket data for comparison:");
        Ticket refTicket = new TicketForm(console).build();
        return new Request(CommandType.REMOVE_LOWER, refTicket);
    }
}