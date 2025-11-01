package client;

import client.network.UDPClient;
import client.utility.Interrogator;
import client.utility.Runner;
import client.utility.console.Console;
import client.utility.console.StandardConsole;

import java.util.Scanner;

public class ClientMain {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 54321;

    public static void main(String[] args) {
        // Host and port can be taken from args or defaults
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        // (Argument parsing code can be added here if needed)

        System.out.println("Client starting...");
        System.out.println("Connecting to server at " + host + ":" + port);

        Interrogator.setUserScanner(new Scanner(System.in));
        Console console = new StandardConsole();
        UDPClient udpClient = new UDPClient(host, port);

        // Runner will now handle the user interaction logic
        Runner runner = new Runner(console, udpClient);
        runner.run(); // Start the main client loop

        System.out.println("Client shutting down.");
    }
}