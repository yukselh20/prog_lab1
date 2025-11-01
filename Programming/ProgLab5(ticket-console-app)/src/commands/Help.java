package commands;

import managers.CommandManager;
import utility.console.Console;

/**
 * help : Kullanılabilir komutları listeler.
 */
public class Help extends Command {
    private final Console console;
    private final CommandManager commandManager;

    public Help(Console console, CommandManager commandManager) {
        super("help", "display help for available commands");
        this.console = console;
        this.commandManager = commandManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        if(!arguments[1].isEmpty()){
            console.println("Utilization: " + getName());
            return false;
        }
        commandManager.getCommands().forEach((k, cmd) -> console.printTable(cmd.getName(), cmd.getDescription()));

        return true;
    }
}