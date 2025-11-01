package commands;

import managers.CollectionManager;
import utility.console.Console;

public class Save extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Save(Console console, CollectionManager collectionManager) {
        super("save", "save the collection to a file");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        if(!arguments[1].isEmpty()){
            console.println("Utilization: " + getName());
            return false;
        }
        collectionManager.saveCollection();
        return true;
    }
}