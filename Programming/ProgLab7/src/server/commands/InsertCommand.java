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
            int userId = collectionManager.verifyUser(request.getUser()); // Get the authenticated user's ID

            if (ticket == null) {
                return new Response("Error: Ticket data is missing for insert command.", false);
            }
            if (collectionManager.containsKey(key)) {
                return new Response("Error: Key " + key + " already exists.", false);
            }

            boolean success = collectionManager.addToCollection(key, ticket, userId);
            if (success) {
                return new Response("Ticket successfully added with key " + key + ".", true);
            } else {
                return new Response("Error: Failed to add ticket to the database.", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Error: Invalid key format. Key must be a number.", false);
        }
    }
}