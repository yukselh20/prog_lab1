package common.models;

import java.io.Serializable;

/**
 * Ticket tiplerini belirleyen enum.
 */
public enum TicketType implements Serializable {
    VIP,
    USUAL,
    BUDGETARY,
    CHEAP;

    /**
     * Enum elemanlarını virgülle ayrılmış bir string olarak döndürür.
     */
    private static final long serialVersionUID = 1L;
    public static String names() {
        StringBuilder sb = new StringBuilder();
        for(TicketType t : TicketType.values()){
            sb.append(t.name()).append(", ");
        }
        return sb.substring(0, sb.length()-2);
    }
}
