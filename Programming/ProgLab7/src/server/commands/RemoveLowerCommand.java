package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.Ticket;
import server.managers.CollectionManager;
import java.util.Map;

public class RemoveLowerCommand extends Command {
    public RemoveLowerCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        Ticket referenceTicket = request.getTicketArgument();
        int userId = collectionManager.verifyUser(request.getUser());

        if (referenceTicket == null) {
            return new Response("Error: Reference ticket data is missing for remove_lower.", false);
        }

        Map<Long, Ticket> originalCollection = collectionManager.getCollection();
        int removedCount = 0;

        // Iterate over a copy of the keys to avoid ConcurrentModificationException
        for (long key : originalCollection.keySet()) {
            Ticket ticket = originalCollection.get(key);
            // --- AUTHORIZATION AND LOGIC CHECK ---
            if (ticket.getOwnerId() == userId && ticket.compareTo(referenceTicket) < 0) {
                if (collectionManager.removeFromCollection(key)) {
                    removedCount++;
                }
            }
        }

        return new Response(removedCount + " of your tickets were removed.", true);
    }
}