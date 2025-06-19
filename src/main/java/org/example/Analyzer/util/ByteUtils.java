package org.example.Analyzer.util;

public class ByteUtils {
    /**
     * Compares two bytes in a case-insensitive manner if they represent ASCII letters.
     * For non-letter bytes, performs a normal comparison.
     * 
     * @param b1 First byte
     * @param b2 Second byte
     * @return true if bytes are equal ignoring case, false otherwise
     */
    public static boolean equalsIgnoreCase(byte b1, byte b2) {
        // If bytes are identical, return true immediately
        if (b1 == b2) return true;
        
        // Check if both bytes represent ASCII letters
        boolean b1IsUppercase = b1 >= 'A' && b1 <= 'Z';
        boolean b1IsLowercase = b1 >= 'a' && b1 <= 'z';
        boolean b2IsUppercase = b2 >= 'A' && b2 <= 'Z';
        boolean b2IsLowercase = b2 >= 'a' && b2 <= 'z';
        
        // If both are letters, compare them ignoring case
        if ((b1IsUppercase || b1IsLowercase) && (b2IsUppercase || b2IsLowercase)) {
            // Convert both to lowercase for comparison
            byte b1Lower = b1IsUppercase ? (byte)(b1 + 32) : b1;
            byte b2Lower = b2IsUppercase ? (byte)(b2 + 32) : b2;
            return b1Lower == b2Lower;
        }
        
        // For non-letter bytes, they must be exactly equal
        return false;
    }
}