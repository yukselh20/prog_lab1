package common.dto; // dto paketine taşı

// import common.models.Ticket; // Payload içinde olacaksa spesifik import gerekmeyebilir
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class Response implements Serializable { // Response artık dto altında
    @Serial
    private static final long serialVersionUID = 302L;

    private final String message;
    private final boolean success;
    private final Object payload; // Genel veri taşıyıcı (örn. List<Ticket> veya String veya null)

    // Constructorlar
    public Response(String message, boolean success) {
        this(message, success, null);
    }

    public Response(String message, boolean success, Object payload) {
        this.message = message;
        this.success = success;
        this.payload = payload;
    }

    // Getterlar
    public boolean isSuccess() { return success; }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(success ? "SUCCESS" : "ERROR").append("]");
        if (message != null && !message.isEmpty()) {
            sb.append(": ").append(message);
        }
        // Payload'ı daha genel yazdırma
        if (payload instanceof List<?> listPayload) { // Java 16+ pattern matching
            if (!listPayload.isEmpty()) {
                sb.append("\n-- Data --\n");
                sb.append(listPayload.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining("\n")));
            } else {
                sb.append("\n-- Data: Empty List --");
            }
        } else if (payload != null) {
            sb.append("\n-- Payload: ").append(payload.toString()).append(" --");
        }
        return sb.toString();
    }
}