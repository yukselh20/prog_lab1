package client.utility;

import client.utility.console.Console;
import common.exceptions.IncorrectInputInScriptException;
import common.utility.validation.ValidationStrategy;
import java.util.Scanner;

/**
 * A helper class that uses a ValidationStrategy to read and validate user input.
 * It encapsulates the logic for prompting, reading, validating, and retrying on failure.
 */
public class ValidatedInputReader {
    private final Scanner scanner;
    private final Console console;

    public ValidatedInputReader(Scanner scanner, Console console) {
        this.scanner = scanner;
        this.console = console;
    }

    public <T> T request(String prompt, ValidationStrategy<T> strategy) throws IncorrectInputInScriptException {
        console.println(prompt);
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return strategy.validate(input);
            } catch (NumberFormatException e) {
                console.printError("Invalid number format. Please try again.");
                if (Interrogator.fileMode()) {
                    throw new IncorrectInputInScriptException();
                }
            } catch (Exception e) {
                // Catches validation errors from the strategy
                console.printError(e.getMessage() + ". Please try again.");
                if (Interrogator.fileMode()) {
                    throw new IncorrectInputInScriptException();
                }
            }
        }
    }
}