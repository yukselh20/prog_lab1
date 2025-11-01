package server.db;

import common.models.*;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

/**
 * Data Access Object for handling ticket-related database operations.
 */
public class TicketDAO {
    private final DatabaseManager databaseManager;

    public TicketDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Inserts a new ticket into the database.
     * @param key The key for the ticket in the collection.
     * @param ticket The ticket object to insert.
     * @param userId The ID of the user who owns this ticket.
     * @return The newly created Ticket object with its database-generated ID, or null on failure.
     */
    //all methods will use try-with-recources to get connection.
    public Ticket insertTicket(long key, Ticket ticket, int userId) {
        String sql = "INSERT INTO tickets (obj_key, name, coordinates_x, coordinates_y, creation_date, price, discount, comment, ticket_type, event_name, event_date, event_type, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, key);
            ps.setString(2, ticket.getName());
            ps.setFloat(3, ticket.getCoordinates().x());
            ps.setFloat(4, ticket.getCoordinates().y());
            ps.setDate(5, Date.valueOf(ticket.getCreationDate()));
            ps.setInt(6, ticket.getPrice());
            ps.setLong(7, ticket.getDiscount());
            ps.setString(8, ticket.getComment());
            ps.setString(9, ticket.getType().toString());

            if (ticket.getEvent() != null) {
                ps.setString(10, ticket.getEvent().getName());
                ps.setTimestamp(11, Timestamp.from(ticket.getEvent().getDate().toInstant()));
                ps.setString(12, ticket.getEvent().getEventType().toString());
            } else {
                ps.setNull(10, Types.VARCHAR);
                ps.setNull(11, Types.TIMESTAMP_WITH_TIMEZONE);
                ps.setNull(12, Types.VARCHAR);
            }
            ps.setInt(13, userId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) return null;

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ticket.setId(generatedKeys.getInt("id"));
                    ticket.setOwnerId(userId);
                    ticket.setKey(key);
                    return ticket;
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error inserting ticket: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates an existing ticket in the database.
     */
    public boolean updateTicket(Ticket ticket) {
        String sql = "UPDATE tickets SET name=?, coordinates_x=?, coordinates_y=?, price=?, discount=?, comment=?, ticket_type=?, event_name=?, event_date=?, event_type=? WHERE id=?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ticket.getName());
            ps.setFloat(2, ticket.getCoordinates().x());
            ps.setFloat(3, ticket.getCoordinates().y());
            ps.setInt(4, ticket.getPrice());
            ps.setLong(5, ticket.getDiscount());
            ps.setString(6, ticket.getComment());
            ps.setString(7, ticket.getType().toString());
            if (ticket.getEvent() != null) {
                ps.setString(8, ticket.getEvent().getName());
                ps.setTimestamp(9, Timestamp.from(ticket.getEvent().getDate().toInstant()));
                ps.setString(10, ticket.getEvent().getEventType().toString());
            } else {
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.TIMESTAMP_WITH_TIMEZONE);
                ps.setNull(10, Types.VARCHAR);
            }
            ps.setInt(11, ticket.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error updating ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a ticket from the database by its key.
     */
    public boolean deleteTicketByKey(long key) {
        String sql = "DELETE FROM tickets WHERE obj_key = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, key);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error deleting ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clears all tickets belonging to a specific user.
     */
    public boolean clearTicketsByUserId(int userId) {
        String sql = "DELETE FROM tickets WHERE user_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true; // Returns true even if no rows were deleted
        } catch (SQLException e) {
            System.err.println("DB Error clearing tickets: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads all tickets from the database into a map.
     * @return A LinkedHashMap of tickets, with the object key as the map key.
     */
    public LinkedHashMap<Long, Ticket> loadCollection() {
        LinkedHashMap<Long, Ticket> collection = new LinkedHashMap<>();
        String sql = "SELECT * FROM tickets";
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Coordinates coordinates = new Coordinates(rs.getFloat("coordinates_x"), rs.getFloat("coordinates_y"));

                Event event = null;
                String eventName = rs.getString("event_name");
                if (eventName != null) {
                    Timestamp eventTimestamp = rs.getTimestamp("event_date");
                    ZonedDateTime eventDate = eventTimestamp != null ? ZonedDateTime.ofInstant(eventTimestamp.toInstant(), ZoneId.systemDefault()) : null;
                    EventType eventType = EventType.valueOf(rs.getString("event_type"));
                    event = new Event(eventName, eventDate, eventType);
                }

                Ticket ticket = new Ticket(
                        rs.getString("name"),
                        coordinates,
                        rs.getInt("price"),
                        rs.getLong("discount"),
                        rs.getString("comment"),
                        TicketType.valueOf(rs.getString("ticket_type")),
                        event
                );
                ticket.setId(rs.getInt("id"));
                ticket.setOwnerId(rs.getInt("user_id"));

                long key = rs.getLong("obj_key");
                ticket.setKey(key);

                collection.put(key, ticket);
            }
            System.out.println("Loaded " + collection.size() + " tickets from the database.");
        } catch (SQLException e) {
            System.err.println("Error loading collection from database: " + e.getMessage());
            // It might be better to exit if the initial load fails
            System.exit(1);
        }
        return collection;
    }
}