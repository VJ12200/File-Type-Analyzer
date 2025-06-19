package org.example.Analyzer.algorithms;

import org.example.Analyzer.util.ByteUtils;

public class KMP {
    public static boolean match(byte[] text, byte[] pattern) {
        // Early return for empty pattern or pattern longer than text
        if (pattern.length == 0) return true;
        if (pattern.length > text.length) return false;

        int[] lps = computeLPS(pattern);
        int textIndex = 0, patternIndex = 0;
        int textLength = text.length;
        int patternLength = pattern.length;

        while (textIndex < textLength) {
            // Case-insensitive comparison
            if (ByteUtils.equalsIgnoreCase(pattern[patternIndex], text[textIndex])) {
                patternIndex++;
                textIndex++;
            }

            if (patternIndex == patternLength) {
                return true; // Full match found
            } else if (textIndex < textLength && !ByteUtils.equalsIgnoreCase(pattern[patternIndex], text[textIndex])) {
                if (patternIndex != 0) {
                    patternIndex = lps[patternIndex - 1]; // Backtrack using LPS
                } else {
                    textIndex++; // No match, move to next character
                }
            }
        }
        return false;
    }

    /**
     * Compute Longest Prefix Suffix (LPS) array for the pattern
     * This is optimized to avoid unnecessary comparisons
     */
    private static int[] computeLPS(byte[] pattern) {
        int length = pattern.length;
        int[] lps = new int[length];

        // lps[0] is always 0
        int len = 0;
        int i = 1;

        while (i < length) {
            if (pattern[i] == pattern[len]) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    // Use previously computed LPS values to avoid redundant comparisons
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static Object[] matchWithTiming(byte[] text, byte[] pattern) {
        long startTime = System.nanoTime();
        boolean result = match(text, pattern);
        long endTime = System.nanoTime();

        double elapsedNanos = (endTime - startTime);
        return new Object[]{result, elapsedNanos};
    }
}


