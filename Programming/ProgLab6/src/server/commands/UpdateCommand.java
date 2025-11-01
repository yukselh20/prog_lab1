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
            if (ticketData == null) {
                return new Response("Error: Ticket data is missing for update command.", false);
            }

            Ticket existingTicket = collectionManager.getById(id);
            if (existingTicket == null) {
                return new Response("Error: Ticket with ID " + id + " not found.", false);
            }

            existingTicket.update(ticketData);

            return new Response("Ticket with ID " + id + " successfully updated.", true);
        } catch (NumberFormatException e) {
            return new Response("Error: Invalid ID format. ID must be an integer.", false);
        } catch (NullPointerException e) {
            return new Response("Error: Missing ID or ticket data for update.", false);
        }
    }
}