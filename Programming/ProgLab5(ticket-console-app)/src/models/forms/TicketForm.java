package models.forms;

import models.*;
import exceptions.InvalidFormException;
import exceptions.IncorrectInputInScriptException;
import utility.Interrogator;
import utility.console.Console;

import java.util.Scanner;

/**
 * Ticket nesnesi oluşturmak için form.
 * Bu form, gerekli alanları almak için CoordinatesForm ve EventForm sınıflarını kullanır.
 */
public class TicketForm {
    private final Console console;
    private final Scanner scanner;

    public TicketForm(Console console) {
        this.console = console;
        this.scanner = Interrogator.getUserScanner();
    }

    /**
     * Kullanıcı girdilerine göre Ticket nesnesi oluşturur.
     *
     * @return Oluşturulan Ticket nesnesi.
     * @throws IncorrectInputInScriptException Girdi hatasında.
     * @throws InvalidFormException            Oluşturulan nesne doğrulanamazsa.
     */
    public Ticket build() throws IncorrectInputInScriptException, InvalidFormException {
        console.println("Enter the name Ticket:");
        String name = readNonEmptyString();

        console.println("Enter the coordinate data:");
        Coordinates coordinates = new CoordinatesForm(console).build();

        console.println("Enter price (integer > 0):");
        int price = readPositiveInt();

        console.println("Enter discount (integer > 0 and <= 100):");
        long discount = readDiscount();

        console.println("Enter a comment (can be blank):");
        String comment = scanner.nextLine().trim();
        if (comment.isEmpty()) comment = null;
        if (comment != null && comment.length() > 631) {
            throw new InvalidFormException("The comment length should be no more than 631 characters long.");
        }

        TicketType type;
        while (true) {
            console.println("Enter the Ticket type. Available values: " + TicketType.names());
            String typeStr = readNonEmptyString();
            try {
                type = TicketType.valueOf(typeStr.toUpperCase());
                break; // Geçerli değer alındı, döngüden çık
            } catch (IllegalArgumentException e) {
                console.printError("Invalid Ticket type. Repeat entry.");
                // Eğer script modunda çalışıyorsanız, belirli koşullarda exception fırlatabilirsiniz:
                if (Interrogator.fileMode()) {
                    throw new IncorrectInputInScriptException("Invalid Ticket type in the script.");
                }
            }
        }


        String createEventInput;
        while (true) {
            console.println("Create an Event for Ticket? (yes/no):");
            createEventInput = scanner.nextLine().trim().toLowerCase();
            if (createEventInput.equals("yes") || createEventInput.equals("no")) {
                break;
            } else {
                console.printError("Enter 'yes' or 'no'.");
                // Eğer script modunda çalışıyorsak, hatalı giriş durumunda exception fırlatmak isteyebilirsiniz:
                if (Interrogator.fileMode()) {
                    throw new IncorrectInputInScriptException("Incorrect entry for yes/no.");
                }
            }
        }

        try { // Oluşturulan nesne doğrulanır.
            return Ticket.createTicket(name, coordinates, price, discount, comment, type,
                    createEventInput.equals("yes") ? new EventForm(console).build() : null);
        } catch (IllegalArgumentException e) {
            throw new InvalidFormException("Event failed validation: " + e.getMessage());
        }

//        Ticket ticket = new Ticket(name, coordinates, price, discount, comment, type, event);
//        if (!ticket.validate()) {
//            throw new InvalidFormException("Ticket did not pass validation.");
//        }
//        return ticket;
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

    private int readPositiveInt() throws IncorrectInputInScriptException {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value > 0) return value;
                else {
                    console.printError("The number must be greater than 0.");
                    if (Interrogator.fileMode()) throw new IncorrectInputInScriptException();
                }
            } catch (NumberFormatException e) {
                console.printError("Enter a valid integer.");
                if (Interrogator.fileMode()) throw new IncorrectInputInScriptException();
            }
        }
    }

    private long readDiscount() throws IncorrectInputInScriptException {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                long value = Long.parseLong(line);
                if (value > 0 && value <= 100) return value;
                else {
                    console.printError("The discount must be greater than 0 and not exceed 100.");
                    if (Interrogator.fileMode()) throw new IncorrectInputInScriptException();
                }
            } catch (NumberFormatException e) {
                console.printError("Enter a valid integer.");
                if (Interrogator.fileMode()) throw new IncorrectInputInScriptException();
            }
        }
    }
}
