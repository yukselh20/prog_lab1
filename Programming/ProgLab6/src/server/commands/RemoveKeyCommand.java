package server.commands;

import common.dto.Request;
import common.dto.Response;
import server.managers.CollectionManager;

public class RemoveKeyCommand extends Command {
    public RemoveKeyCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        try {
            Long key = Long.parseLong(request.getArguments());
            boolean removed = collectionManager.removeFromCollection(key);
            if (removed) {
                return new Response("Ticket with key " + key + " successfully removed.", true);
            } else {
                return new Response("Error: Ticket with key " + key + " not found.", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Error: Invalid key format. Key must be a number.", false);
        } catch (NullPointerException e) {
            return new Response("Error: Missing key for remove_key.", false);
        }
    }
}