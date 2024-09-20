package org.example.unit.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.annotations.UnitTest;
import org.example.dto.LogRecord;
import org.example.service.LogConsumerService;
import org.example.service.LogProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@Tag("unit")
class LogConsumerServiceTest {

    @Mock
    private LogProcessingService logProcessingService;

    @InjectMocks
    private LogConsumerService logConsumerService;

    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        logConsumerService = new LogConsumerService(logProcessingService, validator);
    }

    @Test
    void givenValidLogRecord_whenConsumeLog_thenProcessLogIsCalled() {
        // Arrange
        LogRecord validLogRecord = LogRecord.builder()
                .timestamp("2024-09-09T14:00:00Z")
                .level(LogRecord.LogLevel.INFO)
                .message("Test log message")
                .build();
        ConsumerRecord<String, LogRecord> consumerRecord = new ConsumerRecord<>("logs", 0, 0L, "key", validLogRecord);

        // Act
        logConsumerService.consumeLog(consumerRecord);

        // Assert
        ArgumentCaptor<LogRecord> logRecordCaptor = ArgumentCaptor.forClass(LogRecord.class);
        verify(logProcessingService, times(1)).processLog(logRecordCaptor.capture());
        LogRecord capturedLogRecord = logRecordCaptor.getValue();

        assertThat(capturedLogRecord).isNotNull();
        assertThat(capturedLogRecord.getTimestamp()).isEqualTo(validLogRecord.getTimestamp());
        assertThat(capturedLogRecord.getLevel()).isEqualTo(validLogRecord.getLevel());
        assertThat(capturedLogRecord.getMessage()).isEqualTo(validLogRecord.getMessage());
    }

    @Test
    void givenInvalidLogRecord_whenConsumeLog_thenProcessLogIsNotCalled() {
        // Arrange
        LogRecord invalidLogRecord = LogRecord.builder()
                .timestamp("invalid-timestamp")
                .level(LogRecord.LogLevel.INFO)
                .message("Invalid log message")
                .build();
        ConsumerRecord<String, LogRecord> consumerRecord = new ConsumerRecord<>("logs", 0, 0L, "key", invalidLogRecord);

        // Act
        logConsumerService.consumeLog(consumerRecord);

        // Assert
        verify(logProcessingService, never()).processLog(any());
    }

    @Test
    void whenExceptionIsThrownDuringProcessing_thenHandleGracefully() {
        // Arrange
        LogRecord logRecord = LogRecord.builder()
                .timestamp("2024-09-09T14:00:00Z")
                .level(LogRecord.LogLevel.INFO)
                .message("Test log message")
                .build();
        ConsumerRecord<String, LogRecord> consumerRecord = new ConsumerRecord<>("logs", 0, 0L, "key", logRecord);

        doThrow(new RuntimeException("Processing error")).when(logProcessingService).processLog(any());

        // Act
        logConsumerService.consumeLog(consumerRecord);

        // Assert
        verify(logProcessingService, times(1)).processLog(any());
    }
}
