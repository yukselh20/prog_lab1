package server.commands;

import common.dto.Request;
import common.dto.Response;
import server.managers.CollectionManager;

public class InfoCommand extends Command {
    public InfoCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        String info = collectionManager.getCollectionInfo();
        return new Response(info, true);
    }
}