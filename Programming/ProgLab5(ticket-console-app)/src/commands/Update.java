package commands;

import exceptions.WrongAmountOfElementsException;
import exceptions.InvalidFormException;
import exceptions.IncorrectInputInScriptException;
import managers.CollectionManager;
import models.Ticket;
import models.forms.TicketForm;
import utility.console.Console;

public class Update extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Update(Console console, CollectionManager collectionManager) {
        super("update", "update <id> {element} : update the value of the collection item whose id is equal to the given one");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        try {
            if(arguments[1].isEmpty()){
                throw new WrongAmountOfElementsException();
            }
            String idStr = arguments[1].split(" ")[0];
            int id = Integer.parseInt(idStr);
            Ticket existing = collectionManager.getById(id);
            if(existing == null){
                console.printError("Ticket with this id was not found.");
                return false;
            }
            console.println("Update Ticket, id=." + id);
            TicketForm form = new TicketForm(console);
            Ticket newTicket = form.build();
            existing.update(newTicket);
            console.println("Ticket successfully updated.");
            return true;
        } catch(WrongAmountOfElementsException e) {
            console.printError("Wrong number of arguments!");
        } catch(NumberFormatException e) {
            console.printError("ID must be a number!");
        } catch(InvalidFormException e) {
            console.printError("Ticket has not been updated: " + e.getMessage());
        } catch(IncorrectInputInScriptException e) {
            // Script modunda
        }
        return false;
    }
}
