package org.example.unit.service;

import org.example.annotations.UnitTest;
import org.example.dto.LogRecord;
import org.example.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@Tag("unit")
class LogServiceTest {

    @InjectMocks
    private LogService logService;

    @Mock
    private KafkaTemplate<String, LogRecord> kafkaTemplate; // Mocks the KafkaTemplate

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize the mocks
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

        CompletableFuture<SendResult<String, LogRecord>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), any(LogRecord.class))).thenReturn(future);

        logService.processLog(validLog);

        future.complete(new SendResult<>(null, null));

        verify(kafkaTemplate).send("logs", validLog);
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
