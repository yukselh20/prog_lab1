package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.Ticket;
import server.managers.CollectionManager;

public class ReplaceIfLowerCommand extends Command {
    public ReplaceIfLowerCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        try {
            Long key = Long.parseLong(request.getArguments());
            Ticket newTicket = request.getTicketArgument();
            if (newTicket == null) {
                return new Response("Error: New ticket data is missing for replace_if_lower.", false);
            }

            Ticket existingTicket = collectionManager.getCollection().get(key);
            if (existingTicket == null) {
                return new Response("Error: Ticket with key " + key + " not found.", false);
            }

            if (newTicket.getPrice() < existingTicket.getPrice()) {
                existingTicket.update(newTicket);
                collectionManager.getCollection().put(key, existingTicket);
                return new Response("Ticket with key " + key + " replaced as new price was lower.", true);
            } else {
                return new Response("Ticket with key " + key + " not replaced as new price was not lower.", true);
            }
        } catch (NumberFormatException e) {
            return new Response("Error: Invalid key format. Key must be a number.", false);
        } catch (NullPointerException e) {
            return new Response("Error: Missing key or new ticket data for replace_if_lower.", false);
        }
    }
}