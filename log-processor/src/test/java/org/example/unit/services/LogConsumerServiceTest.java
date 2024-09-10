package org.example.unit.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.Mockito.*;

@UnitTest
@Tag("unit")
class LogConsumerServiceTest {

    @Mock
    private LogProcessingService logProcessingService;

    @InjectMocks
    private LogConsumerService logConsumerService;

    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        logConsumerService = new LogConsumerService(logProcessingService, objectMapper, validator);
    }

    @Test
    void givenValidLogRecordJson_whenConsumeLog_thenProcessLogIsCalled() throws Exception {
        // Arrange
        String logJson = "{\"timestamp\":\"2024-09-09T14:00:00Z\",\"level\":\"INFO\",\"message\":\"Test log message\"}";
        LogRecord logRecord = objectMapper.readValue(logJson, LogRecord.class);
        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>("logs", 0, 0L, "key", logJson);

        // Act
        logConsumerService.consumeLog(consumerRecord);

        // Assert
        ArgumentCaptor<LogRecord> logRecordCaptor = ArgumentCaptor.forClass(LogRecord.class);
        verify(logProcessingService, times(1)).processLog(logRecordCaptor.capture());
        LogRecord capturedLogRecord = logRecordCaptor.getValue();

        assertThat(capturedLogRecord).isNotNull();
        assertThat(capturedLogRecord.getTimestamp()).isEqualTo(logRecord.getTimestamp());
        assertThat(capturedLogRecord.getLevel()).isEqualTo(logRecord.getLevel());
        assertThat(capturedLogRecord.getMessage()).isEqualTo(logRecord.getMessage());
    }

    @Test
    void givenInvalidLogJson_whenConsumeLog_thenProcessLogIsNotCalled() {
        // Arrange
        String invalidJson = "{\"timestamp\":\"invalid-timestamp\",\"level\":\"INFO\",\"message\":\"Test log message\"}";
        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>("logs", 0, 0L, "key", invalidJson);

        // Act
        logConsumerService.consumeLog(consumerRecord);

        // Assert
        verify(logProcessingService, never()).processLog(any());
    }

    @Test
    void whenExceptionIsThrownDuringProcessing_thenHandleGracefully() {
        // Arrange
        String logJson = "{\"timestamp\":\"2024-09-09T14:00:00Z\",\"level\":\"INFO\",\"message\":\"Test log message\"}";
        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>("logs", 0, 0L, "key", logJson);

        doThrow(new RuntimeException("Processing error")).when(logProcessingService).processLog(any());

        // Act
        logConsumerService.consumeLog(consumerRecord);

        // Assert
        verify(logProcessingService, times(1)).processLog(any());
    }
}
