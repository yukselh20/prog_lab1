package server.db;

import common.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for handling user-related database operations.
 */
public class UserDAO {
    private final DatabaseManager databaseManager;

    public UserDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Adds a new user to the database.
     * @param user The user object containing username and password.
     * @return The generated ID of the new user, or -1 if the user already exists or an error occurs.
     */
    public int addUser(User user) {
        String insertUserSQL = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        // MODIFIED: Use try-with-resources to get a connection from the pool
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertUserSQL, new String[]{"id"})) {

            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordHasher.hashPassword(user.getPassword()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return -1; // Insert failed
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return new user ID
                } else {
                    return -1; // Failed to get ID
                }
            }
        } catch (SQLException e) {
            // PSQLException with code 23505 is for unique_violation (username already exists)
            if ("23505".equals(e.getSQLState())) {
                // This is an expected failure, not a fatal error
            } else {
                System.err.println("Error adding user: " + e.getMessage());
            }
            return -1;
        }
    }

    /**
     * Verifies user credentials and returns the user's ID if they are valid.
     * @param user The user object containing username and password.
     * @return The user's ID if authentication is successful, otherwise -1.
     */
    public int verifyUser(User user) {
        String selectUserSQL = "SELECT id, password_hash FROM users WHERE username = ?";

        // MODIFIED: Use try-with-resources to get a connection from the pool
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(selectUserSQL)) {

            ps.setString(1, user.getUsername());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    int userId = rs.getInt("id");
                    String providedHash = PasswordHasher.hashPassword(user.getPassword());

                    if (storedHash.equals(providedHash)) {
                        return userId; // Authentication successful
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying user: " + e.getMessage());
        }
        return -1; // User not found, password incorrect, or DB error
    }
}