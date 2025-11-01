package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.Ticket;
import server.managers.CollectionManager;

public class InsertCommand extends Command {
    public InsertCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        try {
            Long key = Long.parseLong(request.getArguments());
            Ticket ticket = request.getTicketArgument();
            if (ticket == null) {
                return new Response("Error: Ticket data is missing for insert command.", false);
            }
            if (collectionManager.getCollection().containsKey(key)) {
                return new Response("Error: Key " + key + " already exists.", false);
            }

            boolean success = collectionManager.addToCollection(key, ticket);
            if (success) {
                return new Response("Ticket successfully added with key " + key + ".", true);
            } else {
                return new Response("Error: Failed to add ticket (unexpected).", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Error: Invalid key format. Key must be a number.", false);
        } catch (NullPointerException e) {
            return new Response("Error: Missing key or ticket data for insert.", false);
        }
    }
}