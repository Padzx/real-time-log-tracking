package org.example.unit.services;

import org.example.annotations.UnitTest;
import org.example.dto.LogRecord;
import org.example.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@Tag("unit")
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

        LogRecord log = LogRecord.builder()
                .timestamp("2024-09-12T12:00:00.000Z")
                .level(LogRecord.LogLevel.INFO)
                .message("Application started successfully.")
                .source("MainApplication")
                .thread("main")
                .logger("org.example.MainApplication")
                .processedTimestamp(null) // No processed timestamp initially
                .category("SYSTEM")
                .tags(tags)
                .status("NEW")
                .build();

        // Act
        logService.processLog(log);

        // Assert
        assertNotNull(log);
        assertEquals("2024-09-12T12:00:00.000Z", log.getTimestamp());
        assertEquals(LogRecord.LogLevel.INFO, log.getLevel());
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
        LogRecord log = LogRecord.builder()
                .timestamp("") // Invalid timestamp
                .level(LogRecord.LogLevel.ERROR)
                .message("Error occurred while processing.")
                .build();

        // Act
        logService.processLog(log);

        // Assert
        assertNotNull(log);
        assertEquals("", log.getTimestamp());
        assertEquals(LogRecord.LogLevel.ERROR, log.getLevel());
    }

    @Test
    void testProcessLogWithNullLevel() {
        // Arrange
        LogRecord log = LogRecord.builder()
                .timestamp("2024-09-12T12:00:00.000Z")
                .level(null) // Null level
                .message("Warning issued for configuration.")
                .build();

        // Act
        logService.processLog(log);

        // Assert
        assertNotNull(log);
        assertNull(log.getLevel());
    }

    @Test
    void testProcessLogWithBlankMessage() {
        // Arrange
        LogRecord log = LogRecord.builder()
                .timestamp("2024-09-12T12:00:00.000Z")
                .level(LogRecord.LogLevel.WARN)
                .message("") // Blank message
                .build();

        // Act
        logService.processLog(log);

        // Assert
        assertNotNull(log);
        assertEquals("", log.getMessage());
    }
}
