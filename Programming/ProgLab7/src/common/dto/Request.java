package common.dto;

import common.models.Ticket;
import common.models.User; // New import
import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 301L; // UID

    private final CommandType commandType;
    private final String arguments;
    private final Ticket ticketArgument;
    private User user; // not final, Runner can set it

    // Constructor for commands without args
    // Simplified constructors for client-side command builders
    public Request(CommandType commandType, String arguments, Ticket ticketArgument) {
        this(commandType, arguments, ticketArgument, null);
    }

    public Request(CommandType commandType, String arguments) {
        this(commandType, arguments, null, null);
    }

    public Request(CommandType commandType) {
        this(commandType, null, null, null);
    }

    // Full constructor
    public Request(CommandType commandType, String arguments, Ticket ticketArgument, User user) {
        this.commandType = commandType;
        this.arguments = arguments;
        this.ticketArgument = ticketArgument;
        this.user = user;
    }

    public Request(CommandType commandType, Ticket ticketArgument) {
        this(commandType, null, ticketArgument, null);
    }

    // Getterlar
    public CommandType getCommandType() { return commandType; }
    public String getArguments() { return arguments; }
    public Ticket getTicketArgument() { return ticketArgument; }
    public User getUser() { return user; } // New getter


    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type=" + commandType +
                (arguments != null ? ", args='" + arguments + '\'' : "") +
                (ticketArgument != null ? ", ticket=" + ticketArgument : "") +
                (user != null ? ", user=" + user.getUsername() : "") + // Updated
                '}';
    }
}