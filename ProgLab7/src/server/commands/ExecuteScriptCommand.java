package server.commands;

import common.dto.CommandType;
import common.dto.Request;
import common.dto.Response;
import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.InvalidFormException;
import common.exceptions.WrongAmountOfElementsException;
import common.models.Ticket;
import common.models.User; // Required import
import server.managers.CollectionManager;
import server.processing.CommandProcessor;
import server.processing.ScriptReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ExecuteScriptCommand extends Command {
    private final CommandProcessor commandProcessor;
    // Use a ThreadLocal stack to handle concurrent script executions by different users safely
    private final ThreadLocal<Deque<String>> scriptExecutionStack = ThreadLocal.withInitial(ArrayDeque::new);

    private enum ScriptStatus { OK, ERROR }

    public ExecuteScriptCommand(CollectionManager collectionManager, CommandProcessor commandProcessor) {
        super(collectionManager);
        this.commandProcessor = commandProcessor;
    }

    @Override
    public Response execute(Request request) {
        String fileName = request.getArguments();
        // Capture the user who initiated this script execution.
        // This is the key change for Lab 7.
        final User originalUser = request.getUser();

        if (fileName == null || fileName.isEmpty()) {
            return new Response("Error: Script file name is missing.", false);
        }

        File scriptFile = new File(fileName);
        String absolutePath;
        try {
            absolutePath = scriptFile.getCanonicalPath();
        } catch (IOException e) {
            absolutePath = scriptFile.getAbsolutePath();
        }

        // Check for recursion using the current thread's stack
        if (scriptExecutionStack.get().contains(absolutePath)) {
            return new Response("Error: Script recursion detected for file '" + fileName + "'. Aborting script.", false);
        }
        if (!scriptFile.exists() || !scriptFile.isFile()) {
            return new Response("Error: Script file not found or is a directory: '" + fileName + "'.", false);
        }
        if (!scriptFile.canRead()) {
            return new Response("Error: Cannot read script file (permission denied): '" + fileName + "'.", false);
        }

        scriptExecutionStack.get().push(absolutePath);
        StringBuilder scriptOutput = new StringBuilder("--- Executing script '").append(fileName).append("' ---\n");
        ScriptStatus scriptStatus = ScriptStatus.OK;

        try (Scanner scriptScanner = new Scanner(scriptFile)) {
            ScriptReader scriptReader = new ScriptReader(scriptScanner);

            while (scriptStatus == ScriptStatus.OK && scriptScanner.hasNextLine()) {
                String fullInputLine = scriptScanner.nextLine().trim();
                if (fullInputLine.isEmpty() || fullInputLine.startsWith("#")) continue;

                String[] userCommand = fullInputLine.split(" ", 2);
                String commandName = userCommand[0];
                String commandArgs = (userCommand.length > 1) ? userCommand[1].trim() : "";

                // Check for inner script recursion
                if ("execute_script".equalsIgnoreCase(commandName)) {
                    File innerScriptFile = new File(commandArgs);
                    String innerAbsolutePath;
                    try {
                        innerAbsolutePath = innerScriptFile.getCanonicalPath();
                    } catch (IOException e) {
                        innerAbsolutePath = innerScriptFile.getAbsolutePath();
                    }
                    if (scriptExecutionStack.get().contains(innerAbsolutePath)) {
                        scriptOutput.append("! Error: Script recursion detected for '").append(commandArgs).append("'. Stopping execution.\n");
                        scriptStatus = ScriptStatus.ERROR;
                        break;
                    }
                }

                Request scriptRequest;
                try {
                    // Create the sub-request from the script line
                    scriptRequest = createRequestFromScript(commandName, commandArgs, scriptReader);
                    // IMPORTANT: Attach the original user's credentials to the sub-request
                    scriptRequest.setUser(originalUser);
                } catch (Exception e) {
                    scriptOutput.append("! Error parsing script command '").append(commandName).append("': ").append(e.getMessage()).append("\n");
                    scriptStatus = ScriptStatus.ERROR;
                    break;
                }

                // Process the sub-request. The CommandProcessor will handle its authentication.
                Response lineResponse = commandProcessor.process(scriptRequest);
                scriptOutput.append("> ").append(fullInputLine).append("\n");
                scriptOutput.append("  ").append(lineResponse.toString().replace("\n", "\n  ")).append("\n");

                if (!lineResponse.isSuccess()) {
                    scriptStatus = ScriptStatus.ERROR;
                }
            }
        } catch (FileNotFoundException e) {
            scriptOutput.append("! Error: Script file not found: '").append(fileName).append("'.\n");
            scriptStatus = ScriptStatus.ERROR;
        } catch (Exception e) {
            scriptOutput.append("! Error: Unexpected error during script execution '").append(fileName).append("': ").append(e.getMessage()).append("\n");
            scriptStatus = ScriptStatus.ERROR;
        } finally {
            // Clean up the stack for the current thread
            if (!scriptExecutionStack.get().isEmpty()) {
                scriptExecutionStack.get().pop();
            }
            if (scriptExecutionStack.get().isEmpty()) {
                scriptExecutionStack.remove(); // Clean up ThreadLocal to prevent memory leaks
            }
            scriptOutput.append("--- Finished script '").append(fileName).append("' with status: ").append(scriptStatus).append(" ---");
        }

        return new Response(scriptOutput.toString(), scriptStatus == ScriptStatus.OK);
    }

    /**
     * Helper method to create a Request DTO from a line in a script.
     * This does not need to know about the user; the user is attached in the main execute method.
     */
    private Request createRequestFromScript(String commandName, String commandArgs, ScriptReader scriptReader)
            throws WrongAmountOfElementsException, NumberFormatException, InvalidFormException, IncorrectInputInScriptException {

        CommandType type;
        try {
            type = CommandType.valueOf(commandName.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown command in script: " + commandName);
        }

        switch (type) {
            case HELP:
            case INFO:
            case SHOW:
            case CLEAR:
            case PRINT_DESCENDING:
                if (!commandArgs.isEmpty()) throw new WrongAmountOfElementsException();
                return new Request(type);

            case INSERT:
                if (commandArgs.isEmpty()) throw new WrongAmountOfElementsException();
                Long.parseLong(commandArgs);
                Ticket insertTicket = scriptReader.readTicket();
                return new Request(type, commandArgs, insertTicket);

            case REMOVE_KEY:
                if (commandArgs.isEmpty()) throw new WrongAmountOfElementsException();
                Long.parseLong(commandArgs);
                return new Request(type, commandArgs);

            case REPLACE_IF_LOWER:
                if (commandArgs.isEmpty()) throw new WrongAmountOfElementsException();
                Long.parseLong(commandArgs);
                Ticket replaceTicket = scriptReader.readTicket();
                return new Request(type, commandArgs, replaceTicket);

            case UPDATE:
                if (commandArgs.isEmpty()) throw new WrongAmountOfElementsException();
                Integer.parseInt(commandArgs);
                Ticket updateTicket = scriptReader.readTicket();
                return new Request(type, commandArgs, updateTicket);

            case REMOVE_LOWER:
                if (!commandArgs.isEmpty()) throw new WrongAmountOfElementsException();
                Ticket refTicket = scriptReader.readTicket();
                return new Request(type, refTicket);

            case COUNT_GREATER_THAN_DISCOUNT:
            case FILTER_STARTS_WITH_NAME:
            case EXECUTE_SCRIPT:
                if (commandArgs.isEmpty()) throw new WrongAmountOfElementsException();
                return new Request(type, commandArgs);

            case REGISTER: // Register command should not be in scripts for security reasons
                throw new InvalidFormException("Command 'register' cannot be used within a script.");

            default:
                throw new IllegalArgumentException("Command not processable within script: " + commandName);
        }
    }
}