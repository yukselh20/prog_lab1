package commands;

import managers.CommandManager;
import utility.console.Console;

public class History extends Command {
    private final Console console;
    private final CommandManager commandManager;

    public History(Console console, CommandManager commandManager) {
        super("history", "output the last 5 commands (without arguments)");
        this.console = console;
        this.commandManager = commandManager;
    }

    @Override
    public boolean apply(String[] arguments) {
        if(!arguments[1].isEmpty()){
            console.println("Utilization: " + getName());
            return false;
        }
        console.println("Last commands:");
        for(String cmd : commandManager.getCommandHistory()){
            console.println(cmd);
        }
        return true;
    }
}
