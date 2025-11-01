package commands;

import exceptions.InvalidFormException;
import exceptions.IncorrectInputInScriptException;
import exceptions.WrongAmountOfElementsException;
import managers.CollectionManager;
import models.Ticket;
import models.forms.TicketForm;
import utility.console.Console;

public class ReplaceIfLower extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public ReplaceIfLower(Console console, CollectionManager collectionManager) {
        super("replace_if_lower", "replace the value by key if the new value is less than the old one");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        try {
            if(arguments[1].isEmpty()){
                throw new WrongAmountOfElementsException();
            }
            Long key = Long.parseLong(arguments[1].split(" ")[0]);
            Ticket existing = collectionManager.getCollection().get(key);
            if(existing == null){
                console.printError("Element with this key was not found.");
                return false;
            }
            console.println("Enter the data for the new Ticket:");
            TicketForm form = new TicketForm(console);
            Ticket newTicket = form.build();
            if(newTicket.getPrice() < existing.getPrice()){
                collectionManager.getCollection().put(key, newTicket);
                console.println("The element has been successfully replaced.");
            } else {
                console.println("The new value is not less than the old value. The substitution has not been performed.");
            }
            return true;
        } catch(WrongAmountOfElementsException e) {
            console.printError("Wrong number of arguments!");
        } catch(NumberFormatException e) {
            console.printError("The key has to be a number!");
        } catch(InvalidFormException e) {
            console.printError("Incorrect data: " + e.getMessage());
        } catch(IncorrectInputInScriptException e) {
            // Script modunda
        }
        return false;
    }
}