package client.forms;

import client.utility.Interrogator;
import client.utility.ValidatedInputReader;
import client.utility.console.Console;
import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.InvalidFormException;
import common.models.Event;
import common.models.EventType;
import common.utility.validation.EnumStrategy;
import common.utility.validation.NonEmptyStringStrategy;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class EventForm {
    private final Console console;
    private final Scanner scanner; // For the optional date field
    private final ValidatedInputReader reader;

    public EventForm(Console console) {
        this.console = console;
        this.scanner = Interrogator.getUserScanner();
        this.reader = new ValidatedInputReader(this.scanner, console);
    }

    public Event build() throws IncorrectInputInScriptException, InvalidFormException {
        console.println("Enter Event data:");

        String name = reader.request("Enter the name of the event:", new NonEmptyStringStrategy());

        ZonedDateTime date = readDateTimeOptional();

        console.println("Available Event types: " + EventType.names());
        EventType eventType = reader.request("Enter the type of event:", new EnumStrategy<>(EventType.class));

        try {
            return Event.createEvent(name, date, eventType);
        } catch (IllegalArgumentException e) {
            throw new InvalidFormException("Event failed validation: " + e.getMessage());
        }
    }

    private ZonedDateTime readDateTimeOptional() throws IncorrectInputInScriptException {
        console.println("Enter the date of the event (YYYY-MM-DDTHH:MM:SS+ZONE, or leave blank for null):");
        while (true) {
            String dateStr = scanner.nextLine().trim();
            if (dateStr.isEmpty()) {
                return null;
            }
            try {
                return ZonedDateTime.parse(dateStr);
            } catch (DateTimeParseException e) {
                console.printError("Incorrect date format. Please try again or leave blank.");
                if (Interrogator.fileMode()) {
                    throw new IncorrectInputInScriptException();
                }
            }
        }
    }
}