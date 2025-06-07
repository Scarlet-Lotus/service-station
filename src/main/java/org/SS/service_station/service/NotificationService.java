package org.SS.service_station.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    // Method for "notifying" the client (logging)
    public void notifyClient(String clientName, String message) {
        logger.info("SMS to {}: {}", clientName, message);
    }

}
