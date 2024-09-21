package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.LogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.concurrent.CompletableFuture;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    private static final String KAFKA_TOPIC = "logs";

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public LogService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void processLog(LogRecord log) {
        validateTimestamp(log.timestamp());
        validateLevel(log.level());
        validateMessage(log.message());

        String logMessage = String.format(
                "Timestamp: %s, Level: %s, Message: %s, Source: %s, Thread: %s, Logger: %s",
                log.timestamp(), log.level(), log.message(), log.source(), log.thread(), log.logger()
        );

        sendLogToKafka(logMessage);
    }

    public LogRecord parsePlaintextLog(String logText) {
        if (logText == null || logText.isEmpty()) {
            throw new IllegalArgumentException("The 'message' field is mandatory.");
        }

        return new LogRecord(
                Instant.now().toString(),
                "INFO",
                logText,
                "Plaintext Source",
                Thread.currentThread().getName(),
                "PlaintextLogger"
        );
    }

    public LogRecord parseJsonLog(String jsonLogString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonLogString, LogRecord.class);
        } catch (Exception e) {
            logger.error("Failed to parse JSON log: {}", jsonLogString, e);
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        }
    }

    private void validateTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            throw new IllegalArgumentException("The 'timestamp' field is mandatory.");
        }

        if (!isValidTimestamp(timestamp)) {
            throw new IllegalArgumentException("Invalid timestamp format.");
        }
    }

    private void validateLevel(String level) {
        if (level == null || level.isEmpty()) {
            throw new IllegalArgumentException("The 'level' field is mandatory.");
        }
    }

    private void validateMessage(String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("The 'message' field is mandatory.");
        }
    }

    private boolean isValidTimestamp(String timestamp) {
        try {
            Instant.parse(timestamp);
            return true;
        } catch (DateTimeParseException e) {
            logger.warn("Invalid timestamp: {}", timestamp, e);
            return false;
        }
    }

    private void sendLogToKafka(String logMessage) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(KAFKA_TOPIC, logMessage);

        future.thenAccept(result ->
                logger.info("Successfully sent log to Kafka: {}", logMessage)
        ).exceptionally(ex -> {
            logger.error("Failed to send log to Kafka: {}", logMessage, ex);
            return null;
        });
    }
}
