package client.forms;

import client.utility.Interrogator;
import client.utility.ValidatedInputReader;
import client.utility.console.Console;
import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.InvalidFormException;
import common.models.Coordinates;
import common.utility.validation.FloatLowerBoundStrategy;

/**
 * A form for creating a Coordinates object.
 * This class has been refactored to use the reusable ValidatedInputReader and strategies.
 */
public class CoordinatesForm {
    private final Console console;
    private final ValidatedInputReader reader;

    public CoordinatesForm(Console console) {
        this.console = console;
        // Initialize the reader using the global scanner and console
        this.reader = new ValidatedInputReader(Interrogator.getUserScanner(), console);
    }

    /**
     * Builds a new Coordinates object by prompting the user for x and y values
     * and validating them using the FloatLowerBoundStrategy.
     *
     * @return The newly created and validated Coordinates object.
     * @throws IncorrectInputInScriptException If running in script mode and input is invalid.
     * @throws InvalidFormException If the final combined coordinates fail model-level validation.
     */
    public Coordinates build() throws IncorrectInputInScriptException, InvalidFormException {
        console.println("Enter coordinate data:");

        // Use the reader and the float strategy for the 'x' coordinate
        float x = reader.request(
                "Enter a coordinate X (float, > -661):",
                new FloatLowerBoundStrategy(-661f, "The value must be greater than -661")
        );

        // Reuse the reader and the float strategy for the 'y' coordinate
        float y = reader.request(
                "Enter a coordinate Y (float, > -493):",
                new FloatLowerBoundStrategy(-493f, "The value must be greater than -493")
        );

        try {
            // The final object creation might still have its own validation rules
            // (e.g., in a static factory method), so we keep this try-catch block.
            return Coordinates.createCoordinates(x, y);
        } catch (IllegalArgumentException e) {
            // This catches errors from the Coordinates model itself, not from the input parsing.
            throw new InvalidFormException("Coordinates did not pass validation: " + e.getMessage());
        }
    }
}