package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.Ticket;
import server.managers.CollectionManager;

public class RemoveLowerCommand extends Command {
    public RemoveLowerCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        Ticket referenceTicket = request.getTicketArgument();
        if (referenceTicket == null) {
            return new Response("Error: Reference ticket data is missing for remove_lower.", false);
        }
        int initialSize = collectionManager.collectionSize();

        collectionManager.getCollection().values().removeIf(t -> t != null && t.getPrice() < referenceTicket.getPrice());
        int removedCount = initialSize - collectionManager.collectionSize();
        return new Response(removedCount + " elements removed (those with price lower than reference).", true);
    }
}