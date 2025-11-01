package commands;

import exceptions.WrongAmountOfElementsException;
import managers.CollectionManager;
import utility.console.Console;

public class CountGreaterThanDiscount extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public CountGreaterThanDiscount(Console console, CollectionManager collectionManager) {
        super("count_greater_than_discount", "output the number of items whose discount field value is greater than the specified value");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        try {
            if(arguments[1].isEmpty()){
                throw new WrongAmountOfElementsException();
            }
            long discountThreshold = Long.parseLong(arguments[1]);
            long count = collectionManager.getCollection().values().stream()
                    .filter(ticket -> ticket.getDiscount() > discountThreshold)
                    .count();
            console.println("Number of items with discount > " + discountThreshold + ": " + count);
            return true;
        } catch(WrongAmountOfElementsException e) {
            console.printError("Wrong number of arguments!");
        } catch(NumberFormatException e) {
            console.printError("Discount has to be a number!");
        }
        return false;
    }
}