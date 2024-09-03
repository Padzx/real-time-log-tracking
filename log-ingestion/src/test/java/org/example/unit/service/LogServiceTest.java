package org.example.unit.service;

import org.example.annotations.UnitTest;
import org.example.dto.LogRecord;
import org.example.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@UnitTest
@SpringBootTest
class LogServiceTest {

    @InjectMocks
    private LogService logService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize the mocks
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
                .hasMessageContaining("Invalid timestamp format.");
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
                .hasMessageContaining("The 'message' field is mandatory.");
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
                .hasMessageContaining("The 'message' field is mandatory.");
    }

    @Test
    void testParseJsonLog() {
        String jsonLogString = """
                {
                    "timestamp": "2024-08-28T12:34:56Z",
                    "level": "INFO",
                    "message": "This is a JSON log message",
                    "source": "JsonSource",
                    "thread": "json-thread",
                    "logger": "JsonLogger"
                }
                """;

        LogRecord result = logService.parseJsonLog(jsonLogString);

        assertThat(result).isNotNull();
        assertThat(result.timestamp()).isEqualTo("2024-08-28T12:34:56Z");
        assertThat(result.level()).isEqualTo("INFO");
        assertThat(result.message()).isEqualTo("This is a JSON log message");
        assertThat(result.source()).isEqualTo("JsonSource");
        assertThat(result.thread()).isEqualTo("json-thread");
        assertThat(result.logger()).isEqualTo("JsonLogger");
    }
}
