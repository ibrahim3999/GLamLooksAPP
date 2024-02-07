package com.example.glamlooksapp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
    private static final int SALT_LENGTH = 16; // Length of the salt in bytes
    private static final String HASH_ALGORITHM = "SHA-256";

    // Generate a random salt
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    // Hash the password using SHA-256 and the provided salt
    public static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        digest.reset();
        digest.update(salt);
        byte[] hashedBytes = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    // Validate the password by comparing the stored hashed password with the hash of the provided password
    public static boolean validatePassword(String providedPassword, String storedHash, byte[] salt) throws NoSuchAlgorithmException {
        String providedHash = hashPassword(providedPassword, salt);
        return providedHash.equals(storedHash);
    }
}

