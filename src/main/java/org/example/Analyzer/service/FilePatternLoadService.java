package org.example.Analyzer.service;

import org.example.Analyzer.model.PatternResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@Service
public class FilePatternLoadService {

    // Update the path to match where your file actually is
    private static final String PATTERNS_FILE = "org/example/Analyzer/config/Patterns.txt";

    private final ResourceLoader resourceLoader;

    public FilePatternLoadService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<PatternResult> loadPatternsFromFile() {
        List<PatternResult> patterns = new ArrayList<>();

        try {
            // Only look for the pattern file in the Patterns directory
            Resource resource = resourceLoader.getResource("classpath:org/example/Analyzer/Patterns/Patterns.txt");

            if (!resource.exists()) {
                throw new IOException("Could not find Patterns.txt in org/example/Analyzer/Patterns directory");
            }

            System.out.println("Loading patterns from: " + resource.getURL());

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip empty lines
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    // Parse the line (format: serialNo;"pattern";"description")
                    String[] parts = line.split(";");
                    if (parts.length >= 3) {
                        try {
                            Long serialNo = Long.parseLong(parts[0].trim());
                            // Remove quotes if present
                            String pattern = parts[1].replaceAll("^\"|\"$", "");
                            String fileType = parts[2].replaceAll("^\"|\"$", "");

                            patterns.add(new PatternResult(serialNo, pattern, fileType));
                        } catch (NumberFormatException e) {
                            // Skip lines with invalid serial numbers
                            System.err.println("Invalid line format: " + line);
                        }
                    }
                }
            }

            // Sort patterns by serial number to respect priority
            patterns.sort(Comparator.comparing(PatternResult::serialNo));

            System.out.println("Loaded " + patterns.size() + " patterns");
            // Print the first few patterns for debugging
            for (int i = 0; i < Math.min(5, patterns.size()); i++) {
                PatternResult p = patterns.get(i);
                System.out.println(p.serialNo() + ": " + p.pattern() + " -> " + p.fileType());
            }

        } catch (IOException e) {
            System.err.println("Error loading patterns file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load patterns from file", e);
        }

        return patterns;
    }
}
