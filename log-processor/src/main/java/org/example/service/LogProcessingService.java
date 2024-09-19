package org.example.service;

import org.example.dto.LogRecord;
import org.example.entity.LogProcessing;
import org.example.repository.LogProcessingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class LogProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(LogProcessingService.class);
    private static final String PROCESSED_LOGS_TOPIC = "processed-logs";

    private final KafkaTemplate<String, LogRecord> kafkaTemplate;
    private final LogProcessingRepository logProcessingRepository;

    public LogProcessingService(KafkaTemplate<String, LogRecord> kafkaTemplate, LogProcessingRepository logProcessingRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.logProcessingRepository = logProcessingRepository;
    }

    public void processLog(LogRecord logRecord) {
        // Analyze, filter, and transform the log
        LogRecord processedLog = analyzeAndFilter(logRecord);

        // Persist the processed log in PostgreSQL
        saveProcessedLog(processedLog);

        // Publish the processed log back to Kafka
        publishProcessedLog(processedLog);
    }

    public LogRecord analyzeAndFilter(LogRecord logRecord) {

        String processedMessage = logRecord.getMessage().replaceAll("DEBUG", "INFO");
        Map<String, String> tags = new HashMap<>(logRecord.getTags() != null ? logRecord.getTags() : new HashMap<>());
        tags.put("processed", "true");

        String processedTimestamp = Instant.now().toString();
        String category = determineCategory(logRecord.getLevel());

        return LogRecord.builder()
                .timestamp(logRecord.getTimestamp())
                .level(logRecord.getLevel())
                .message(processedMessage)
                .source(logRecord.getSource())
                .thread(logRecord.getThread())
                .logger(logRecord.getLogger())
                .processedTimestamp(processedTimestamp)
                .category(category)
                .tags(tags)
                .status("processed")
                .build();
    }

    public void saveProcessedLog(LogRecord processedLog) {
        LogProcessing entity = new LogProcessing();
        entity.setTimestamp(Instant.parse(processedLog.getTimestamp())); // Parse String to Instant
        entity.setLevel(processedLog.getLevel().name()); // Convert LogLevel to String using .name()
        entity.setMessage(processedLog.getMessage());
        entity.setSource(processedLog.getSource());
        entity.setThread(processedLog.getThread());
        entity.setLogger(processedLog.getLogger());
        entity.setProcessedTimestamp(Instant.parse(processedLog.getProcessedTimestamp())); // Parse String to Instant
        entity.setCategory(processedLog.getCategory());
        entity.setTags(processedLog.getTags());
        entity.setStatus(processedLog.getStatus());

        logProcessingRepository.save(entity);
        logger.info("Persisted processed log to PostgreSQL: {}", entity);
    }

    private String determineCategory(LogRecord.LogLevel level) {
        return switch (level) {
            case ERROR -> "Critical";
            case WARN -> "Warning";
            case INFO -> "Information";
            default -> "General";
        };
    }

    public void publishProcessedLog(LogRecord processedLog) {
        CompletableFuture<SendResult<String, LogRecord>> future = kafkaTemplate.send(PROCESSED_LOGS_TOPIC, processedLog);

        future.thenAccept(result ->
                logger.info("Successfully published processed log to Kafka: {}", processedLog)
        ).exceptionally(ex -> {
            logger.error("Failed to publish processed log to Kafka: {}", processedLog, ex);
            return null;
        });
    }
}
