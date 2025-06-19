package org.example.Analyzer.controller;

import org.example.Analyzer.model.FileAnalysisResult;
import org.example.Analyzer.service.AnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/analyzer")
public class RPFMAController {

    private final AnalyzerService analyzerService;

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
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}