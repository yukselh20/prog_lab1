package commands;

import managers.CollectionManager;
import models.Ticket;
import utility.console.Console;
import java.util.Comparator;
import java.util.List;


public class PrintDescending extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public PrintDescending(Console console, CollectionManager collectionManager) {
        super("print_descending", "output the elements of the collection in descending order");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        if(!arguments[1].isEmpty()){
            console.println("Utilization: " + getName());
            return false;
        }
        List<Ticket> sorted = collectionManager.getCollection().values().stream()
                .sorted(Comparator.comparing(Ticket::getPrice).reversed())
                .toList();
        sorted.forEach(console::println);
        return true;
    }
}
