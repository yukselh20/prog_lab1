package common.utility;

import java.io.*;

import common.exceptions.SerializationException;


/**
 * Provides utility methods for serializing and deserializing objects.
 * Used for network communication between client and server.
 */
public class SerializationUtils {

    /**
     * Serializes an object into a byte array.
     *
     * @param obj The object to serialize (must implement Serializable).
     * @return The byte array representing the serialized object.
     * @throws SerializationException If an IOException occurs during serialization.
     */
    public static byte[] serialize(Object obj) throws SerializationException {
        if (!(obj instanceof Serializable)) {
            // Bu kontrol aslında derleme zamanında yakalanmalı ama emin olmak için eklenebilir.
            throw new SerializationException("Object of class " + obj.getClass().getName() + " is not Serializable", null);
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            oos.flush(); // Ensure all data is written to the byte array stream
            return baos.toByteArray();
        } catch (IOException e) {
            // logger.error("Serialization failed for object: {}", obj, e); // Loglama
            throw new SerializationException("Failed to serialize object: " + e.getMessage(), e);
        }
    }

    /**
     * Deserializes an object from a byte array.
     *
     * @param data The byte array containing the serialized object.
     * @return The deserialized object.
     * @throws SerializationException If an IOException or ClassNotFoundException occurs during deserialization.
     */
    public static Object deserialize(byte[] data) throws SerializationException {
        if (data == null || data.length == 0) {
            throw new SerializationException("Cannot deserialize null or empty data", null);
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            // logger.error("Deserialization failed:", e); // Loglama
            throw new SerializationException("Failed to deserialize object: " + e.getMessage(), e);
        }
    }
}