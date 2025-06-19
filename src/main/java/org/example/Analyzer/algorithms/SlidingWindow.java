package org.example.Analyzer.algorithms;

import java.util.Arrays;

public class SlidingWindow {
    /**
     * Optimized sliding window approach with O(n*m) worst case
     * Uses array copying to reduce index calculations
     */
    public static boolean match(byte[] text, byte[] pattern) {
        int patternLength = pattern.length;
        byte[] window = new byte[patternLength];

        // Slide window through text
        for (int i = 0; i <= text.length - patternLength; i++) {
            // Copy current window from text
            System.arraycopy(text, i, window, 0, patternLength);

            if (Arrays.equals(window, pattern)) {
                return true;
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

