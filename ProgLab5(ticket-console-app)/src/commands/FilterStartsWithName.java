package commands;

import exceptions.WrongAmountOfElementsException;
import managers.CollectionManager;
import models.Ticket;
import utility.console.Console;

import java.util.List;

public class FilterStartsWithName extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public FilterStartsWithName(Console console, CollectionManager collectionManager) {
        super("filter_starts_with_name", "output items whose name field value starts with the specified substring");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        try {
            if(arguments[1].isEmpty()){
                throw new WrongAmountOfElementsException();
            }
            String substring = arguments[1];
            List<Ticket> filtered = collectionManager.getCollection().values().stream()
                    .filter(ticket -> ticket.getName().startsWith(substring))
                    .toList();
            if (filtered.isEmpty()) {
                console.println("There are no items with a name beginning with '" + substring + "'.");
            } else {
                filtered.forEach(console::println);
            }
            return true;
        } catch(WrongAmountOfElementsException e) {
            console.printError("Wrong number of arguments!");
        }
        return false;
    }
}
