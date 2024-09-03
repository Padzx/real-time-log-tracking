package org.example.service;

import org.example.dto.LogRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LogServiceTest {

    private LogService logService;

    @BeforeEach
    void setUp() {
        logService = new LogService();
    }

    @Test
    void testProcessLog_ValidLog() {
        LogRecord validLog = new LogRecord(
                "2024-08-28T12:34:56Z",
                "INFO",
                "This is a valid log message",
                "TestSource",
                "main",
                "TestLogger"
        );

        logService.processLog(validLog);
        assertThat(true).isTrue();
    }

    @Test
    void testProcessLog_InvalidTimestamp() {
        LogRecord invalidTimestampLog = new LogRecord(
                "invalid-timestamp",
                "INFO",
                "Log with invalid timestamp",
                "TestSource",
                "main",
                "TestLogger"
        );

        assertThatThrownBy(() -> logService.processLog(invalidTimestampLog))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid timestamp format");
    }

    @Test
    void testProcessLog_EmptyMessage() {
        LogRecord emptyMessageLog = new LogRecord(
                "2024-08-28T12:34:56Z",
                "INFO",
                "",  // Empty Message
                "TestSource",
                "main",
                "TestLogger"
        );

        assertThatThrownBy(() -> logService.processLog(emptyMessageLog))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message field is mandatory");
    }

    @Test
    void testParsePlaintextLog() {
        String logText = "This is a simple text log message";
        LogRecord result = logService.parsePlaintextLog(logText);

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo(logText);
        assertThat(result.level()).isEqualTo("INFO");
        assertThat(result.source()).isEqualTo("Plaintext Source");
        assertThat(result.thread()).isEqualTo(Thread.currentThread().getName());
        assertThat(result.logger()).isEqualTo("PlaintextLogger");
    }

    @Test
    void testParsePlaintextLog_EmptyText() {
        String emptyLogText = "";

        assertThatThrownBy(() -> logService.parsePlaintextLog(emptyLogText))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message field is mandatory");
    }
}
