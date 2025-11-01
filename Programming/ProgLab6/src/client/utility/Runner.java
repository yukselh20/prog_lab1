package client.utility;

import client.commands.*;
import client.network.UDPClient;
import client.utility.console.Console;
import common.dto.Request;
import common.dto.Response;
import common.exceptions.*;
import common.exceptions.NoSuchElementException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * İstemci uygulamasının ana mantığını yöneten sınıf.
 * Kullanıcı girdisini alır, komutları işler, sunucuya istek gönderir ve yanıtı gösterir.
 */

public class Runner {

    public enum ExitCode { OK, ERROR, EXIT }

    private final Console console;
    private final UDPClient udpClient;
    private final List<String> scriptStack = new ArrayList<>();

    // Komut adlarını (String) ilgili komut nesnelerine (ICommand) eşleyen harita.
    // Bu yapı, "Command" tasarım desenini istemci tarafında uygular.
    private final Map<String, ICommand> commandMap = new HashMap<>();

    public Runner(Console console, UDPClient udpClient) {
        this.console = console;
        this.udpClient = udpClient;
        // İstemci başladığında, tüm komutları kaydederek haritayı doldururuz.
        this.registerCommands();
    }


    /**
     * İstemci tarafında çalışacak tüm komut nesnelerini oluşturur ve haritaya kaydeder.
     */
    private void registerCommands() {
        commandMap.put("help", new HelpCommand());
        commandMap.put("info", new InfoCommand());
        commandMap.put("show", new ShowCommand());
        commandMap.put("clear", new ClearCommand());
        commandMap.put("print_descending", new PrintDescendingCommand());
        // Kullanıcıdan form doldurmasını isteyen komutlar. `console` nesnesine ihtiyaç duyarlar.
        commandMap.put("insert", new InsertCommand(console));
        commandMap.put("update", new UpdateCommand(console));
        commandMap.put("remove_key", new RemoveKeyCommand());
        commandMap.put("remove_lower", new RemoveLowerCommand(console));
        commandMap.put("replace_if_lower", new ReplaceIfLowerCommand(console));
        commandMap.put("count_greater_than_discount", new CountGreaterThanDiscountCommand());
        commandMap.put("filter_starts_with_name", new FilterStartsWithNameCommand());
    }


    /**
     * İnteraktif modda istemciyi çalıştıran ana döngü.
     */
    public void interactiveMode() {
        Scanner scanner = Interrogator.getUserScanner();
        Interrogator.setUserMode();
        try {
            ExitCode commandStatus;
            String[] userCommand;

            do {
                console.ps1();
                String fullInputLine = scanner.nextLine().trim();
                userCommand = fullInputLine.split(" ", 2);
                String commandName = userCommand[0].toLowerCase();
                String commandArgs = (userCommand.length > 1) ? userCommand[1].trim() : "";

                commandStatus = processUserInput(commandName, commandArgs);

            } while (commandStatus != ExitCode.EXIT);

        } catch (IllegalStateException exception) {
            console.printError("Unexpected error! Exiting.");
        } finally {
            udpClient.close();
        }
    }

    private ExitCode processUserInput(String commandName, String commandArgs) {
        if (commandName.isEmpty()) return ExitCode.OK;

        if ("exit".equals(commandName)) {
            if (!commandArgs.isEmpty()) {
                console.printError("Usage: exit (no arguments)");
                return ExitCode.ERROR;
            }
            console.println("Exiting program...");
            return ExitCode.EXIT;
        }

        if ("execute_script".equals(commandName)) {
            if (commandArgs.isEmpty()) {
                console.printError("Usage: execute_script <file_name>");
                return ExitCode.ERROR;
            }
            return scriptMode(commandArgs);
        }

        // Mapten ilgili komut nesnesini buluruz.
        ICommand command = commandMap.get(commandName);
        if (command == null) {
            console.printError("Unknown command: '" + commandName + "'. Type 'help' for a list of commands.");
            return ExitCode.ERROR;
        }

        try {
            // 1. Komut nesnesinin `build` metodunu çağırarak Request DTO'sunu oluştururuz.
            //    Kullanıcı girdisi gerekiyorsa (formlar), bu işlem `build` metodu içinde yapılır.
            Request requestToSend = command.build(commandArgs);

            // 2. Oluşturulan isteği sunucuya gönderip yanıtı alırız.
            Response response = udpClient.sendAndReceive(requestToSend);


            // 3. Yanıtı işleriz.
            if (response != null) {
                console.println(response.toString());
                return response.isSuccess() ? ExitCode.OK : ExitCode.ERROR;
            } else {
                console.printError("Failed to get response from server (max retries exceeded or other issue).");
                return ExitCode.ERROR;
            }

        } catch (WrongAmountOfElementsException e) {
            console.printError("Incorrect usage for command '" + commandName + "'. Please check arguments.");
            return ExitCode.ERROR;
        } catch (NumberFormatException e) {
            console.printError("Numeric argument is invalid for command '" + commandName + "': " + e.getMessage());
            return ExitCode.ERROR;
        } catch (InvalidFormException | IncorrectInputInScriptException e) {
            console.printError("Invalid data entered for command '" + commandName + "': " + e.getMessage());
            return ExitCode.ERROR;
        } catch (Exception e) {
            console.printError("An unexpected error occurred: " + e.getMessage());
            return ExitCode.ERROR;
        }
    }

    public ExitCode scriptMode(String fileName) {
        String[] userCommand;
        ExitCode commandStatus = ExitCode.OK;
        scriptStack.add(fileName);
        console.println("Executing script: " + fileName);

        try (Scanner scriptScanner = new Scanner(new File(fileName))) {
            if (!scriptScanner.hasNext()) {
                throw new NoSuchElementException("Script file is empty!");
            }

            Scanner tmpScanner = Interrogator.getUserScanner();
            Interrogator.setUserScanner(scriptScanner);
            Interrogator.setFileMode();

            while (commandStatus == ExitCode.OK && scriptScanner.hasNextLine()) {
                String fullInputLine = scriptScanner.nextLine().trim();
                if (fullInputLine.isEmpty()) continue;

                console.println(console.getPS1() + fullInputLine);

                userCommand = fullInputLine.split(" ", 2);
                String commandName = userCommand[0].toLowerCase();
                String commandArgs = (userCommand.length > 1) ? userCommand[1].trim() : "";

                if ("execute_script".equals(commandName)) {
                    for (String script : scriptStack) {
                        if (commandArgs.equals(script)) {
                            throw new ScriptRecursionException();
                        }
                    }
                }
                commandStatus = processUserInput(commandName, commandArgs);
            }

            Interrogator.setUserScanner(tmpScanner);
            Interrogator.setUserMode();

            if (commandStatus == ExitCode.ERROR) {
                console.printError("Script execution aborted due to an error in the script.");
            } else {
                console.println("Script '" + fileName + "' executed successfully.");
            }
            return commandStatus;

        } catch (FileNotFoundException exception) {
            console.printError("Script file not found: " + fileName);
            commandStatus = ExitCode.ERROR;
        } catch (NoSuchElementException exception) {
            console.printError(exception.getMessage());
            commandStatus = ExitCode.ERROR;
        } catch (ScriptRecursionException exception) {
            console.printError("Script recursion detected! Aborting script.");
            commandStatus = ExitCode.ERROR;
        } catch (IllegalStateException exception) {
            console.printError("Unexpected error during script execution!");
            commandStatus = ExitCode.ERROR;
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
            Interrogator.setUserMode();
        }
        return commandStatus;
    }
}