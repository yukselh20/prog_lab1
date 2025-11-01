package server.commands;

import common.dto.Request;
import common.dto.Response;
import common.models.User;
import server.managers.CollectionManager;

/**
 * Command to register a new user.
 */
public class RegisterCommand extends Command {
    public RegisterCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        User user = request.getUser();
        if (user == null || user.getUsername() == null || user.getPassword() == null || user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            return new Response("Error: Username and password cannot be empty for registration.", false);
        }

        int userId = collectionManager.addUser(user);

        if (userId != -1) {
            return new Response("User '" + user.getUsername() + "' registered successfully. You can now log in.", true);
        } else {
            return new Response("Error: This username is already taken. Please choose another one.", false);
        }
    }
}