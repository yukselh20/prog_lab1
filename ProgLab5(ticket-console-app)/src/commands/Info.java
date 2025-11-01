package commands;

import managers.CollectionManager;
import utility.console.Console;

/**
 * info : Koleksiyon hakkında bilgi verir.
 */
public class Info extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Info(Console console, CollectionManager collectionManager) {
        super("info", "display collection information");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        if(!arguments[1].isEmpty()){
            console.println("Utilization: " + getName());
            return false;
        }
        console.println(collectionManager.getCollectionInfo());
        return true;
    }
}