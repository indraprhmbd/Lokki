package com.lokki.service;

import java.security.SecureRandom;

public final class PasswordGeneratorService {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordGeneratorService() {}

    /**
     * Generates a random password with the given parameters.
     * Guarantees at least one character from each selected set.
     */
    public static String generate(int length, boolean includeUppercase, boolean includeLowercase,
                                  boolean includeDigits, boolean includeSymbols) {
        StringBuilder characterSet = new StringBuilder();
        StringBuilder password = new StringBuilder(length);

        if (includeUppercase) {
            characterSet.append(UPPERCASE);
            password.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        }
        if (includeLowercase) {
            characterSet.append(LOWERCASE);
            password.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        }
        if (includeDigits) {
            characterSet.append(DIGITS);
            password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }
        if (includeSymbols) {
            characterSet.append(SYMBOLS);
            password.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
        }

        String chars = characterSet.toString();
        for (int i = password.length(); i < length; i++) {
            password.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }

        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }
}
