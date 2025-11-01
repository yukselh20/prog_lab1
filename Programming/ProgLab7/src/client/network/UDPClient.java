package client.network;

import common.dto.Request;
import common.dto.Response;
import common.exceptions.SerializationException;
import common.network.NetworkConfig; // Yeni import
import common.utility.SerializationUtils;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;

public class UDPClient {

    // BUFFER_SIZE sabiti kaldırıldı.
    private static final int TIMEOUT_MS = 5000;
    private static final int MAX_RETRIES = 3;

    private DatagramChannel channel;
    private SocketAddress serverAddress;

    public UDPClient(String host, int port) {
        try {
            // Sunucu adresini oluştur
            this.serverAddress = new InetSocketAddress(InetAddress.getByName(host), port);
            // Kanalı Aç
            this.channel = DatagramChannel.open();
            this.channel.bind(null);
            // Engellemeyen (Non-blocking) moda ayarla - ÇOK ÖNEMLİ!
            this.channel.configureBlocking(false);
            System.out.println("UDP Client channel opened for " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("Failed to open DatagramChannel: " + e.getMessage());
            System.exit(1);
        }
    }

    public Response sendAndReceive(Request request) {
        byte[] requestBytes;
        try {
            requestBytes = SerializationUtils.serialize(request);
        } catch (SerializationException e) {
            System.err.println("ERROR: Failed to prepare request (serialization): " + e.getMessage());
            return null;
        }

        // İstek paketinin boyutu kontrol edilebilir, ancak genelde istekler küçüktür.
        if (requestBytes.length > NetworkConfig.MAX_PACKET_SIZE) {
            System.err.println("ERROR: Request is too large to send (" + requestBytes.length + " bytes).");
            return new Response("Error: The command you are trying to send is too large.", false);
        }

        ByteBuffer sendBuffer = ByteBuffer.wrap(requestBytes);
        // Buffer boyutu artık merkezi NetworkConfig'den alınıyor.
        ByteBuffer receiveBuffer = ByteBuffer.allocate(NetworkConfig.MAX_PACKET_SIZE);

        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                System.out.println("DEBUG: Attempt " + (attempt + 1) + " sending request to " + serverAddress);
                channel.send(sendBuffer, serverAddress);

                // Yanıt bekleme
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < TIMEOUT_MS) {
                    receiveBuffer.clear();
                    SocketAddress fromAddress = channel.receive(receiveBuffer);

                    if (fromAddress != null) {
                        if (fromAddress.equals(serverAddress)) {
                            System.out.println("DEBUG: Received response from " + serverAddress + " (" + receiveBuffer.position() + " bytes)");
                            receiveBuffer.flip();
                            byte[] responseBytes = new byte[receiveBuffer.remaining()];
                            receiveBuffer.get(responseBytes);

                            try {
                                Object responseObject = SerializationUtils.deserialize(responseBytes);
                                if (responseObject instanceof Response) {
                                    return (Response) responseObject;
                                } else {
                                    System.err.println("ERROR: Received unexpected data type from server.");
                                    return new Response("Received unexpected data type from server.", false);
                                }
                            } catch (SerializationException e) {
                                System.err.println("ERROR: Failed to process server response: " + e.getMessage());
                                return new Response("Failed to process server response: " + e.getMessage(), false);
                            }
                        } else {
                            System.err.println("WARN: Received packet from unexpected address: " + fromAddress);
                        }
                    }
                    Thread.sleep(100);
                }

                System.err.println("WARN: Timeout waiting for response (Attempt " + (attempt + 1) + "/" + MAX_RETRIES + ")");

            } catch (ClosedChannelException e) {
                System.err.println("ERROR: Network channel closed unexpectedly. Cannot send/receive.");
                return null;
            } catch (IOException e) {
                System.err.println("ERROR: Network I/O error on attempt " + (attempt + 1) + ": " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("WARN: Client thread interrupted.");
                Thread.currentThread().interrupt();
                return null;
            }
            // Bir sonraki denemeden önce buffer'ı başa sar
            sendBuffer.rewind();
        }

        System.err.println("ERROR: Failed to get response from server after " + MAX_RETRIES + " attempts.");
        return null;
    }

    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                System.out.println("Client UDP channel closed.");
            }
        } catch (IOException e) {
            System.err.println("ERROR: Failed to close client network channel: " + e.getMessage());
        }
    }
}