package commands;

import managers.CollectionManager;
import utility.console.Console;

public class Clear extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Clear(Console console, CollectionManager collectionManager) {
        super("clear", "clean up the collection");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        if(!arguments[1].isEmpty()){
            console.println("Usage: " + getName());
            return false;
        }
        collectionManager.clearCollection();
        console.println("The collection has been cleared!");
        return true;
    }
}