package server.managers;

import common.models.Ticket;
import common.models.User;
import server.db.TicketDAO;
import server.db.UserDAO;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Manages the in-memory collection and synchronizes it with the database.
 * Now with ReadWriteLock for thread-safe access.
 */
public class CollectionManager {
    private LinkedHashMap<Long, Ticket> collection;
    private final TicketDAO ticketDAO;
    private final UserDAO userDAO;
    private LocalDateTime lastInitTime;

    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public CollectionManager(TicketDAO ticketDAO, UserDAO userDAO) {
        this.ticketDAO = ticketDAO;
        this.userDAO = userDAO;
        loadCollection();
    }

    public int verifyUser(User user) {
        return userDAO.verifyUser(user);
    }
    public int addUser(User user) {
        return userDAO.addUser(user);
    }

    // --- READ OPERATIONS (use read lock) ---

    public Map<Long, Ticket> getCollection() {
        lock.readLock().lock();
        try {
            return new LinkedHashMap<>(collection); // Return a copy for safety
        } finally {
            lock.readLock().unlock();
        }
    }


    public String getCollectionInfo() {
        lock.readLock().lock();
        try {
            return "Type: " + collection.getClass().getName() + "\n" +
                    "Initialization date: " + (lastInitTime != null ? lastInitTime : "unknown") + "\n" +
                    "Number of elements: " + collection.size() + "\n";
        } finally {
            lock.readLock().unlock();
        }
    }

    public Ticket getById(int id) {
        lock.readLock().lock();
        try {
            return collection.values().stream()
                    .filter(ticket -> ticket.getId() == id)
                    .findFirst().orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean containsKey(long key) {
        lock.readLock().lock();
        try {
            return collection.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Collection<Ticket> getAllTicketsCollection() {
        lock.readLock().lock();
        try {
            return collection.values().stream().collect(Collectors.toList()); // Return a copy
        } finally {
            lock.readLock().unlock();
        }
    }

    // --- WRITE OPERATIONS (use write lock) ---

    private void loadCollection() {
        lock.writeLock().lock();
        try {
            this.collection = ticketDAO.loadCollection();
            this.lastInitTime = LocalDateTime.now();
            System.out.println("Collection has been loaded into memory.");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean addToCollection(long key, Ticket ticket, int userId) {
        lock.writeLock().lock();
        try {
            Ticket newTicketFromDB = ticketDAO.insertTicket(key, ticket, userId);
            if (newTicketFromDB != null) {
                collection.put(key, newTicketFromDB);
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean removeFromCollection(long key) {
        lock.writeLock().lock();
        try {
            if (ticketDAO.deleteTicketByKey(key)) {
                collection.remove(key);
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clearCollection(int userId) {
        lock.writeLock().lock();
        try {
            if (ticketDAO.clearTicketsByUserId(userId)) {
                collection.entrySet().removeIf(entry -> entry.getValue().getOwnerId() == userId);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean updateInCollection(Ticket ticket) {
        lock.writeLock().lock();
        try {
            if (ticketDAO.updateTicket(ticket)) {
                collection.put(ticket.getKey(), ticket);
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}