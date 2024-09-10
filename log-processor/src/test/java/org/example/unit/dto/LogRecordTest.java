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
        LogRecord logRecord = new LogRecord(
                "2024-09-09T14:00:00Z",
                "INFO",
                "This is a log message.",
                "source",
                "main",
                "logger",
                "2024-09-09T14:00:01Z",
                "category",
                Map.of("key1", "value1"),
                "processed"
        );

        Set<ConstraintViolation<LogRecord>> violations = validator.validate(logRecord);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenTimestampIsInvalid_thenViolationOccurs() {
        LogRecord logRecord = new LogRecord(
                "invalid-timestamp",
                "INFO",
                "This is a log message.",
                "source",
                "main",
                "logger",
                "2024-09-09T14:00:01Z",
                "category",
                Map.of("key1", "value1"),
                "processed"
        );

        Set<ConstraintViolation<LogRecord>> violations = validator.validate(logRecord);
        assertThat(violations).hasSize(1);

        ConstraintViolation<LogRecord> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("timestamp");
        assertThat(violation.getMessage()).isEqualTo("Timestamp must be in ISO 8601 format");
    }

    @Test
    void whenLevelIsNull_thenViolationOccurs() {
        LogRecord logRecord = new LogRecord(
                "2024-09-09T14:00:00Z",
                null,
                "This is a log message.",
                "source",
                "main",
                "logger",
                "2024-09-09T14:00:01Z",
                "category",
                Map.of("key1", "value1"),
                "processed"
        );

        Set<ConstraintViolation<LogRecord>> violations = validator.validate(logRecord);
        assertThat(violations).hasSize(1);

        ConstraintViolation<LogRecord> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("level");
        assertThat(violation.getMessage()).isEqualTo("Level must not be null");
    }

    @Test
    void whenMessageIsEmpty_thenViolationOccurs() {
        LogRecord logRecord = new LogRecord(
                "2024-09-09T14:00:00Z",
                "INFO",
                "",
                "source",
                "main",
                "logger",
                "2024-09-09T14:00:01Z",
                "category",
                Map.of("key1", "value1"),
                "processed"
        );

        Set<ConstraintViolation<LogRecord>> violations = validator.validate(logRecord);
        assertThat(violations).hasSize(1);

        ConstraintViolation<LogRecord> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("message");
        assertThat(violation.getMessage()).isEqualTo("Message must not be blank");
    }

    @Test
    void whenProcessedTimestampIsNull_thenNoViolation() {
        LogRecord logRecord = new LogRecord(
                "2024-09-09T14:00:00Z",
                "INFO",
                "This is a log message.",
                "source",
                "main",
                "logger",
                null, // Processed timestamp is optional
                "category",
                Map.of("key1", "value1"),
                "processed"
        );

        Set<ConstraintViolation<LogRecord>> violations = validator.validate(logRecord);
        assertThat(violations).isEmpty();
    }
}
