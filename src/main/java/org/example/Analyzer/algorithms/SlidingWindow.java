package org.example.Analyzer.algorithms;

import org.example.Analyzer.util.ByteUtils;

public class SlidingWindow {
    /**
     * Optimized sliding window approach with case-insensitive matching
     */
    public static boolean match(byte[] text, byte[] pattern) {
        int patternLength = pattern.length;
        int textLength = text.length;

        // Early return for empty pattern or pattern longer than text
        if (patternLength == 0) return true;
        if (patternLength > textLength) return false;

        // Check first and last bytes before full comparison (quick rejection)
        byte firstByte = pattern[0];
        byte lastByte = pattern[patternLength - 1];

        // Slide window through text
        for (int i = 0; i <= textLength - patternLength; i++) {
            // Quick check of first and last bytes (case-insensitive)
            if (ByteUtils.equalsIgnoreCase(text[i], firstByte) &&
                ByteUtils.equalsIgnoreCase(text[i + patternLength - 1], lastByte)) {

                // Only create a window and do full comparison if first and last bytes match
                boolean match = true;

                // Check all bytes in between (case-insensitive)
                for (int j = 1; j < patternLength - 1; j++) {
                    if (!ByteUtils.equalsIgnoreCase(text[i + j], pattern[j])) {
                        match = false;
                        break;
                    }
                }

                if (match) return true;
            }
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

