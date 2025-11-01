package server.network;

import common.dto.Request;
import common.dto.Response;
import common.utility.SerializationUtils;
import server.processing.CommandProcessor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A multithreaded UDP server that follows the specific threading model requirements.
 * - Main thread: Reads request packets.
 * - CachedThreadPool: Processes requests (deserialization and business logic).
 * - New Thread: Sends responses (serialization and network dispatch).
 */
public class UDPServer implements Runnable {
    private final int port;
    private final CommandProcessor commandProcessor;
    private DatagramSocket socket;
    private boolean running = true;

    // A cached thread pool for processing requests, as per requirements.
    private final ExecutorService processingPool = Executors.newCachedThreadPool();

    public UDPServer(int port, CommandProcessor commandProcessor) {
        this.port = port;
        this.commandProcessor = commandProcessor;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
            System.out.println("UDP Server is listening on port " + port);

            // This is the main "reading" thread.
            while (running) {
                try {
                    // 1. READ (Receive raw bytes)
                    // The main thread blocks here until a packet arrives.
                    byte[] buffer = new byte[8192]; // Use a constant, e.g., NetworkConfig.MAX_PACKET_SIZE
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    // Offload the processing to a worker thread from the pool
                    // The main thread is now free to loop back and receive the next packet.
                    handleRequestProcessing(packet);

                } catch (SocketException e) {
                    if (!running) {
                        System.out.println("Server socket closed.");
                    } else {
                        System.err.println("Socket error in server loop: " + e.getMessage());
                    }
                } catch (IOException e) {
                    if (running) {
                        System.err.println("I/O error in server loop: " + e.getMessage());
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("FATAL: Could not start server on port " + port + ": " + e.getMessage());
        } finally {
            stop(); // Ensure resources are cleaned up
        }
    }

    /**
     * Submits the received packet to the processing thread pool.
     * @param packet The packet received from a client.
     */
    private void handleRequestProcessing(DatagramPacket packet) {
        // Use a CachedThreadPool for processing.
        processingPool.submit(() -> {
            try {
                // 2. PROCESS (Deserialize & Execute Business Logic)
                // This code runs in a thread from the processingPool.
                Request request = (Request) SerializationUtils.deserialize(packet.getData());
                System.out.println("INFO: Received request " + request.getCommandType() + " from " + packet.getSocketAddress());

                Response response = commandProcessor.process(request);

                // Handoff to a brand new thread for sending.
                sendResponse(response, packet.getSocketAddress());

            } catch (Exception e) {
                System.err.println("Error processing request from " + packet.getSocketAddress() + ": " + e.getMessage());
                // If processing fails, we still need to inform the client.
                Response errorResponse = new Response("Error: An internal server error occurred while processing your request.", false);
                sendResponse(errorResponse, packet.getSocketAddress());
            }
        });
    }

    /**
     * Creates a new thread to serialize and send the response.
     * @param response The Response object to send.
     * @param clientAddress The destination address.
     */
    private void sendResponse(Response response, SocketAddress clientAddress) {
        // Requirement: Use a new Thread for sending.
        Thread senderThread = new Thread(() -> {
            try {
                // 3. SEND (Serialize & Dispatch over Network)
                // This code runs in a new, dedicated thread.
                byte[] responseBytes = SerializationUtils.serialize(response);

                // Handle oversized packets
                if (responseBytes.length > 8192) {
                    System.err.println("ERROR: Response is too large to send (" + responseBytes.length + " bytes).");
                    Response errorResponse = new Response("Error: The server's response is too large to be sent in a single packet.", false);
                    responseBytes = SerializationUtils.serialize(errorResponse);
                }

                DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress);
                socket.send(responsePacket);
                // Optional: Log successful sending. Can be noisy.
                // System.out.println("INFO: Sent response to " + clientAddress);

            } catch (Exception e) {
                System.err.println("Error sending response to " + clientAddress + ": " + e.getMessage());
            }
        });
        senderThread.start();
    }

    /**
     * Stops the server and shuts down resources gracefully.
     */
    public void stop() {
        if (!running) return;
        running = false;

        // Shut down the thread pool
        processingPool.shutdown();

        // Close the socket, which will interrupt the blocking socket.receive() call
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}