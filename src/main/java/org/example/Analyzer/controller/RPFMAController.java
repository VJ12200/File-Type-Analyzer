package org.example.Analyzer.controller;

import org.example.Analyzer.model.FileAnalysisResult;
import org.example.Analyzer.service.AnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/analyzer")
public class RPFMAController {

    private final AnalyzerService analyzerService;
    private static final Logger logger = LoggerFactory.getLogger(RPFMAController.class);

    @Autowired
    public RPFMAController(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<List<FileAnalysisResult>> analyzeFiles(
            @RequestParam("files") MultipartFile[] files) {
        try {
            List<FileAnalysisResult> results = analyzerService.analyzeFiles(Arrays.asList(files));
            return ResponseEntity.ok(results);
        } catch (IOException e) {
            logger.error("Failed to analyze files", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of(new FileAnalysisResult(
                            "Error",
                            "Failed to analyze files",
                            Map.of("error", -1.0)
                    )));
        }
    }
}