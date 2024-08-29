package org.example.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.service.LogService;
import org.example.dto.LogRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("api/logs")
public class LogController {

    private final LogService logService;

    // Dependency Injection via constructor
    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    // Endpoint to receive logs in JSON format
    @PostMapping(value = "/json", consumes = "application/json")
    public ResponseEntity<String> receiveJsonLog(@RequestBody LogRecord logRecord) {
        try {
            logService.processLog(logRecord);  // Delegate the processing to the service layer
            return ResponseEntity.ok("Log received in JSON format.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to receive logs in simple text format
    @PostMapping(value = "/text", consumes = "text/plain")
    public ResponseEntity<String> receiveTextLog(@RequestBody String logText) {
        try {
            LogRecord logRecord = logService.parsePlaintextLog(logText);
            logService.processLog(logRecord);  // Delegate the processing to the service layer
            return ResponseEntity.ok("Log received in Text format.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Future extension points:
    // - You can add more methods here to handle different formats.
    // - If customization is needed, provide instructions or comments for other developers.
}
