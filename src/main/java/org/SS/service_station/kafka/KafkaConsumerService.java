package org.SS.service_station.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    // Method for processing messages from Kafka
    @KafkaListener(topics = "status-updates", groupId = "service-station-group")
    public void handleStatusUpdate(String message) {
        logger.info("Received status update from Kafka: {}", message);
        // Here you can add logic to process the message.
    }

}
