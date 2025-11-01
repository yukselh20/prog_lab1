package common.models;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Event (etkinlik) sınıfı.
 */
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final ZonedDateTime date;
    private final EventType eventType;

    public Event(String name, ZonedDateTime date, EventType eventType) {
        this.name = name;
        this.date = date;
        this.eventType = eventType;
    }

    // Getters needed by TicketDAO
    public String getName() { return name; }
    public ZonedDateTime getDate() { return date; }
    public EventType getEventType() { return eventType; }

    public static Event createEvent(String name, ZonedDateTime date, EventType eventType) {
        if (name == null || name.trim().isEmpty() || eventType == null) {
            throw new IllegalArgumentException("Invalid data for creating an Event");
        }
        return new Event(name, date, eventType);
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", eventType=" + eventType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(name, event.name) && Objects.equals(date, event.date) && eventType == event.eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, eventType);
    }
}