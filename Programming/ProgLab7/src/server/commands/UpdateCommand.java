package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.Ticket;
import server.managers.CollectionManager;

public class UpdateCommand extends Command {
    public UpdateCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        try {
            int id = Integer.parseInt(request.getArguments());
            Ticket ticketData = request.getTicketArgument();
            int userId = collectionManager.verifyUser(request.getUser());

            if (ticketData == null) {
                return new Response("Error: Ticket data is missing for update command.", false);
            }

            Ticket existingTicket = collectionManager.getById(id);
            if (existingTicket == null) {
                return new Response("Error: Ticket with ID " + id + " not found.", false);
            }

            // --- AUTHORIZATION CHECK ---
            if (existingTicket.getOwnerId() != userId) {
                return new Response("Error: Permission denied. You can only update tickets that you own.", false);
            }

            // Update fields while preserving id, key, and owner
            ticketData.setId(existingTicket.getId());
            ticketData.setKey(existingTicket.getKey());
            ticketData.setOwnerId(existingTicket.getOwnerId());

            if (collectionManager.updateInCollection(ticketData)) {
                return new Response("Ticket with ID " + id + " successfully updated.", true);
            } else {
                return new Response("Error: Failed to update ticket in the database.", false);
            }

        } catch (NumberFormatException e) {
            return new Response("Error: Invalid ID format. ID must be an integer.", false);
        }
    }
}