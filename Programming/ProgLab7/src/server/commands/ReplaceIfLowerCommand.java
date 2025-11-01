package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.Ticket;
import server.managers.CollectionManager;
import java.util.Map;

public class ReplaceIfLowerCommand extends Command {
    public ReplaceIfLowerCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        try {
            long key = Long.parseLong(request.getArguments());
            Ticket newTicket = request.getTicketArgument();
            int userId = collectionManager.verifyUser(request.getUser());

            if (newTicket == null) {
                return new Response("Error: New ticket data is missing for replace_if_lower.", false);
            }

            Map<Long, Ticket> collection = collectionManager.getCollection();
            Ticket existingTicket = collection.get(key);

            if (existingTicket == null) {
                return new Response("Error: Ticket with key " + key + " not found.", false);
            }

            // --- AUTHORIZATION CHECK ---
            if (existingTicket.getOwnerId() != userId) {
                return new Response("Error: Permission denied. You can only modify tickets that you own.", false);
            }

            if (newTicket.compareTo(existingTicket) < 0) {
                // Preserve key attributes
                newTicket.setId(existingTicket.getId());
                newTicket.setKey(key);
                newTicket.setOwnerId(userId);

                if (collectionManager.updateInCollection(newTicket)) {
                    return new Response("Ticket with key " + key + " was replaced.", true);
                } else {
                    return new Response("Error: Failed to update ticket in the database.", false);
                }
            } else {
                return new Response("Ticket with key " + key + " was not replaced (the new ticket was not lower).", true);
            }
        } catch (NumberFormatException e) {
            return new Response("Error: Invalid key format. Key must be a number.", false);
        }
    }
}