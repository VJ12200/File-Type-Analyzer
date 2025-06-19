package org.example.Analyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.example.Analyzer.algorithms.*;
import org.example.Analyzer.model.FileAnalysisResult;
import org.example.Analyzer.model.PatternResult;

import jakarta.annotation.PreDestroy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AnalyzerService {
    private final FilePatternLoadService patternLoadService;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private List<PatternResult> cachedPatterns;
    private static final Logger logger = LoggerFactory.getLogger(AnalyzerService.class);

    @Autowired
    public AnalyzerService(FilePatternLoadService patternLoadService) {
        this.patternLoadService = patternLoadService;
        // Load patterns once at startup
        try {
            this.cachedPatterns = patternLoadService.loadPatternsFromFile();
            logger.info("Loaded {} patterns at startup", cachedPatterns.size());
        } catch (Exception e) {
            logger.error("Failed to load patterns at startup: {}", e.getMessage(), e);
            // Initialize with empty list
            this.cachedPatterns = new ArrayList<>();
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Analyzes multiple files in parallel and returns their file types
     */
    public List<FileAnalysisResult> analyzeFiles(List<MultipartFile> files) throws IOException {
        List<Future<FileAnalysisResult>> futures = new ArrayList<>();
        List<FileAnalysisResult> results = new ArrayList<>();

        // Submit each file for analysis in parallel
        for (MultipartFile file : files) {
            futures.add(executor.submit(() -> analyzeFile(file)));
        }

        // Collect results
        for (Future<FileAnalysisResult> future : futures) {
            try {
                results.add(future.get());
            } catch (Exception e) {
                logger.error("Failed to analyze file: {}", e.getMessage(), e);
                results.add(new FileAnalysisResult("Error", "Failed to analyze file", new HashMap<>()));
            }
        }

        return results;
    }

    private FileAnalysisResult analyzeFile(MultipartFile file) throws IOException {
        // Use cached patterns instead of loading them every time
        List<PatternResult> patterns = cachedPatterns;
        if (patterns.isEmpty()) {
            // If patterns are empty, try loading them again
            try {
                patterns = patternLoadService.loadPatternsFromFile();
                cachedPatterns = patterns;
            } catch (Exception e) {
                System.err.println("Failed to load patterns: " + e.getMessage());
                // Return a placeholder result
                return new FileAnalysisResult(file.getOriginalFilename(), "Unknown (No patterns loaded)", new HashMap<>());
            }
        }

        byte[] fileBytes = file.getBytes();

        return patterns.stream()
                .filter(pattern -> matchesAnyAlgorithm(fileBytes, pattern))
                .findFirst()
                .map(pattern -> createResult(file.getOriginalFilename(), pattern))
                .orElse(new FileAnalysisResult(file.getOriginalFilename(), "Unknown", new HashMap<>()));
    }

    private boolean matchesAnyAlgorithm(byte[] fileBytes, PatternResult pattern) {
        // Convert pattern string to bytes, handling escape sequences
        byte[] patternBytes;
        try {
            patternBytes = convertPatternToBytes(pattern.pattern());
        } catch (Exception e) {
            System.err.println("Error converting pattern: " + pattern.pattern());
            return false;
        }

        // Clear previous timings
        algorithmTimings.get().clear();

        // Validate and run each algorithm
        Object[] bfResult = validateResult(BruteForce.matchWithTiming(fileBytes, patternBytes), "BruteForce");
        Object[] kmpResult = validateResult(KMP.matchWithTiming(fileBytes, patternBytes), "KMP");
        Object[] rkmResult = validateResult(RKM.matchWithTiming(fileBytes, patternBytes), "RabinKarp");
        Object[] swResult = validateResult(SlidingWindow.matchWithTiming(fileBytes, patternBytes), "SlidingWindow");

        // If any algorithm found a match, return true
        return (boolean)bfResult[0] || (boolean)kmpResult[0] || (boolean)rkmResult[0] || (boolean)swResult[0];
    }

    private Object[] validateResult(Object[] result, String algorithmName) {
        if (result == null || result.length < 2 || result[1] == null || !(result[1] instanceof Double)) {
            System.err.println("Invalid result from " + algorithmName + " algorithm");
            return new Object[]{false, 0.0};
        }

        // Store the timing in the ThreadLocal map
        algorithmTimings.get().put(algorithmName, (Double)result[1]);

        return result;
    }

    // Thread-local storage for algorithm timings
    private final ThreadLocal<Map<String, Double>> algorithmTimings =
            ThreadLocal.withInitial(HashMap::new);

    private Double getAlgorithmTiming(String algorithm) {
        return algorithmTimings.get().getOrDefault(algorithm, 0.0);
    }

    private FileAnalysisResult createResult(String fileName, PatternResult pattern) {
        return new FileAnalysisResult(
                fileName,
                pattern.fileType(),
                Map.of(
                        "BruteForce", getAlgorithmTiming("BruteForce"),
                        "KMP", getAlgorithmTiming("KMP"),
                        "RabinKarp", getAlgorithmTiming("RabinKarp"),
                        "SlidingWindow", getAlgorithmTiming("SlidingWindow")
                )
        );
    }

    private byte[] convertPatternToBytes(String pattern) {
        // Check if pattern is in space-separated hex format
        if (pattern.matches("([0-9A-Fa-f]{2}\\s*)+")) {
            // Handle space-separated hex values
            String[] hexValues = pattern.split("\\s+");
            byte[] result = new byte[hexValues.length];

            for (int i = 0; i < hexValues.length; i++) {
                try {
                    // Convert to uppercase to ensure case-insensitive matching
                    result[i] = (byte) Integer.parseInt(hexValues[i].toUpperCase(), 16);
                } catch (NumberFormatException e) {
                    logger.error("Invalid hex value: {}", hexValues[i]);
                    // Use 0 as fallback for invalid hex
                    result[i] = 0;
                }
            }
            return result;
        } else {
            // Handle escape sequences in the pattern (original implementation)
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int i = 0; i < pattern.length(); i++) {
                char c = pattern.charAt(i);
                if (c == '\\' && i + 1 < pattern.length()) {
                    char next = pattern.charAt(++i);
                    switch (next) {
                        case 'x':
                            // Handle hex escape sequence \xHH
                            if (i + 2 < pattern.length()) {
                                String hex = pattern.substring(i + 1, i + 3);
                                try {
                                    // Convert to uppercase for case-insensitive matching
                                    out.write(Integer.parseInt(hex.toUpperCase(), 16));
                                } catch (NumberFormatException e) {
                                    logger.error("Invalid hex sequence: \\x{}", hex);
                                }
                                i += 2;
                            }
                            break;
                        case 'n': out.write('\n'); break;
                        case 'r': out.write('\r'); break;
                        case 't': out.write('\t'); break;
                        case '0': out.write(0); break;
                        default: out.write(next);
                    }
                } else {
                    out.write(c);
                }
            }
            return out.toByteArray();
        }
    }
}
