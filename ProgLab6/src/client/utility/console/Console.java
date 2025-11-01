package client.utility.console;

/**
 * Konsol ile iletişim için temel arayüz.
 */
public interface Console {
    void print(Object obj);
    void println(Object obj);
    void printError(Object obj);
    void ps1();
    String getPS1();

}