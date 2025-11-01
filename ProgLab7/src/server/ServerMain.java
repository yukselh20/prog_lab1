package server;

import server.db.DatabaseManager;
import server.db.DatabaseSchemaValidator;
import server.db.TicketDAO;
import server.db.UserDAO;
import server.managers.CollectionManager;
import server.network.UDPServer;
import server.processing.CommandProcessor;

/**
 * Main class for the server application.
 * Initializes all managers and starts the server.
 */
public class ServerMain {
    private static final int DEFAULT_PORT = 54321;

    public static void main(String[] args) {
        try {
            int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

            // Initialize the DatabaseManager with the pool
            DatabaseManager databaseManager = new DatabaseManager();
            System.out.println("Database connection pool established.");

            // Validate and Initialize Schema
            DatabaseSchemaValidator schemaValidator = new DatabaseSchemaValidator(databaseManager);
            schemaValidator.validateAndInitialize();

            // DAOs now take the DatabaseManager
            UserDAO userDAO = new UserDAO(databaseManager);
            TicketDAO ticketDAO = new TicketDAO(databaseManager);

            // Initialize Managers
            CollectionManager collectionManager = new CollectionManager(ticketDAO, userDAO);
            CommandProcessor commandProcessor = new CommandProcessor(collectionManager);
            UDPServer udpServer = new UDPServer(port, commandProcessor);

            // Shutdown hook now closes the connection pool
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n[SHUTDOWN] Stopping server and closing resources...");
                udpServer.stop();
                databaseManager.close();
                System.out.println("[SHUTDOWN] Database connection pool closed.");
            }));

            // Start the server
            System.out.println("UDP Server starts listening on port: " + port);
            udpServer.run();

        } catch (NumberFormatException e) {
            System.err.println("ERROR: Port must be a valid integer.");
        } catch (Exception e) {
            System.err.println("FATAL: Failed to initialize the server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}