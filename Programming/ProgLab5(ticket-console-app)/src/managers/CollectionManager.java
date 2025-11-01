package managers;

import models.Ticket;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.lang.*;

/**
 * Ticket nesnelerinin tutulduğu LinkedHashMap koleksiyonunu yöneten sınıf.
 */
public class CollectionManager {
    private LinkedHashMap<Long, Ticket> collection = new LinkedHashMap<>();
    private LocalDateTime lastInitTime;
    private final DumpManager dumpManager;

    public CollectionManager(DumpManager dumpManager) {
        this.dumpManager = dumpManager;
        loadCollection();
        initializeKeys(); // Key değerlerini Ticket nesnelerine set et
    }

    public LinkedHashMap<Long, Ticket> getCollection() {
        return collection;
    }

    public void initializeKeys() {
    }

    public String collectionType() { return collection.getClass().getName(); }
    public int collectionSize() { return collection.size(); }

    public Ticket getById(int id) {
        return collection.values().stream()
                .filter(ticket -> ticket.getId() == id)
                .findFirst().orElse(null);
    }


    public void addToCollection(Long key, Ticket ticket) {
         // Key değerini Ticket nesnesine ata
        collection.put(key, ticket);
    }

    public void removeFromCollection(Long key) {
        collection.remove(key);
    }

    public void clearCollection() {
        collection.clear();
    }

    public void saveCollection() {
        dumpManager.writeCollection(collection);
        lastInitTime = LocalDateTime.now();
    }

    private void loadCollection() {
        collection = dumpManager.readCollection();
        lastInitTime = LocalDateTime.now();
    }

    public String getCollectionInfo() {
        return "Type: " + collectionType() + "\n" +
                "Initialization date: " + lastInitTime + "\n" +
                "Number of elements: " + collectionSize() + "\n";
    }

    public String getAllTickets() {
        if(collection.isEmpty()) return "The collection is empty!";
        return collection.entrySet().stream()
                .map(entry -> "Key: " + entry.getKey() + " - " + entry.getValue())
                .collect(Collectors.joining("\n\n"));
    }
}

