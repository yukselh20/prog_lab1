package server.commands;

import common.dto.Request;
import common.dto.Response;
import server.managers.CollectionManager;

public class HelpCommand extends Command {
    public HelpCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        String helpText = """
                Available commands:
                help : Display help for available commands
                info : Display collection information
                show : Display all items in the collection
                insert <key> {element} : Add a new item with the specified key
                update <id> {element} : Update the value of the collection item whose id is equal to the given one
                remove_key <key> : Remove an item from the collection by its key
                clear : Clear the collection
                execute_script <file_name> : Read and execute a script from a specified file (Server side execution)
                remove_lower {element} : Remove from the collection all items smaller than the specified value
                replace_if_lower <key> {element} : Replace the value by key if the new value is less than the old one
                count_greater_than_discount <discount> : Output the number of items whose discount field value is greater than the specified value
                filter_starts_with_name <name> : Output items whose name field value starts with the specified substring
                print_descending : Output the elements of the collection in descending order
                """;
        return new Response(helpText, true);
    }
}