package org.example.Analyzer.algorithms;

public class BruteForce {
    public static boolean match(byte[] text, byte[] pattern) {
        int patternLength = pattern.length;
        int textLength = text.length;

        // Early return for empty pattern or pattern longer than text
        if (patternLength == 0) return true;
        if (patternLength > textLength) return false;

        // Cache the first byte of pattern for faster initial check
        byte firstByte = pattern[0];

        // Slide pattern over text one byte at a time
        for (int i = 0; i <= textLength - patternLength; i++) {
            // Quick check for first byte before full comparison
            if (text[i] == firstByte) {
                boolean matchFound = true;

                // Compare each byte of pattern with text
                for (int j = 1; j < patternLength; j++) {
                    if (text[i + j] != pattern[j]) {
                        matchFound = false;
                        break; // Early exit on mismatch
                    }
                }

                if (matchFound) return true;
            }
        }
        return false;
    }


    //An Object is the superclass to all other objects so it can hold any type of object
    public static Object[] matchWithTiming(byte[] text, byte[] pattern) {
        long startTime = System.nanoTime();
        boolean result = match(text, pattern);
        long endTime = System.nanoTime();

        // Return nanoseconds directly instead of converting to milliseconds
        double elapsedNanos = (endTime - startTime);
        return new Object[]{result, elapsedNanos};
    }
}
