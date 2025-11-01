package server.commands;

import common.dto.Request;
import common.dto.Response;
import server.managers.CollectionManager;

public class ClearCommand extends Command {
    public ClearCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        collectionManager.clearCollection();
        return new Response("Collection cleared successfully.", true);
    }
}