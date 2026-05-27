package com.lokki.service;

import com.lokki.util.SecureMemoryUtil;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public final class KeyDerivationService {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 310000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private KeyDerivationService() {}

    /**
     * Derives a 256-bit key from the given password and salt using PBKDF2-HMAC-SHA256.
     */
    public static byte[] deriveKey(char[] password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] key = factory.generateSecret(spec).getEncoded();
            return key;
        } catch (Exception e) {
            throw new RuntimeException("Key derivation failed", e);
        }
    }

    /**
     * Generates a 16-byte random salt and returns it as a byte array.
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Computes a PBKDF2 hash of the password for verification purposes.
     * Returns the result as a base64-encoded string.
     */
    public static String hashPassword(char[] password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            String encoded = Base64.getEncoder().encodeToString(hash);
            SecureMemoryUtil.clearByteArray(hash);
            return encoded;
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}
