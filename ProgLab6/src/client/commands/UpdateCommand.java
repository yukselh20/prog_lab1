package client.commands;

import client.forms.TicketForm;
import client.utility.console.Console;
import common.dto.CommandType;
import common.dto.Request;
import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.InvalidFormException;
import common.exceptions.WrongAmountOfElementsException;
import common.models.Ticket;

public class UpdateCommand implements ICommand {
    private final Console console;

    public UpdateCommand(Console console) {
        this.console = console;
    }

    @Override
    public Request build(String args) throws IncorrectInputInScriptException, InvalidFormException, WrongAmountOfElementsException {
        if (args.isEmpty()) throw new WrongAmountOfElementsException();
        try {
            int id = Integer.parseInt(args);
            console.println("=> Enter new data for Ticket ID#" + id + ":");
            Ticket ticketForUpdate = new TicketForm(console).build();
            return new Request(CommandType.UPDATE, args, ticketForUpdate);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("ID must be an integer.");
        }
    }
}