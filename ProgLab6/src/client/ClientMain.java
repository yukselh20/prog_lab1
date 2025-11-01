package client;

import client.network.UDPClient;
import client.utility.Interrogator;
import client.utility.Runner;
import client.utility.console.Console;
import client.utility.console.StandardConsole;

import java.util.Scanner;

public class ClientMain {
    // private static final Logger logger = LoggerFactory.getLogger(ClientMain.class);

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 54321; // Sunucu ile aynı port


    public static void main(String[] args) {
        // logger.info("Client starting...");
        System.out.println("Client starting...");

        // Host ve port argümanlardan veya varsayılanlardan alınabilir
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        // (Argüman parse etme kodu eklenebilir - ServerMain'deki gibi)

        // logger.info("Connecting to server at {}:{}", host, port);
        System.out.println("Connecting to server at " + host + ":" + port);


        Interrogator.setUserScanner(new Scanner(System.in));
        Console console = new StandardConsole();

        // Ağ yöneticisini oluştur (henüz sadece taslak)
        UDPClient udpClient = new UDPClient(host, port);

        // Komut oluşturma ve çalıştırma mantığını yönetecek Runner
        // Runner artık doğrudan CollectionManager'a değil, UDPClient üzerinden sunucuya istek gönderecek
        Runner runner = new Runner(console, udpClient);

        runner.interactiveMode();

        // Kapanış
        // logger.info("Client shutting down.");
        System.out.println("Client shutting down.");
        udpClient.close(); // Ağ kaynaklarını serbest bırak
    }
}
