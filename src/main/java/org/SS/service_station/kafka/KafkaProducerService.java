package org.SS.service_station.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Method for sending a status change message
    public void sendStatusUpdate(Long ticketId, String newStatus, String changedBy, String reason) {
        String message = String.format("Ticket ID: %d, New Status: %s, Changed by: %s, Reason: %s", ticketId, newStatus, changedBy, reason);
        try {
            kafkaTemplate.send("status-updates", message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }

}
