package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.User;
import server.managers.CollectionManager;

public class ClearCommand extends Command {
    public ClearCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        // The user is already verified by the CommandProcessor. We just need their ID.
        User user = request.getUser();
        int userId = collectionManager.verifyUser(user);

        // This method in CollectionManager is already designed to only clear the user's tickets
        collectionManager.clearCollection(userId);

        return new Response("Your entries in the collection have been cleared.", true);
    }
}