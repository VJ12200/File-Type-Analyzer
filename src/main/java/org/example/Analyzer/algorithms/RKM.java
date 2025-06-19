package org.example.Analyzer.algorithms;

import org.example.Analyzer.util.ByteUtils;

public class RKM {
    private static final int BASE = 256; // ASCII character set size
    private static final int PRIME = 16777619; // FNV prime for better hash distribution

    /**
     * Rabin-Karp algorithm with case-insensitive matching
     * Note: For case-insensitive matching, we can't rely solely on hash values
     * and must perform explicit comparisons
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
        // Note: Hash values are only used as a quick filter
        // We still need to do case-insensitive comparison
        long patternHash = 0;
        long textHash = 0;
        long power = 1;

        // Precompute the hash for pattern and first window of text
        for (int i = 0; i < patternLength; i++) {
            // Use lowercase values for bytes to make hash case-insensitive
            byte patternByte = pattern[i];
            byte textByte = text[i];

            // Convert uppercase to lowercase for ASCII letters
            if (patternByte >= 'A' && patternByte <= 'Z') patternByte += 32;
            if (textByte >= 'A' && textByte <= 'Z') textByte += 32;

            patternHash = (patternHash * BASE + (patternByte & 0xFF)) % PRIME;
            textHash = (textHash * BASE + (textByte & 0xFF)) % PRIME;
            if (i < patternLength - 1) {
                power = (power * BASE) % PRIME;
            }
        }

        // Slide window through text
        for (int i = 0; i <= textLength - patternLength; i++) {
            // Only check actual bytes if hash matches (reduces unnecessary comparisons)
            if (patternHash == textHash) {
                // Perform case-insensitive comparison
                boolean match = true;
                for (int j = 0; j < patternLength; j++) {
                    if (!ByteUtils.equalsIgnoreCase(text[i + j], pattern[j])) {
                        match = false;
                        break;
                    }
                }
                if (match) return true;
            }

            // Update rolling hash for next window
            if (i < textLength - patternLength) {
                // Get current and next bytes
                byte currentByte = text[i];
                byte nextByte = text[i + patternLength];

                // Convert to lowercase for hash calculation
                if (currentByte >= 'A' && currentByte <= 'Z') currentByte += 32;
                if (nextByte >= 'A' && nextByte <= 'Z') nextByte += 32;

                // Remove leading digit, add trailing digit
                textHash = (BASE * (textHash - (currentByte & 0xFF) * power) + (nextByte & 0xFF)) % PRIME;
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
                if (!ByteUtils.equalsIgnoreCase(text[i + j], pattern[j])) {
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
