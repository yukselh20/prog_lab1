package client.utility;

import client.commands.*;
import client.network.UDPClient;
import client.utility.console.Console;
import common.dto.Request;
import common.dto.Response;
import common.exceptions.*;
import common.exceptions.NoSuchElementException;
import common.models.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages the client application's main logic, including user authentication and command execution.
 */
public class Runner {
    public enum ExitCode { OK, ERROR, EXIT }
    private final Console console;
    private final UDPClient udpClient;
    private final Map<String, ICommand> commandMap = new HashMap<>();
    private final List<String> scriptStack = new ArrayList<>();

    private User currentUser = null; // To store the authenticated user's credentials

    public Runner(Console console, UDPClient udpClient) {
        this.console = console;
        this.udpClient = udpClient;
        this.registerCommands();
    }

    private void registerCommands() {
        commandMap.put("help", new HelpCommand());
        commandMap.put("info", new InfoCommand());
        commandMap.put("show", new ShowCommand());
        commandMap.put("clear", new ClearCommand());
        commandMap.put("insert", new InsertCommand(console));
        commandMap.put("update", new UpdateCommand(console));
        commandMap.put("remove_key", new RemoveKeyCommand());
        commandMap.put("remove_lower", new RemoveLowerCommand(console));
        commandMap.put("replace_if_lower", new ReplaceIfLowerCommand(console));
        commandMap.put("count_greater_than_discount", new CountGreaterThanDiscountCommand());
        commandMap.put("filter_starts_with_name", new FilterStartsWithNameCommand());
        commandMap.put("print_descending", new PrintDescendingCommand());
    }

    /**
     * Main client loop. Manages authentication and command processing.
     */
    public void run() {
        Scanner scanner = Interrogator.getUserScanner();

        // Authentication loop
        while (currentUser == null) {
            authenticate(scanner);
        }

        // Main command loop
        interactiveMode(scanner);

        udpClient.close();
    }

    /**
     * Handles the user authentication process (login/register).
     */
    private void authenticate(Scanner scanner) {
        console.println("Enter 'login' to sign in or 'register' to create a new account:");
        String choice = scanner.nextLine().trim().toLowerCase();

        switch (choice) {
            case "login":
                handleLogin(scanner);
                break;
            case "register":
                handleRegister(scanner);
                break;
            default:
                console.printError("Invalid choice. Please enter 'login' or 'register'.");
                break;
        }
    }

    private void handleLogin(Scanner scanner) {
        console.println("Enter username:");
        String username = scanner.nextLine().trim();
        console.println("Enter password:");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            console.printError("Username and password cannot be empty.");
            return;
        }

        User user = new User(username, password);
        Request authRequest = new Request(common.dto.CommandType.HELP, null, null, user);
        Response response = udpClient.sendAndReceive(authRequest);

        if (response != null && response.isSuccess()) {
            console.println("Login successful. Welcome, " + username + "!");
            currentUser = user;
        } else {
            console.printError("Login failed: " + (response != null ? response.getMessage() : "No response from server."));
            currentUser = null;
        }
    }

    private void handleRegister(Scanner scanner) {
        console.println("Enter a new username:");
        String username = scanner.nextLine().trim();
        console.println("Enter a new password:");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            console.printError("Username and password cannot be empty.");
            return;
        }

        User user = new User(username, password);
        Request registerRequest = new Request(common.dto.CommandType.REGISTER, null, null, user);
        Response response = udpClient.sendAndReceive(registerRequest);

        if (response != null) {
            console.println(response.getMessage());
        } else {
            console.printError("No response from server during registration.");
        }
    }

    /**
     * Runs the client in interactive command mode.
     */
    private void interactiveMode(Scanner scanner) {
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
        }
    }

    private ExitCode processUserInput(String commandName, String commandArgs) {
        if (commandName.isEmpty()) return ExitCode.OK;

        if ("exit".equals(commandName)) {
            if (!commandArgs.isEmpty()) {
                console.printError("Usage: exit (no arguments)");
                return ExitCode.ERROR;
            }
            return ExitCode.EXIT;
        }

        if ("execute_script".equals(commandName)) {
            return scriptMode(commandArgs);
        }

        ICommand command = commandMap.get(commandName);
        if (command == null) {
            console.printError("Unknown command: '" + commandName + "'. Type 'help' for a list of commands.");
            return ExitCode.ERROR;
        }

        try {
            // Build the request using the command builder
            Request requestToSend = command.build(commandArgs);

            // Attach the current user to the request
            Request authenticatedRequest = new Request(
                    requestToSend.getCommandType(),
                    requestToSend.getArguments(),
                    requestToSend.getTicketArgument(),
                    currentUser
            );

            Response response = udpClient.sendAndReceive(authenticatedRequest);

            if (response != null) {
                console.println(response.toString());
                return response.isSuccess() ? ExitCode.OK : ExitCode.ERROR;
            } else {
                console.printError("Failed to get response from server (max retries exceeded or other issue).");
                return ExitCode.ERROR;
            }
        } catch (WrongAmountOfElementsException | InvalidFormException | IncorrectInputInScriptException e) {
            console.printError("Invalid data entered for command '" + commandName + "': " + e.getMessage());
            return ExitCode.ERROR;
        } catch (NumberFormatException e) {
            console.printError("Numeric argument is invalid for command '" + commandName + "': " + e.getMessage());
            return ExitCode.ERROR;
        } catch (Exception e) {
            console.printError("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            return ExitCode.ERROR;
        }
    }

    public ExitCode scriptMode(String fileName) {
        // it calls `processUserInput`, which now correctly
        // attaches the `currentUser` to every request sent from the script.
        String[] userCommand;
        ExitCode commandStatus = ExitCode.OK;
        scriptStack.add(fileName);

        try (Scanner scriptScanner = new Scanner(new File(fileName))) {
            if (!scriptScanner.hasNext()) {
                throw new NoSuchElementException("Script file is empty!");
            }
            Scanner tmpScanner = Interrogator.getUserScanner();
            Interrogator.setUserScanner(scriptScanner);
            Interrogator.setFileMode();

            do {
                String fullInputLine = scriptScanner.nextLine().trim();
                if (fullInputLine.isEmpty()) continue;
                console.println(console.getPS1() + fullInputLine);

                userCommand = fullInputLine.split(" ", 2);
                String commandName = userCommand[0].toLowerCase();
                String commandArgs = (userCommand.length > 1) ? userCommand[1].trim() : "";

                if ("execute_script".equals(commandName)) {
                    for (String script : scriptStack) {
                        if (commandArgs.equals(script)) throw new ScriptRecursionException();
                    }
                }

                commandStatus = processUserInput(commandName, commandArgs);

            } while (commandStatus == ExitCode.OK && scriptScanner.hasNextLine());

            Interrogator.setUserScanner(tmpScanner);
            Interrogator.setUserMode();

            if (commandStatus == ExitCode.ERROR) {
                console.printError("Script execution aborted due to an error in the script.");
            } else {
                console.println("Script '" + fileName + "' executed successfully.");
            }

        } catch (FileNotFoundException e) {
            console.printError("Script file not found: " + fileName);
            commandStatus = ExitCode.ERROR;
        } catch (NoSuchElementException e) {
            console.printError(e.getMessage());
            commandStatus = ExitCode.ERROR;
        } catch (ScriptRecursionException e) {
            console.printError("Script recursion detected! Aborting script.");
            commandStatus = ExitCode.ERROR;
        } catch (IllegalStateException e) {
            console.printError("Unexpected error during script execution!");
            commandStatus = ExitCode.ERROR;
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        return commandStatus;
    }

}