package server.db;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A utility class for hashing passwords using SHA-384.
 * This class provides a static method to hash a given password string
 */
public class PasswordHasher {

    /**
     * Hashes the given password using the SHA-384 algorithm.
     * @param password The raw password string.
     * @return A hexadecimal string representation of the hashed password.
     */
    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-384 algorithm not found", e);
        }
    }
}