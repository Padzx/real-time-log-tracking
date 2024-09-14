package org.example.unit.services;

import org.example.dto.LogRecord;
import org.example.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LogServiceTest {

    private LogService logService;

    @BeforeEach
    void setUp() {
        logService = new LogService();
    }

    @Test
    void testProcessLogSuccessfully() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("environment", "production");

        LogRecord log = new LogRecord(
                "2024-09-12T12:00:00.000Z",
                "INFO",
                "Application started successfully.",
                "MainApplication",
                "main",
                "org.example.MainApplication",
                null, // No processed timestamp initially
                "SYSTEM",
                tags,
                "NEW"
        );

        // Act
        logService.processLog(log);

        // Assert
        assertNotNull(log);
        assertEquals("2024-09-12T12:00:00.000Z", log.getTimestamp());
        assertEquals("INFO", log.getLevel());
        assertEquals("Application started successfully.", log.getMessage());
        assertEquals("MainApplication", log.getSource());
        assertEquals("main", log.getThread());
        assertEquals("org.example.MainApplication", log.getLogger());
        assertEquals("NEW", log.getStatus());
        assertNull(log.getProcessedTimestamp()); // Assuming processLog does not set processedTimestamp
    }

    @Test
    void testProcessLogWithInvalidTimestamp() {
        // Arrange
        LogRecord log = new LogRecord(
                "", // Invalid timestamp
                "ERROR",
                "Error occurred while processing.",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Act
        logService.processLog(log);

        // Assert
        assertNotNull(log);
        assertEquals("", log.getTimestamp());
    }

    @Test
    void testProcessLogWithNullLevel() {
        // Arrange
        LogRecord log = new LogRecord(
                "2024-09-12T12:00:00.000Z",
                null, // Null level
                "Warning issued for configuration.",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Act
        logService.processLog(log);

        // Assert
        assertNotNull(log);
        assertNull(log.getLevel());
    }

    @Test
    void testProcessLogWithBlankMessage() {
        // Arrange
        LogRecord log = new LogRecord(
                "2024-09-12T12:00:00.000Z",
                "WARN",
                "", // Blank message
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Act
        logService.processLog(log);

        // Assert
        assertNotNull(log);
        assertEquals("", log.getMessage());
    }
}
