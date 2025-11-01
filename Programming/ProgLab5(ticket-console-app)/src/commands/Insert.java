package commands;

import exceptions.WrongAmountOfElementsException;
import managers.CollectionManager;
import models.Ticket;
import models.forms.TicketForm;
import utility.console.Console;
import exceptions.InvalidFormException;
import exceptions.IncorrectInputInScriptException;

public class Insert extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Insert(Console console, CollectionManager collectionManager) {
        super("insert", "insert <key> {element} : add a new item with the specified key");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        try {
            if (arguments.length < 2 || arguments[1].trim().isEmpty()) {
                throw new WrongAmountOfElementsException();
            }

            long key;
            try {
                key = Long.parseLong(arguments[1].trim());
            } catch (NumberFormatException e) {
                console.printError("The key must be a number.");
                return false;
            }

            console.println("Creating a new Ticket...");
            TicketForm form = new TicketForm(console);

            Ticket ticket = form.build(); // Yeni bir Ticket nesnesi oluştur
            // Artık ticket.setKey(key) çağrısına gerek yok

            console.println("New Ticket created: " + ticket); // Kontrol için

            collectionManager.addToCollection(key, ticket); // Koleksiyona ekle
            console.println("Ticket has been successfully added!");

            return true;
        } catch (WrongAmountOfElementsException e) {
            console.printError("Wrong number of arguments!");
        } catch (InvalidFormException e) {
            console.printError("Ticket has not been created: " + e.getMessage());
        } catch (IncorrectInputInScriptException e) {
            // Script modunda hata olduğunda
        }
        return false;
    }
}
