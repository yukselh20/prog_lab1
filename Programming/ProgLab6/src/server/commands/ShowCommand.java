package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.Ticket;
import server.managers.CollectionManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShowCommand extends Command {
    public ShowCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        List<Ticket> sortedList = new ArrayList<>(collectionManager.getAllTicketsCollection())
                .stream()
                .sorted(Comparator.comparing(Ticket::getName))
                .collect(Collectors.toList());

        if (sortedList.isEmpty()) {
            return new Response("Collection is empty.", true, sortedList);
        } else {
            return new Response("Collection elements (sorted by name):", true, sortedList);
        }
    }
}