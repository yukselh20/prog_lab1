package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.Ticket;
import server.managers.CollectionManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PrintDescendingCommand extends Command {
    public PrintDescendingCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        List<Ticket> sortedList = collectionManager.getAllTicketsCollection().stream()
                .sorted(Comparator.comparing(Ticket::getPrice).reversed())
                .collect(Collectors.toList());

        if (sortedList.isEmpty()) {
            return new Response("Collection is empty.", true, sortedList);
        } else {
            return new Response("Collection elements (sorted by price descending):", true, sortedList);
        }
    }
}