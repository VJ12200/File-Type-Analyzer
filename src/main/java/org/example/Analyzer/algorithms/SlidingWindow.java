package org.example.Analyzer.algorithms;

public class SlidingWindow {
    /**
     * Optimized sliding window approach with early termination
     * Avoids unnecessary array copying and comparisons
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
            // Quick check of first and last bytes
            if (text[i] == firstByte && text[i + patternLength - 1] == lastByte) {
                // Only create a window and do full comparison if first and last bytes match
                boolean match = true;

                // Check all bytes in between
                for (int j = 1; j < patternLength - 1; j++) {
                    if (text[i + j] != pattern[j]) {
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

