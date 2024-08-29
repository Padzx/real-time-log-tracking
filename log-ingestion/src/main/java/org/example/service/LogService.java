package org.example.service;

import org.example.dto.LogRecord;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class LogService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT; // ISO 8601 format with 'Z'

    public void processLog(LogRecord log) {
        validateLog(log);
        System.out.println("Processing log: " + log.message());
    }

    public LogRecord parsePlaintextLog(String log) {
        if (log == null || log.isEmpty()) {
            throw new IllegalArgumentException("Message field is mandatory");
        }

        return new LogRecord(
                Instant.now().toString(),
                "INFO",
                log,
                "Plaintext Source",
                Thread.currentThread().getName(),
                "PlaintextLogger"
        );
    }

    private void validateLog(LogRecord log) {
        validateTimestamp(log.timestamp());
        validateLevel(log.level());
        validateMessage(log.message());
    }

    private void validateTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            throw new IllegalArgumentException("Timestamp field is mandatory");
        }

        if (!isValidTimestamp(timestamp)) {
            throw new IllegalArgumentException("Invalid timestamp format");
        }
    }

    private void validateLevel(String level) {
        if (level == null || level.isEmpty()) {
            throw new IllegalArgumentException("Level field is mandatory");
        }
    }

    private void validateMessage(String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message field is mandatory");
        }
    }

    private boolean isValidTimestamp(String timestamp) {
        try {
            Instant.parse(timestamp);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
