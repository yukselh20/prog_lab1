package server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Validates and initializes the database schema using "IF NOT EXISTS" for maximum reliability.
 */
public class DatabaseSchemaValidator {
    private final DatabaseManager databaseManager;

    public DatabaseSchemaValidator(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Ensures the required tables exist by using the database's native
     * "CREATE TABLE IF NOT EXISTS" command. This is the most robust approach.
     */
    public void validateAndInitialize() {
        System.out.println("Ensuring database schema is up to date...");
        // We must create users before tickets due to the foreign key dependency.
        try {
            createUsersTableIfNotExists();
            createTicketsTableIfNotExists();
            System.out.println("Database schema is ready.");
        } catch (SQLException e) {
            System.err.println("FATAL: Could not initialize database schema: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Executes the SQL to create the 'users' table and its sequence if they don't already exist.
     */
    private void createUsersTableIfNotExists() throws SQLException {
        // Use a single try-with-resources for the entire transaction
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            // The "IF NOT EXISTS" clause handles the check inside the database itself.
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "    id SERIAL PRIMARY KEY," +
                            "    username VARCHAR(255) UNIQUE NOT NULL," +
                            "    password_hash VARCHAR(255) NOT NULL" +
                            ");"
            );
        }
    }

    /**
     * Executes the SQL to create the 'tickets' table and its sequence if they don't already exist.
     */
    private void createTicketsTableIfNotExists() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tickets (" +
                            "    id SERIAL PRIMARY KEY," +
                            "    obj_key BIGINT UNIQUE NOT NULL," +
                            "    name VARCHAR(255) NOT NULL," +
                            "    coordinates_x FLOAT NOT NULL," +
                            "    coordinates_y FLOAT NOT NULL," +
                            "    creation_date DATE NOT NULL," +
                            "    price INT NOT NULL," +
                            "    discount BIGINT NOT NULL," +
                            "    comment VARCHAR(631)," +
                            "    ticket_type VARCHAR(50) NOT NULL," +
                            "    event_name VARCHAR(255)," +
                            "    event_date TIMESTAMP WITH TIME ZONE," +
                            "    event_type VARCHAR(50)," +
                            "    user_id INT NOT NULL," +
                            "    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ");"
            );
        }
    }
}