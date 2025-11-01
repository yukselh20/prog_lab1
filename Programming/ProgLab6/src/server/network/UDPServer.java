package server.network;

import common.dto.Request;
import common.dto.Response;
import common.exceptions.SerializationException;
import common.network.NetworkConfig; // Yeni import
import common.utility.SerializationUtils;
import server.processing.CommandProcessor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class UDPServer implements Runnable {

    private final int port;
    private final CommandProcessor commandProcessor;
    private DatagramChannel channel;
    private Selector selector;
    private boolean running = true;

    public UDPServer(int port, CommandProcessor commandProcessor) {
        this.port = port;
        this.commandProcessor = commandProcessor;
    }

    @Override
    public void run() {
        try {
            initializeServer();
            System.out.println("UDP Server started successfully on port " + port);

            // Buffer boyutu NetworkConfig geliyor.
            ByteBuffer buffer = ByteBuffer.allocate(NetworkConfig.MAX_PACKET_SIZE);

            while (running) {
                try {
                    if (selector.select(1000) == 0) continue;

                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (!key.isValid()) continue;

                        if (key.isReadable()) {
                            handleIncomingData(key, buffer);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("ERROR: Network I/O error in selector loop: " + e.getMessage());
                } catch (CancelledKeyException e) {
                    System.err.println("WARN: SelectionKey was cancelled.");
                }
            }
        } catch (IOException e) {
            System.err.println("FATAL: Server initialization failed: " + e.getMessage());
        } finally {
            closeServer();
        }
    }

    private void initializeServer() throws IOException {
        selector = Selector.open();
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(port));
        channel.register(selector, SelectionKey.OP_READ);
        System.out.println("Selector and DatagramChannel initialized.");
    }

    private void handleIncomingData(SelectionKey key, ByteBuffer buffer) {
        DatagramChannel currentChannel = (DatagramChannel) key.channel();
        SocketAddress clientAddress = null;

        try {
            buffer.clear(); // Buffer'ı her okumadan önce temizle
            clientAddress = currentChannel.receive(buffer);

            if (clientAddress == null) return;
            System.out.println("DEBUG: Received " + buffer.position() + " bytes from " + clientAddress);

            buffer.flip();
            if (buffer.remaining() == 0) return;
            byte[] receivedData = Arrays.copyOf(buffer.array(), buffer.limit());

            // 1. İsteği Deserileştir
            Request request;
            try {
                Object receivedObject = SerializationUtils.deserialize(receivedData);
                if (receivedObject instanceof Request) {
                    request = (Request) receivedObject;
                    System.out.println("INFO: Received request '" + request.getCommandType() + "' from " + clientAddress);
                } else {
                    // Yanlış tip gelirse
                    System.err.println("ERROR: Received object is not a Request: " + (receivedObject != null ? receivedObject.getClass().getName() : "null"));
                    sendResponse(new Response("Error: Invalid request type received.", false), clientAddress);
                    return;
                }
            } catch (SerializationException e) {
                System.err.println("ERROR: Failed to deserialize request from " + clientAddress + ": " + e.getMessage());
                String errorMsg = "Error: Could not deserialize request. The packet may have been too large and was truncated.";
                sendResponse(new Response(errorMsg, false), clientAddress);
                return;
            }

            // 2. Komutu İşle
            Response response = commandProcessor.process(request);
            System.out.println("INFO: Processed command '" + request.getCommandType() + "' for " + clientAddress + ". Success: " + response.isSuccess());

            // 3. Yanıtı Gönder
            sendResponse(response, clientAddress);

        } catch (IOException e) {
            System.err.println("ERROR: I/O error handling data from " + (clientAddress != null ? clientAddress : "unknown") + ": " + e.getMessage());
            if (clientAddress != null) {
                sendResponse(new Response("Internal server error during request processing.", false), clientAddress);
            }
        }
    }

    private void sendResponse(Response response, SocketAddress clientAddress) {
        try {
            // 1. Yanıtı Serileştir (SerializationUtils kullanılıyor)
            byte[] responseBytes = SerializationUtils.serialize(response);

            // Göndermeden önce boyutu kontrol et.
            if (responseBytes.length > NetworkConfig.MAX_PACKET_SIZE) {
                System.err.println("ERROR: Response is too large to send (" + responseBytes.length + " bytes). Max size is " + NetworkConfig.MAX_PACKET_SIZE + ".");
                // Büyük yanıtı göndermek yerine, istemciye bir hata mesajı gönderiyoruz.
                Response errorResponse = new Response("Error: The data you requested is too large to be sent in a single packet.", false);
                byte[] errorBytes = SerializationUtils.serialize(errorResponse);
                ByteBuffer errorBuffer = ByteBuffer.wrap(errorBytes);
                channel.send(errorBuffer, clientAddress);
                return; // Orijinal büyük yanıtı gönderme.
            }

            ByteBuffer sendBuffer = ByteBuffer.wrap(responseBytes);
            System.out.println("DEBUG: Sending response (" + responseBytes.length + " bytes) to " + clientAddress);
            channel.send(sendBuffer, clientAddress);

        } catch (SerializationException e) {
            System.err.println("ERROR: Failed to serialize response for " + clientAddress + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println("ERROR: I/O error sending response to " + clientAddress + ": " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        if (selector != null) {
            selector.wakeup();
        }
    }

    private void closeServer() {
        try {
            if (channel != null && channel.isOpen()) channel.close();
            if (selector != null && selector.isOpen()) selector.close();
        } catch (IOException e) {
            System.err.println("ERROR: Failed to close network resources: " + e.getMessage());
        }
    }
}