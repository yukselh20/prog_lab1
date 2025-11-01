package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.Ticket;
import server.managers.CollectionManager;

import java.util.List;
import java.util.stream.Collectors;

public class FilterStartsWithNameCommand extends Command {
    public FilterStartsWithNameCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        String prefix = request.getArguments();
        if (prefix == null) {
            return new Response("Error: Name prefix is missing for filter.", false);
        }
        List<Ticket> filteredList = collectionManager.getCollection().values().stream()
                .filter(ticket -> ticket.getName() != null && ticket.getName().startsWith(prefix))
                .collect(Collectors.toList());

        if (filteredList.isEmpty()) {
            return new Response("No tickets found with name starting with '" + prefix + "'.", true, filteredList);
        } else {
            return new Response("Tickets with name starting with '" + prefix + "':", true, filteredList);
        }
    }
}