package commands;

import exceptions.WrongAmountOfElementsException;
import managers.CollectionManager;
import utility.console.Console;

public class RemoveKey extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public RemoveKey(Console console, CollectionManager collectionManager) {
        super("remove_key", "remove_key <key> : remove an item from the collection by its key");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        try {
            if(arguments[1].isEmpty()){
                throw new WrongAmountOfElementsException();
            }
            Long key = Long.parseLong(arguments[1]);
            if(!collectionManager.getCollection().containsKey(key)){
                console.printError("Element with this key was not found.");
                return false;
            }
            collectionManager.removeFromCollection(key);
            console.println("The item was successfully deleted.");
            return true;
        } catch(WrongAmountOfElementsException e){
            console.printError("Wrong number of arguments!");
        } catch(NumberFormatException e){
            console.printError("The key must be a number!");
        }
        return false;
    }
}