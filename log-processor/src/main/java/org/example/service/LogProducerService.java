package org.example.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public LogProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishProcessedLog(String processedLog) {
        kafkaTemplate.send(new ProducerRecord<>("processed-logs", processedLog));
    }
}
