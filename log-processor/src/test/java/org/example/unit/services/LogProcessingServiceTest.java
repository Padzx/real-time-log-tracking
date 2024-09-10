package org.example.unit.services;

import org.example.annotations.UnitTest;
import org.example.dto.LogRecord;
import org.example.entity.LogProcessing;
import org.example.repository.LogProcessingRepository;
import org.example.service.LogProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@Tag("unit")
class LogProcessingServiceTest {

    @Mock
    private KafkaTemplate<String, LogRecord> kafkaTemplate;

    @Mock
    private LogProcessingRepository logProcessingRepository;

    @InjectMocks
    private LogProcessingService logProcessingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidLogRecord_whenProcessLog_thenLogIsProcessedAndSaved() {
        // Arrange
        LogRecord logRecord = new LogRecord(
                "2024-09-09T14:00:00Z", "INFO", "Test log message",
                "source", "thread", "logger", "2024-09-09T14:00:00Z",
                "General", new HashMap<>(), "unprocessed"
        );

        CompletableFuture future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(any(String.class), any(LogRecord.class))).thenReturn(future);

        // Act
        logProcessingService.processLog(logRecord);

        // Assert
        // Capture the entity saved to the repository
        ArgumentCaptor<LogProcessing> entityCaptor = ArgumentCaptor.forClass(LogProcessing.class);
        verify(logProcessingRepository, times(1)).save(entityCaptor.capture());

        LogProcessing savedEntity = entityCaptor.getValue();
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getMessage()).isEqualTo("Test log message");
        assertThat(savedEntity.getLevel()).isEqualTo("INFO");
        assertThat(savedEntity.getStatus()).isEqualTo("processed");

        // Verify that the log is published to Kafka
        verify(kafkaTemplate, times(1)).send(eq("processed-logs"), any(LogRecord.class));
    }

    @Test
    void givenLogRecord_whenAnalyzeAndFilter_thenLogIsCorrectlyTransformed() {
        // Arrange
        LogRecord logRecord = new LogRecord(
                "2024-09-09T14:00:00Z", "DEBUG", "This is a DEBUG message",
                "source", "thread", "logger", "2024-09-09T14:00:00Z",
                "General", new HashMap<>(), "unprocessed"
        );

        // Act
        LogRecord processedLog = logProcessingService.analyzeAndFilter(logRecord);

        // Assert
        assertThat(processedLog.getMessage()).isEqualTo("This is a INFO message");
        assertThat(processedLog.getStatus()).isEqualTo("processed");
        assertThat(processedLog.getTags()).containsEntry("processed", "true");
    }

    @Test
    void givenLogRecord_whenSaveProcessedLog_thenEntityIsSavedToDatabase() {
        // Arrange
        LogRecord processedLog = new LogRecord(
                "2024-09-09T14:00:00Z", "INFO", "Processed message",
                "source", "thread", "logger", "2024-09-09T14:00:00Z",
                "General", Map.of("processed", "true"), "processed"
        );

        // Act
        logProcessingService.saveProcessedLog(processedLog);

        // Assert
        ArgumentCaptor<LogProcessing> entityCaptor = ArgumentCaptor.forClass(LogProcessing.class);
        verify(logProcessingRepository, times(1)).save(entityCaptor.capture());

        LogProcessing savedEntity = entityCaptor.getValue();
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getMessage()).isEqualTo("Processed message");
        assertThat(savedEntity.getStatus()).isEqualTo("processed");
    }

    @Test
    void givenLogRecord_whenPublishProcessedLog_thenLogIsPublishedToKafka() {
        // Arrange
        LogRecord processedLog = new LogRecord(
                "2024-09-09T14:00:00Z", "INFO", "Processed message",
                "source", "thread", "logger", "2024-09-09T14:00:00Z",
                "General", Map.of("processed", "true"), "processed"
        );

        CompletableFuture<SendResult<String, LogRecord>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(any(String.class), any(LogRecord.class))).thenReturn(future);

        // Act
        logProcessingService.publishProcessedLog(processedLog);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq("processed-logs"), eq(processedLog));
    }
}
