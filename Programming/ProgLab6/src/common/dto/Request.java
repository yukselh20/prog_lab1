package common.dto;

import common.models.Ticket; // Modeli import et

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 301L; // UID

    private final CommandType commandType;
    private final String arguments; // Komut argümanları (örn. key, id, discount, filename)
    private final Ticket ticketArgument; // Ticket gerektiren komutlar için

    // Constructorlar (farklı ihtiyaçlar için)
    public Request(CommandType commandType) {
        this(commandType, null, null);
    }

    public Request(CommandType commandType, String arguments) {
        this(commandType, arguments, null);
    }

    public Request(CommandType commandType, Ticket ticketArgument) {
        this(commandType, null, ticketArgument);
    }

    public Request(CommandType commandType, String arguments, Ticket ticketArgument) {
        this.commandType = commandType;
        this.arguments = arguments;
        this.ticketArgument = ticketArgument;
    }

    // Getterlar
    public CommandType getCommandType() { return commandType; }
    public String getArguments() { return arguments; }
    public Ticket getTicketArgument() { return ticketArgument; }

    @Override
    public String toString() { // Debugging için
        return "Request{" +
                "type=" + commandType +
                (arguments != null ? ", args='" + arguments + '\'' : "") +
                (ticketArgument != null ? ", ticket=" + ticketArgument : "") +
                '}';
    }
}