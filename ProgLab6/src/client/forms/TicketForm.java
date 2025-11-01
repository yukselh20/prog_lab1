package client.forms;

import client.utility.Interrogator;
import client.utility.ValidatedInputReader;
import client.utility.console.Console;
import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.InvalidFormException;
import common.models.*;
import common.utility.validation.*;

import java.util.Scanner;

public class TicketForm {
    private final Console console;
    private final Scanner scanner; // Kept for simple, non-validated string input like comments
    private final ValidatedInputReader reader;

    public TicketForm(Console console) {
        this.console = console;
        this.scanner = Interrogator.getUserScanner();
        this.reader = new ValidatedInputReader(this.scanner, console);
    }

    public Ticket build() throws IncorrectInputInScriptException, InvalidFormException {
        String name = reader.request("Enter the name for the Ticket:", new NonEmptyStringStrategy());

        Coordinates coordinates = new CoordinatesForm(console).build();

        int price = reader.request("Enter price (integer > 0):", new PositiveIntegerStrategy());

        long discount = reader.request("Enter discount (integer > 0 and <= 100):", new DiscountStrategy());

        console.println("Enter a comment (can be blank):");
        String comment = scanner.nextLine().trim();
        if (comment.isEmpty()) {
            comment = null;
        }
        if (comment != null && comment.length() > 631) {
            throw new InvalidFormException("The comment length should be no more than 631 characters long.");
        }

        console.println("Available Ticket types: " + TicketType.names());
        TicketType type = reader.request("Enter the Ticket type:", new EnumStrategy<>(TicketType.class));

        // Event creation
        Event event = null;
        String createEventInput = reader.request("Create an Event for this Ticket? (yes/no):", new YesNoStrategy());
        if (createEventInput.equals("yes")) {
            event = new EventForm(console).build();
        }

        try {
            return Ticket.createTicket(name, coordinates, price, discount, comment, type, event);
        } catch (IllegalArgumentException e) {
            throw new InvalidFormException("Ticket failed validation: " + e.getMessage());
        }
    }
}