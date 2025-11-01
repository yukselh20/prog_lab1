package models;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Event (etkinlik) sınıfı.
 */
public class Event {
    private final String name;             // null olamaz, boş olmamalı
    private final ZonedDateTime date;      // null olabilir
    private final EventType eventType;     // null olamaz

    public Event(String name, ZonedDateTime date, EventType eventType) {
        this.name = name;
        this.date = date;
        this.eventType = eventType;
    }
    // Event nesnesi oluşturmak için factory metodu. static factory method, nesne oluşmadan
    // önce gerekli doğrulamaları yapar doğru ise nesne oluşturur.
    public static Event createEvent(String name, ZonedDateTime date, EventType eventType) {
        if (name == null || name.trim().isEmpty() || eventType == null) {
            throw new IllegalArgumentException("Invalid data for creating an Event");
        }
        return new Event(name, date, eventType);
    }

    public String getName() { return name; }


    // @Override
    //public boolean validate() {
    //    return name != null && !name.trim().isEmpty() && eventType != null;
    //}

    @Override
    public String toString() {
        return "Event [name=" + name +
                ", date=" + (date == null ? "null" : date.toString()) +
                ", eventType=" + eventType + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(name, event.name) &&
                Objects.equals(date, event.date) &&
                eventType == event.eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, eventType);
    }
}
