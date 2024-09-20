package org.example.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.LogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;

@Service
public class LogConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(LogConsumerService.class);
    private final LogProcessingService logProcessingService;
    private final Validator validator;

    public LogConsumerService(LogProcessingService logProcessingService, Validator validator) {
        this.logProcessingService = logProcessingService;
        this.validator = validator;
    }

    @KafkaListener(topics = "logs", groupId = "log-processor-group")
    public void consumeLog(ConsumerRecord<String, LogRecord> record) {
        try {
            LogRecord logRecord = record.value(); // Agora recebemos o LogRecord diretamente
            logger.info("Consumed log: {}", logRecord);

            validateLogRecord(logRecord);

            logProcessingService.processLog(logRecord);

        } catch (ConstraintViolationException e) {
            logger.error("Validation failed for log: {}", record.value(), e);
            // Lógica adicional para logs inválidos, como envio para um tópico de erro

        } catch (Exception e) {
            logger.error("Failed to process log: {}", record.value(), e);
            // Lógica adicional para lidar com falhas, como retry ou fallback
        }
    }

    private void validateLogRecord(LogRecord logRecord) {
        Set<ConstraintViolation<LogRecord>> violations = validator.validate(logRecord);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("LogRecord validation failed", violations);
        }
    }
}
