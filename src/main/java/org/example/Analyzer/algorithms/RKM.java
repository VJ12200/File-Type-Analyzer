package org.example.Analyzer.algorithms;

public class RKM {
    private static final int BASE = 256; // ASCII character set size
    private static final int PRIME = 101; // Large prime for hash calculation

    /**
     * Rabin-Karp algorithm with O(n+m) average case time complexity
     * Uses rolling hash for efficient pattern matching
     */
    public static boolean match(byte[] text, byte[] pattern) {
        int patternLength = pattern.length;
        int textLength = text.length;
        if (patternLength > textLength) return false;

        // Calculate initial hashes and power value
        long patternHash = 0;
        long textHash = 0;
        long power = 1;

        for (int i = 0; i < patternLength; i++) {
            patternHash = (patternHash * BASE + pattern[i]) % PRIME;
            textHash = (textHash * BASE + text[i]) % PRIME;
            if (i < patternLength - 1) {
                power = (power * BASE) % PRIME;
            }
        }

        // Slide window through text
        for (int i = 0; i <= textLength - patternLength; i++) {
            if (patternHash == textHash && checkEquals(text, i, pattern)) {
                return true;
            }

            // Update rolling hash for next window
            if (i < textLength - patternLength) {
                textHash = (BASE * (textHash - text[i] * power) + text[i + patternLength]) % PRIME;
                if (textHash < 0) textHash += PRIME; // Handle negative values
            }
        }
        return false;
    }

    /**
     * Verify actual byte comparison when hashes match
     */
    private static boolean checkEquals(byte[] text, int start, byte[] pattern) {
        for (int j = 0; j < pattern.length; j++) {
            if (text[start + j] != pattern[j]) {
                return false;
            }
        }
        return true;
    }
    public static Object[] matchWithTiming(byte[] text, byte[] pattern) {
        long startTime = System.nanoTime();
        boolean result = match(text, pattern);
        long endTime = System.nanoTime();

        // Return nanoseconds directly instead of converting to milliseconds
        double elapsedNanos = (endTime - startTime);
        return new Object[]{result, elapsedNanos};
    }
}
