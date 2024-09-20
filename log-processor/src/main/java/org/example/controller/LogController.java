package org.example.controller;

import org.example.dto.LogRecord;
import org.example.service.LogProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// This class was used to directly test the operation of the Log Processor microservice.
// A simple and practical way to test the independence of the microservice in relation to other services.

@RestController
@RequestMapping("/logs")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    private final LogProcessingService logProcessingService;

    public LogController(LogProcessingService logProcessingService) {
        this.logProcessingService = logProcessingService;
    }

    @PostMapping
    public ResponseEntity<String> submitLog(@RequestBody LogRecord logRecord) {
        try {
            logger.info("Received log: {}", logRecord.getMessage());
            logProcessingService.processLog(logRecord);
            return ResponseEntity.ok("Log processed successfully");
        } catch (Exception e) {
            logger.error("Error processing log", e);
            return ResponseEntity.status(500).body("Failed to process log");
        }
    }
}