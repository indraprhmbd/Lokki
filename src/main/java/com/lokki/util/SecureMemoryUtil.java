package com.lokki.util;

import java.util.Arrays;

public final class SecureMemoryUtil {

    private SecureMemoryUtil() {}

    public static void clearCharArray(char[] array) {
        if (array != null) {
            Arrays.fill(array, '\u0000');
        }
    }

    public static void clearByteArray(byte[] array) {
        if (array != null) {
            Arrays.fill(array, (byte) 0);
        }
    }
}
