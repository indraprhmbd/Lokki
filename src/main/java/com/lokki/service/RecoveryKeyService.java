package com.lokki.service;

import com.lokki.util.RecoveryKeyFormatter;
import java.security.SecureRandom;

public final class RecoveryKeyService {

    private static final String CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int KEY_LENGTH = 24;
    private static final SecureRandom RANDOM = new SecureRandom();

    private RecoveryKeyService() {}

    /**
     * Generates a recovery key: 24 random characters from a safe charset,
     * formatted as XXXX-XXXX-XXXX-XXXX-XXXX-XXXX.
     */
    public static char[] generate() {
        char[] key = new char[KEY_LENGTH];
        for (int i = 0; i < KEY_LENGTH; i++) {
            key[i] = CHARSET.charAt(RANDOM.nextInt(CHARSET.length()));
        }
        return key;
    }

    /**
     * Returns a formatted display string of the recovery key.
     */
    public static String formatForDisplay(char[] key) {
        return RecoveryKeyFormatter.format(key);
    }
}
