package models;

import utility.Element;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Ticket sınıfı.
 */
public class Ticket extends Element {
    private static int nextId = 1;
    private final int id;               // > 0, benzersiz, otomatik üretilir
    private String name;                // null olamaz, boş olmamalı
    private Coordinates coordinates;    // null olamaz
    private final LocalDate creationDate; // otomatik oluşturulur
    private int price;                  // > 0
    private long discount;              // > 0, maksimum 100
    private String comment;             // 631 karakterden uzun olmamalı, null olabilir
    private TicketType type;            // null olamaz
    private Event event;                // null olabilir

    public Ticket(String name, Coordinates coordinates, int price, long discount, String comment, TicketType type, Event event) {
        this.id = nextId++;
        this.creationDate = LocalDate.now();
        this.name = name;
        this.coordinates = coordinates;
        this.price = price;
        this.discount = discount;
        this.comment = comment;
        this.type = type;
        this.event = event;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public long getDiscount() { return discount; }

    /**
     * Var olan Ticket nesnesinin alanlarını günceller (id ve creationDate sabit kalır).
     */
    public void update(Ticket newTicket) {
        this.name = newTicket.name;
        this.coordinates = newTicket.coordinates;
        this.price = newTicket.price;
        this.discount = newTicket.discount;
        this.comment = newTicket.comment;
        this.type = newTicket.type;
        this.event = newTicket.event;
    }

    public static Ticket createTicket(String name, Coordinates coordinates, int price, long discount, String comment, TicketType type, Event event) {
        if (name == null || name.trim().isEmpty() || coordinates == null || price <= 0 || discount <= 0 || discount > 100 || type == null || (comment != null && comment.length() > 631)) {
            throw new IllegalArgumentException("Invalid data for creating a Ticket");
        }
        return new Ticket(name, coordinates, price, discount, comment, type, event);
    }

    public static void updateNextId(LinkedHashMap<Long, Ticket> collection) {
        int maxId = collection.values().stream()
                .mapToInt(Ticket::getId)
                .max().orElse(0);
        nextId = maxId + 1;
    }

    @Override
    public int compareTo(Element o) {
        return Integer.compare(this.id, o.getId());
    }

    @Override
    public String toString() {
        return "[id " + id +
                ", name=" + name +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", price=" + price +
                ", discount=" + discount +
                ", comment=" + (comment == null ? "null" : "'" + comment + "'") +
                ", type=" + type +
                ", event=" + event + "]";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Ticket ticket && this.id == ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
