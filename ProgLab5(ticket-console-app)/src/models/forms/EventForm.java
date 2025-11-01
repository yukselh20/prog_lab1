package models.forms;

import models.Event;
import models.EventType;
import exceptions.InvalidFormException;
import exceptions.IncorrectInputInScriptException;
import utility.Interrogator;
import utility.console.Console;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Event nesnesi oluşturmak için form.
 */
public class EventForm {
    private final Console console;
    private final Scanner scanner;

    public EventForm(Console console) {
        this.console = console;
        this.scanner = Interrogator.getUserScanner();
    }

    /**
     * Kullanıcıdan Event bilgilerini alır ve Event nesnesini oluşturur.
     *
     * @return Oluşturulan Event nesnesi.
     * @throws IncorrectInputInScriptException Girdi hatasında.
     * @throws InvalidFormException            Oluşturulan nesne doğrulanamazsa.
     */
    public Event build() throws IncorrectInputInScriptException, InvalidFormException {
        console.println("Enter the name of the event (Event):");
        String name = readNonEmptyString();

        console.println("Enter the date of the event (in ISO_ZONED_DATE_TIME(uuuu-MM-dd'T'HH:mm:ssXXX'['VV']') format, or leave blank for null):");
        String dateStr = scanner.nextLine().trim();
        ZonedDateTime date = null;
        if (!dateStr.isEmpty()) {
            try {
                date = ZonedDateTime.parse(dateStr);
            } catch (DateTimeParseException e) {
                console.printError("Incorrect date format.");
                if (Interrogator.fileMode()) throw new IncorrectInputInScriptException();
            }
        }

        EventType eventType;
        while (true) {
            console.println("Enter the type of event. Available values: " + EventType.names());
            String eventTypeStr = readNonEmptyString();
            try {
                eventType = EventType.valueOf(eventTypeStr.toUpperCase());
                break; // Geçerli değer girildi, döngüden çık
            } catch (IllegalArgumentException e) {
                console.printError("Incorrect event type. Repeat the entry.");
                // Eğer script modunda çalışıyorsa, hata fırlatmak isteyebilirsiniz:
                if (Interrogator.fileMode()) {
                    throw new IncorrectInputInScriptException("Incorrect event type in the script.");
                }
                // Aksi halde, döngü devam eder ve kullanıcıdan tekrar giriş istenir.
            }
        }

        try { // Oluşturulan nesne doğrulanır.
            return Event.createEvent(name, date, eventType);
        } catch (IllegalArgumentException e) {
            throw new InvalidFormException("Event failed validation: " + e.getMessage());
        }
    }
    private String readNonEmptyString() throws IncorrectInputInScriptException {
        while (true) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            } else {
                console.printError("The input cannot be empty.");
                if (Interrogator.fileMode()) throw new IncorrectInputInScriptException();
            }
        }
    }
}