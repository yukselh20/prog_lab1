package utility;

import exceptions.ScriptRecursionException;
import managers.CommandManager;
import utility.console.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Uygulamanın interaktif ve script modlarını yöneten sınıf.
 */
public class Runner {
    public enum ExitCode { OK, ERROR, EXIT }

    private final Console console;
    private final CommandManager commandManager;
    private final List<String> scriptStack = new ArrayList<>();
    private static final String SCRIPT_BASE_DIR = System.getProperty("user.dir") +File.separator + "src" + File.separator;


    public Runner(Console console, CommandManager commandManager) {
        this.console = console;
        this.commandManager = commandManager;
    }

    /**
     * İnteraktif mod: Kullanıcıdan komutları alır.
     */
    public void interactiveMode() {
        Scanner scanner = Interrogator.getUserScanner();
        try {
            ExitCode code;
            String[] commandArgs;
            do {
                console.ps1();
                String input = scanner.nextLine().trim();
                commandArgs = (input + " ").split(" ", 2);
                commandArgs[1] = commandArgs[1].trim();
                commandManager.addToHistory(commandArgs[0]);
                code = launchCommand(commandArgs);
            } while(code != ExitCode.EXIT);
        } catch(NoSuchElementException e) {
            console.printError("No user input detected!");
        } catch(IllegalStateException e) {
            console.printError("Unforeseen mistake!");
        }
    }

    /**
     * Script mod: Verilen dosyadan komutları okur ve çalıştırır.
     */
    public ExitCode scriptMode(String fileName) {
        String[] commandArgs;
        ExitCode code;
        scriptStack.add(fileName);
        fileName = SCRIPT_BASE_DIR + fileName;
        if(!new File(fileName).exists()){
            fileName = "../" + fileName;
        }
        try (Scanner scriptScanner = new Scanner(new File(fileName))) {
            if(!scriptScanner.hasNext()) throw new NoSuchElementException();
            Scanner tempScanner = Interrogator.getUserScanner();
            Interrogator.setUserScanner(scriptScanner);
            Interrogator.setFileMode();

            do {
                String input = scriptScanner.nextLine().trim();
                commandArgs = (input + " ").split(" ", 2);
                commandArgs[1] = commandArgs[1].trim();
                while(scriptScanner.hasNextLine() && commandArgs[0].isEmpty()){
                    input = scriptScanner.nextLine().trim();
                    commandArgs = (input + " ").split(" ", 2);
                    commandArgs[1] = commandArgs[1].trim();
                }
                console.println(console.getPS1() + input);
                if(commandArgs[0].equals("execute_script")){
                    for(String script : scriptStack){
                        if(commandArgs[1].equals(script)) throw new ScriptRecursionException();
                        // Bunu düzeltmek için, script dosyasının zaten scriptStack içindeyse
                        // tekrar çalıştırılmamasını sağlamanız gerekir.
                    }
                }
                code = launchCommand(commandArgs);
            } while(code == ExitCode.OK && scriptScanner.hasNextLine());

            Interrogator.setUserScanner(tempScanner);
            Interrogator.setUserMode();

            if(code == ExitCode.ERROR && !(commandArgs[0].equals("execute_script") && !commandArgs[1].isEmpty())){
                console.println("Check the script to make sure the data is correct!");
            }
            return code;
        } catch(FileNotFoundException e) {
            console.printError("Script file not found!");
        } catch(NoSuchElementException e) {
            console.printError("The script file is empty!");
        } catch(ScriptRecursionException e) {
            console.printError("Scripts cannot be called recursively!");
        } catch(IllegalStateException e) {
            console.printError("Unforeseen mistake!");
            System.exit(0);
        } finally {
            if (!scriptStack.isEmpty()) scriptStack.remove(scriptStack.size() - 1);
        }
        return ExitCode.ERROR;
    }

    private ExitCode launchCommand(String[] commandArgs) {
        if(commandArgs[0].isEmpty()) return ExitCode.OK;
        var command = commandManager.getCommands().get(commandArgs[0]);
        if(command == null){
            console.printError("Command '" + commandArgs[0] + "' not found. Type 'help' for help");
            return ExitCode.ERROR;
        }
        switch(commandArgs[0]){
            case "exit":
                if(!command.apply(commandArgs)) return ExitCode.ERROR;
                else return ExitCode.EXIT;
            case "execute_script":
                if(!command.apply(commandArgs)) return ExitCode.ERROR;
                else return scriptMode(commandArgs[1]);
            default:
                if(!command.apply(commandArgs)) return ExitCode.ERROR;
        }
        return ExitCode.OK;
    }
}