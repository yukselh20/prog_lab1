package models;

/**
 * Event tiplerini belirleyen enum.
 */
public enum EventType {
    CONCERT,
    E_SPORTS,
    BASKETBALL;

    /**
     * Enum elemanlarını virgülle ayrılmış bir string olarak döndürür.
     */
    public static String names() {
        StringBuilder sb = new StringBuilder();
        for(EventType et : EventType.values()){
            sb.append(et.name()).append(", ");
        }
        return sb.substring(0, sb.length()-2);
    }
}
