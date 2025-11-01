package common.models;

import java.io.Serializable;

/**
 * Coordinates sınıfı.
 *
 * @param x > -661
 * @param y null olamaz, > -493
 */
public record Coordinates(float x, float y) implements Serializable {
    private static final long serialVersionUID = 1L;
    public static Coordinates createCoordinates(float x, float y) {
        if (x <= -661 || y <= -493) {
            throw new IllegalArgumentException("Invalid data for creating a Ticket");
        }
        return new Coordinates(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates that)) return false;
        return Float.compare(this.x, that.x) == 0 &&
                Float.compare(this.y, that.y) == 0;
    }
}