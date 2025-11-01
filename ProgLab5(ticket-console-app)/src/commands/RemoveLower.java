package commands;

import exceptions.InvalidFormException;
import exceptions.IncorrectInputInScriptException;
import exceptions.WrongAmountOfElementsException;
import managers.CollectionManager;
import models.Ticket;
import models.forms.TicketForm;
import utility.console.Console;

import java.util.Iterator;

public class RemoveLower extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public RemoveLower(Console console, CollectionManager collectionManager) {
        super("remove_lower", "remove from the collection all items smaller than the specified value");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        try {
            if(!arguments[1].isEmpty()){
                throw new WrongAmountOfElementsException();
            }
            console.println("Enter data for comparison Ticket.");
            TicketForm form = new TicketForm(console);
            Ticket reference = form.build();
            int initialSize = collectionManager.getCollection().size();
            // Örneğin, karşılaştırmayı price üzerinden yapalım:
            collectionManager.getCollection().values().removeIf(t -> t.getPrice() < reference.getPrice());
            int removed = initialSize - collectionManager.getCollection().size();
            console.println("Items removed: " + removed);
            return true;
        } catch(WrongAmountOfElementsException e) {
            console.printError("Wrong number of arguments!");
        } catch(InvalidFormException e) {
            console.printError("Incorrect data for comparison!");
        } catch(IncorrectInputInScriptException e) {
            // Script modunda hata
        }
        return false;
    }
}