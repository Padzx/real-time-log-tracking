package org.example.unit.services;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.annotations.UnitTest;
import org.example.service.LogProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@Tag("unit")
class LogProducerServiceTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private LogProducerService logProducerService;

    @BeforeEach
    void setUp() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        logProducerService = new LogProducerService(kafkaTemplate);
    }

    @Test
    void shouldPublishProcessedLogToKafka() {
        // Arrange
        String processedLog = "Test log message";
        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);

        // Act
        logProducerService.publishProcessedLog(processedLog);

        // Assert
        verify(kafkaTemplate, times(1)).send(captor.capture());
        ProducerRecord<String, String> capturedRecord = captor.getValue();
        assertThat(capturedRecord.topic()).isEqualTo("processed-logs");
        assertThat(capturedRecord.value()).isEqualTo(processedLog);
    }
}
