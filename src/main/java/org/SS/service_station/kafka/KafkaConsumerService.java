package org.SS.service_station.kafka;

import org.SS.service_station.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final TicketService ticketService;

    public KafkaConsumerService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Method for processing messages from Kafka
    @KafkaListener(topics = "status-updates", groupId = "service-station-group")
    public void handleStatusUpdate(String message) {
        logger.info("Received status update from Kafka: {}", message);

        // Parse the message (e.g., "TicketId: 1, NewStatus: COMPLETED, Changed by: Admin, Reason: Work was completed")
        String[] parts = message.split(", ");
        if (parts.length != 4) {
            logger.error("Invalid message format: {}", message);
            return;
        }

        Long ticketId = Long.parseLong(parts[0].split(": ")[1]);
        String newStatus = parts[1].split(": ")[1];
        String changedBy = parts[2].split(": ")[1];
        String reason = parts[3].split(": ")[1];

        // Update the ticket status using the service
        ticketService.updateTicketStatusFromKafka(ticketId, newStatus, changedBy, reason);
    }

}
