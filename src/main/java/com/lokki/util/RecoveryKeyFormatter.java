package com.lokki.util;

public final class RecoveryKeyFormatter {

    private static final int GROUP_SIZE = 4;
    private static final int GROUP_COUNT = 6;
    private static final char SEPARATOR = '-';

    private RecoveryKeyFormatter() {}

    public static String format(char[] keyChars) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < keyChars.length; i++) {
            if (i > 0 && i % GROUP_SIZE == 0) {
                formatted.append(SEPARATOR);
            }
            formatted.append(keyChars[i]);
        }
        return formatted.toString();
    }

    public static char[] strip(String formatted) {
        return formatted.replace(String.valueOf(SEPARATOR), "").toCharArray();
    }

    public static int getExpectedLength() {
        return GROUP_SIZE * GROUP_COUNT;
    }

    public static int getFormattedLength() {
        return (GROUP_SIZE * GROUP_COUNT) + (GROUP_COUNT - 1);
    }
}
