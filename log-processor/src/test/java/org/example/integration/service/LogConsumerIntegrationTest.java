package org.example.integration.service;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.dto.LogRecord;
import org.example.entity.LogProcessing;
import org.example.repository.LogProcessingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@EmbeddedKafka(partitions = 1, topics = { "logs" })
@SpringBootTest
@Tag("integration")
class LogConsumerIntegrationTest {

    private static final String TOPIC = "logs";

    @Autowired
    private KafkaTemplate<String, LogRecord> kafkaTemplate;

    @MockBean
    private LogProcessingRepository logProcessingRepository;

    @BeforeEach
    void setUp() {
        ProducerFactory<String, LogRecord> producerFactory = new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        ));
        kafkaTemplate = new KafkaTemplate<>(producerFactory);
    }

    @Test
    void givenValidLog_whenConsumed_thenLogIsProcessedAndSaved() {
        // Arrange
        LogRecord validLog = LogRecord.builder()
                .timestamp("2024-09-12T12:00:00.000Z")
                .level(LogRecord.LogLevel.INFO)
                .message("Application started successfully.")
                .build();

        // Act
        kafkaTemplate.send(new ProducerRecord<>(TOPIC, validLog));
        kafkaTemplate.flush();

        // Assert
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(logProcessingRepository, times(1)).save(any(LogProcessing.class));
        });
    }

    @Test
    void givenInvalidLog_whenConsumed_thenLogIsNotSaved() {
        // Arrange
        LogRecord invalidLog = LogRecord.builder()
                .timestamp("invalid-timestamp")
                .level(LogRecord.LogLevel.INFO)
                .message("Invalid log.")
                .build();

        // Act
        kafkaTemplate.send(new ProducerRecord<>(TOPIC, invalidLog));
        kafkaTemplate.flush();

        // Assert
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(logProcessingRepository, times(0)).save(any(LogProcessing.class));
        });
    }
}
