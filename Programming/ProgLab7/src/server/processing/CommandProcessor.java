package server.processing;

import common.dto.CommandType;
import common.dto.Request;
import common.dto.Response;
import server.commands.*;
import server.managers.CollectionManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Bu sınıf, clientten gelen Request DTO'larını işleyen merkezi bir dağıtıcıdır (dispatcher).
 * Gelen komutun türüne göre ilgili komut nesnesini çalıştırır.
 */

public class CommandProcessor {
    // Komut türlerini (CommandType) ilgili komut nesnelerine (Command) eşleyen bir harita.
    // Bu, if-else veya switch-case bloklarını ortadan kaldırır.
    private final Map<CommandType, Command> commandMap = new HashMap<>();
    private final CollectionManager collectionManager;

    public CommandProcessor(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        registerCommands();
    }


    /**
     * Tüm komut sınıflarını başlatır ve commandMap'e koyar.
     * Yeni bir komut eklendiğinde, sadece bu metoda eklenmesi yeterlidir.
     */

    private void registerCommands() {
        commandMap.put(CommandType.HELP, new HelpCommand(collectionManager));
        commandMap.put(CommandType.INFO, new InfoCommand(collectionManager));
        commandMap.put(CommandType.SHOW, new ShowCommand(collectionManager));
        commandMap.put(CommandType.INSERT, new InsertCommand(collectionManager));
        commandMap.put(CommandType.UPDATE, new UpdateCommand(collectionManager));
        commandMap.put(CommandType.REMOVE_KEY, new RemoveKeyCommand(collectionManager));
        commandMap.put(CommandType.CLEAR, new ClearCommand(collectionManager));
        commandMap.put(CommandType.REMOVE_LOWER, new RemoveLowerCommand(collectionManager));
        commandMap.put(CommandType.REPLACE_IF_LOWER, new ReplaceIfLowerCommand(collectionManager));
        commandMap.put(CommandType.COUNT_GREATER_THAN_DISCOUNT, new CountGreaterThanDiscountCommand(collectionManager));
        commandMap.put(CommandType.FILTER_STARTS_WITH_NAME, new FilterStartsWithNameCommand(collectionManager));
        commandMap.put(CommandType.PRINT_DESCENDING, new PrintDescendingCommand(collectionManager));
        commandMap.put(CommandType.REGISTER, new RegisterCommand(collectionManager));
    }

    /**
     * Gelen isteği işleyen ana metot.
     * Sadece mapten doğru komutu bulur ve execute metodunu çağırır.
     * @param request İstemciden gelen istek.
     * @return Sunucunun oluşturduğu yanıt.
     */

    public Response process(Request request) {
        if (request == null || request.getCommandType() == null) {
            return new Response("Error: Invalid request received by server.", false);
        }

        CommandType type = request.getCommandType();

        // --- Central Authentication ---
        if (type != CommandType.REGISTER) {
            if (request.getUser() == null) {
                return new Response("Error: Authentication is required for this command.", false);
            }
            int userId = collectionManager.verifyUser(request.getUser());
            if (userId == -1) {
                return new Response("Error: Authentication failed. Invalid username or password.", false);
            }
        }

        Command command = commandMap.get(type);
        if (command == null) {
            return new Response("Error: Unknown command type '" + type + "' received by server.", false);
        }

        try {
            return command.execute(request);
        } catch (Exception e) {
            System.err.println("Unexpected error processing command '" + type + "': " + e.getMessage());
            e.printStackTrace(); // Good for debugging
            return new Response("Error: An unexpected server error occurred while processing command '" + type + "'.", false);
        }
    }
}