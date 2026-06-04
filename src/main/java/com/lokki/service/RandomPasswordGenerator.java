package com.lokki.service;

public class RandomPasswordGenerator extends PasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private final int length;
    private final boolean includeUppercase;
    private final boolean includeLowercase;
    private final boolean includeDigits;
    private final boolean includeSymbols;

    public RandomPasswordGenerator(int length, boolean includeUppercase, boolean includeLowercase,
                                   boolean includeDigits, boolean includeSymbols) {
        this.length = length;
        this.includeUppercase = includeUppercase;
        this.includeLowercase = includeLowercase;
        this.includeDigits = includeDigits;
        this.includeSymbols = includeSymbols;
    }

    @Override
    public String generate() {
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
