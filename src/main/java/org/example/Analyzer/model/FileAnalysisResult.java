package org.example.Analyzer.model;

import java.util.Map;

public record FileAnalysisResult(
    String fileName,
    String fileType,
    Map<String, Double> algorithmTimings
) {}