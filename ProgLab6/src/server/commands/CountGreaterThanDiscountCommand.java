package server.commands;

import common.dto.Request;
import common.dto.Response;
import server.managers.CollectionManager;

public class CountGreaterThanDiscountCommand extends Command {
    public CountGreaterThanDiscountCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    @Override
    public Response execute(Request request) {
        try {
            long discount = Long.parseLong(request.getArguments());
            long count = collectionManager.getCollection().values().stream()
                    .filter(ticket -> ticket.getDiscount() > discount)
                    .count();
            return new Response("Number of tickets with discount greater than " + discount + ": " + count, true);
        } catch (NumberFormatException e) {
            return new Response("Error: Invalid discount format. Discount must be a number.", false);
        } catch (NullPointerException e) {
            return new Response("Error: Missing discount value.", false);
        }
    }
}