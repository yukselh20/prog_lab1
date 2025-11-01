package utility.console;

/**
 * Konsol ile iletişim için temel arayüz.
 */
public interface Console {
    void print(Object obj);
    void println(Object obj);
    void printError(Object obj);
    void printTable(Object obj1, Object obj2);
    void ps1();
    String getPS1();

}