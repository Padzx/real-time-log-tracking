package org.example.unit.dto;

import org.example.annotations.UnitTest;
import org.example.dto.LogRecord;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@Tag("unit")
class LogRecordTest {

    private final Validator validator;

    public LogRecordTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenAllFieldsAreValid_thenNoViolations() {
        LogRecord logRecord = LogRecord.builder()
                .timestamp("2024-09-09T14:00:00.000Z")
                .level(LogRecord.LogLevel.INFO)
                .message("This is a log message.")
                .source("source")
                .thread("main")
                .logger("logger")
                .processedTimestamp("2024-09-09T14:00:01.000Z")
                .category("category")
                .tags(Map.of("key1", "value1"))
                .status("processed")
                .build();

        Set<ConstraintViolation<LogRecord>> violations = validator.validate(logRecord);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenTimestampIsInvalid_thenViolationOccurs() {
        LogRecord logRecord = LogRecord.builder()
                .timestamp("invalid-timestamp")
                .level(LogRecord.LogLevel.INFO)
                .message("This is a log message.")
                .source("source")
                .thread("main")
                .logger("logger")
                .processedTimestamp("2024-09-09T14:00:01.000Z")
                .category("category")
                .tags(Map.of("key1", "value1"))
                .status("processed")
                .build();

        Set<ConstraintViolation<LogRecord>> violations = validator.validate(logRecord);
        assertThat(violations).hasSize(1);

        ConstraintViolation<LogRecord> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("timestamp");
        assertThat(violation.getMessage()).isEqualTo("Timestamp must be in ISO 8601 format");
    }

    @Test
    void whenLevelIsNull_thenViolationOccurs() {
        LogRecord logRecord = LogRecord.builder()
                .timestamp("2024-09-09T14:00:00.000Z")
                .level(null)
                .message("This is a log message.")
                .source("source")
                .thread("main")
                .logger("logger")
                .processedTimestamp("2024-09-09T14:00:01.000Z")
                .category("category")
                .tags(Map.of("key1", "value1"))
                .status("processed")
                .build();

        Set<ConstraintViolation<LogRecord>> violations = validator.validate(logRecord);
        assertThat(violations).hasSize(1);

        ConstraintViolation<LogRecord> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("level");
        assertThat(violation.getMessage()).isEqualTo("Level must not be null");
    }

    @Test
    void whenMessageIsEmpty_thenViolationOccurs() {
        LogRecord logRecord = LogRecord.builder()
                .timestamp("2024-09-09T14:00:00.000Z")
                .level(LogRecord.LogLevel.INFO)
                .message("")
                .source("source")
                .thread("main")
                .logger("logger")
                .processedTimestamp("2024-09-09T14:00:01.000Z")
                .category("category")
                .tags(Map.of("key1", "value1"))
                .status("processed")
                .build();

        Set<ConstraintViolation<LogRecord>> violations = validator.validate(logRecord);
        assertThat(violations).hasSize(1);

        ConstraintViolation<LogRecord> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("message");
        assertThat(violation.getMessage()).isEqualTo("Message must not be blank");
    }

    @Test
    void whenProcessedTimestampIsNull_thenNoViolation() {
        LogRecord logRecord = LogRecord.builder()
                .timestamp("2024-09-09T14:00:00.000Z")
                .level(LogRecord.LogLevel.INFO)
                .message("This is a log message.")
                .source("source")
                .thread("main")
                .logger("logger")
                .processedTimestamp(null)
                .category("category")
                .tags(Map.of("key1", "value1"))
                .status("processed")
                .build();

        Set<ConstraintViolation<LogRecord>> violations = validator.validate(logRecord);
        assertThat(violations).isEmpty();
    }
}
