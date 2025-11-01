package server;

import server.managers.CollectionManager;
import server.managers.DumpManager;
import server.network.UDPServer;
import server.processing.CommandProcessor;

public class ServerMain {

    private static final int DEFAULT_PORT = 54321; // Örnek port

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port <= 0 || port > 65535) {
                    System.err.println("ERROR: Invalid port number: " + args[0] + ". Default port: " + DEFAULT_PORT );
                    port = DEFAULT_PORT;
                }
            } catch (NumberFormatException e) {
                System.err.println("ERROR: Port number must be a number: " + args[0] + ". Default port: " + DEFAULT_PORT );
                port = DEFAULT_PORT;
            }
        }

        // logger.info("Sunucu başlatılıyor...");
        System.out.println("Initialising server...");


        String fileName = System.getenv("TICKET_FILE");
        if (fileName == null || fileName.isEmpty()) {
            // logger.error("Ortam değişkeni 'TICKET_FILE' tanımlanmamış. Sunucu durduruluyor.");
            System.err.println("ERROR: Environment variable ‘TICKET_FILE’ is not defined. The server is being stopped.");
            System.exit(1); // Dosya adı olmadan çalışamaz
        }
        // logger.info("Koleksiyon dosyası kullanılacak: {}", fileName);
        System.out.println("Collection file: " + fileName);


        // Manager'ları başlat
        DumpManager dumpManager = new DumpManager(fileName);
        CollectionManager collectionManager = new CollectionManager(dumpManager);
        CommandProcessor commandProcessor = new CommandProcessor(collectionManager);
        UDPServer udpServer = new UDPServer(port, commandProcessor);

        // Shutdown hook ekle (uygulama kapanırken koleksiyonu kaydetmek için)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SHUTDOWN] Saving collection …");
            udpServer.stop();                  // running = false + selector.wakeup()
            collectionManager.saveCollection();
            System.out.println("[SHUTDOWN] Collection saved.");
        }));


        // Ağ sunucusunu başlat ve çalıştır
        // logger.info("UDP Sunucu {} portunda dinlemeye başlıyor.", port);
        System.out.println("UDP Server starts listening on port: " + port);
        udpServer.run(); // Bu metot sunucu ana döngüsünü içerecek
    }
}