import commands.*;
import managers.CollectionManager;
import managers.CommandManager;
import managers.DumpManager;
import models.Ticket;
import utility.Interrogator;
import utility.Runner;
import utility.console.Console;
import utility.console.StandardConsole;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Kullanıcı girdileri için Scanner ayarlıyoruz.
        Interrogator.setUserScanner(new Scanner(System.in));
        Console console = new StandardConsole();

        // Dosya ismini çevresel değişkenden alıyoruz (örn. TICKET_FILE).
        String fileName = System.getenv("TICKET_FILE");
        if(fileName == null || fileName.isEmpty()) {
            console.println("Environment variable `TICKET_FILE` is not defined. Terminating the process.");
            System.exit(1);
        } else {
            console.println("Type 'help' to see all commands.");
        }


        DumpManager dumpManager = new DumpManager(fileName, console);
        CollectionManager collectionManager = new CollectionManager(dumpManager);

        Ticket.updateNextId(collectionManager.getCollection());


        CommandManager commandManager = new CommandManager();
        commandManager.register("help", new Help(console, commandManager));
        commandManager.register("info", new Info(console, collectionManager));
        commandManager.register("show", new Show(console, collectionManager));
        commandManager.register("insert", new Insert(console, collectionManager));
        commandManager.register("update", new Update(console, collectionManager));
        commandManager.register("remove_key", new RemoveKey(console, collectionManager));
        commandManager.register("clear", new Clear(console, collectionManager));
        commandManager.register("save", new Save(console, collectionManager));
        commandManager.register("execute_script", new ExecuteScript(console));
        commandManager.register("exit", new Exit(console));
        commandManager.register("remove_lower", new RemoveLower(console, collectionManager));
        commandManager.register("history", new History(console, commandManager));
        commandManager.register("replace_if_lower", new ReplaceIfLower(console, collectionManager));
        commandManager.register("count_greater_than_discount", new CountGreaterThanDiscount(console, collectionManager));
        commandManager.register("filter_starts_with_name", new FilterStartsWithName(console, collectionManager));
        commandManager.register("print_descending", new PrintDescending(console, collectionManager));

        Runner runner = new Runner(console, commandManager);
        runner.interactiveMode();
    }
}