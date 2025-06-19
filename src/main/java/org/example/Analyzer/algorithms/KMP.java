package org.example.Analyzer.algorithms;

public class KMP {
    public static boolean match(byte[] text, byte[] pattern) {
        int[] lps = computeLPS(pattern);
        int textIndex = 0, patternIndex = 0;
        int textLength = text.length;
        int patternLength = pattern.length;

        while (textIndex < textLength) {
            if (pattern[patternIndex] == text[textIndex]) {
                patternIndex++;
                textIndex++;
            }

            if (patternIndex == patternLength) {
                return true; // Full match found
            } else if (textIndex < textLength && pattern[patternIndex] != text[textIndex]) {
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
     */
    private static int[] computeLPS(byte[] pattern) {
        int[] lps = new int[pattern.length];
        int len = 0; // Length of previous longest prefix suffix
        int i = 1;

        while (i < pattern.length) {
            if (pattern[i] == pattern[len]) {
                lps[i++] = ++len;
            } else {
                if (len != 0) {
                    len = lps[len - 1]; // Backtrack in LPS array
                } else {
                    lps[i++] = 0; // No matching prefix
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


