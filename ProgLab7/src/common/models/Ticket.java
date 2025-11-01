package common.models;

import common.utility.Element;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Ticket sınıfı.
 */
public class Ticket extends Element implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id; // Removed final to allow setting from DB
    private final String name;
    private final Coordinates coordinates;
    private final LocalDate creationDate;
    private final int price;
    private final long discount;
    private final String comment;
    private final TicketType type;
    private final Event event;
    private int ownerId;

    public Ticket(String name, Coordinates coordinates, int price, long discount, String comment, TicketType type, Event event) {
        // Id artık burada ayarlanmamaktadır. Veritabanı dizisinden(db sequence) gelecektir.
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

    //Setter for ID
    public void setId(int id) {
        this.id = id;
    }

    // TicketDAO için getterlar
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public LocalDate getCreationDate() { return creationDate; }
    public int getPrice() { return price; }
    public long getDiscount() { return discount; }
    public String getComment() { return comment; }
    public TicketType getType() { return type; }
    public Event getEvent() { return event; }

    // New: Getter and Setter for ownerId
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    // Static factory ve diğer methodlar lab6
    // Note: The logic for nextId handled by the database sequence.
    public static Ticket createTicket(String name, Coordinates coordinates, int price, long discount, String comment, TicketType type, Event event) {
        if (name == null || name.trim().isEmpty() || coordinates == null || price <= 0 || discount <= 0 || discount > 100 || type == null || (comment != null && comment.length() > 631)) {
            throw new IllegalArgumentException("Invalid data for creating a Ticket");
        }
        return new Ticket(name, coordinates, price, discount, comment, type, event);
    }

    @Override
    public int compareTo(Element o) {
        return Integer.compare(this.id, o.getId());
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", key=" + getKey() +
                ", ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", price=" + price +
                ", discount=" + discount +
                ", comment='" + comment + '\'' +
                ", type=" + type +
                ", event=" + event +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id == ticket.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}