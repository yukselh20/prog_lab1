package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.Ticket;
import server.managers.CollectionManager;
import java.util.Map;

public class RemoveKeyCommand extends Command {
    public RemoveKeyCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        try {
            long key = Long.parseLong(request.getArguments());
            int userId = collectionManager.verifyUser(request.getUser());

            // We must get the collection to check ownership BEFORE deleting
            Map<Long, Ticket> collection = collectionManager.getCollection();
            Ticket ticketToRemove = collection.get(key);

            if (ticketToRemove == null) {
                return new Response("Error: Ticket with key " + key + " not found.", false);
            }

            // --- AUTHORIZATION CHECK ---
            if (ticketToRemove.getOwnerId() != userId) {
                return new Response("Error: Permission denied. You can only remove tickets that you own.", false);
            }

            if (collectionManager.removeFromCollection(key)) {
                return new Response("Ticket with key " + key + " successfully removed.", true);
            } else {
                return new Response("Error: Failed to remove ticket from database.", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Error: Invalid key format. Key must be a number.", false);
        }
    }
}