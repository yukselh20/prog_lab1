package commands;

import managers.CollectionManager;
import utility.console.Console;

/**
 * show : Koleksiyondaki tüm Ticket’ları gösterir.
 */
public class Show extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Show(Console console, CollectionManager collectionManager) {
        super("show", "display all items in the collection");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        if(!arguments[1].isEmpty()){
            console.println("Utilization: " + getName());
            return false;
        }
        console.println(collectionManager.getAllTickets());
        return true;
    }
}
