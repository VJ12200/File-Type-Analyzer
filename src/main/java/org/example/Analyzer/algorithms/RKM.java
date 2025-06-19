package org.example.Analyzer.algorithms;

public class RKM {
    private static final int BASE = 256; // ASCII character set size
    private static final int PRIME = 16777619; // FNV prime for better hash distribution

    /**
     * Rabin-Karp algorithm with O(n+m) average case time complexity
     * Uses rolling hash for efficient pattern matching
     */
    public static boolean match(byte[] text, byte[] pattern) {
        int patternLength = pattern.length;
        int textLength = text.length;

        // Early return for empty pattern or pattern longer than text
        if (patternLength == 0) return true;
        if (patternLength > textLength) return false;

        // For very short patterns, brute force is faster
        if (patternLength <= 2) {
            return bruteForceForShortPattern(text, pattern);
        }

        // Calculate initial hashes and power value
        long patternHash = 0;
        long textHash = 0;
        long power = 1;

        // Precompute the hash for pattern and first window of text
        for (int i = 0; i < patternLength; i++) {
            // Use unsigned values for bytes to avoid negative values
            patternHash = (patternHash * BASE + (pattern[i] & 0xFF)) % PRIME;
            textHash = (textHash * BASE + (text[i] & 0xFF)) % PRIME;
            if (i < patternLength - 1) {
                power = (power * BASE) % PRIME;
            }
        }

        // Slide window through text
        for (int i = 0; i <= textLength - patternLength; i++) {
            // Only check actual bytes if hash matches (reduces unnecessary comparisons)
            if (patternHash == textHash) {
                // Quick check of first and last byte before full comparison
                if (text[i] == pattern[0] && text[i + patternLength - 1] == pattern[patternLength - 1]) {
                    boolean match = true;
                    // Check middle bytes
                    for (int j = 1; j < patternLength - 1; j++) {
                        if (text[i + j] != pattern[j]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) return true;
                }
            }

            // Update rolling hash for next window
            if (i < textLength - patternLength) {
                // Remove leading digit, add trailing digit
                textHash = (BASE * (textHash - (text[i] & 0xFF) * power) + (text[i + patternLength] & 0xFF)) % PRIME;
                // Ensure hash is positive
                if (textHash < 0) textHash += PRIME;
            }
        }
        return false;
    }

    // For very short patterns, use a simpler approach
    private static boolean bruteForceForShortPattern(byte[] text, byte[] pattern) {
        int patternLength = pattern.length;
        int textLength = text.length;

        for (int i = 0; i <= textLength - patternLength; i++) {
            boolean match = true;
            for (int j = 0; j < patternLength; j++) {
                if (text[i + j] != pattern[j]) {
                    match = false;
                    break;
                }
            }
            if (match) return true;
        }
        return false;
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
