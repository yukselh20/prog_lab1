package commands;

import utility.console.Console;

public class ExecuteScript extends Command {
    private final Console console;

    public ExecuteScript(Console console) {
        super("execute_script", "execute_script <file_name> : read and execute a script from a specified file");
        this.console = console;
    }

    @Override
    public boolean apply(String[] arguments) {
        if(arguments[1].isEmpty()){
            console.println("Utilization: " + getName());
            return false;
        }
        console.println("Script Execution: " + arguments[1]);
        // Runner sınıfı script modunu devralıyor.
        return true;
    }
}
