package commands;

import utility.console.Console;

public class Exit extends Command {
    private final Console console;

    public Exit(Console console) {
        super("exit", "end the program (without saving to a file)");
        this.console = console;
    }

    @Override
    public boolean apply(String[] arguments) {
        if(!arguments[1].isEmpty()){
            console.println("Utilization: " + getName());
            return false;
        }
        console.println("Program Completion...");
        return true;
    }
}
