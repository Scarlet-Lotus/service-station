package org.SS.service_station.service;

import org.SS.service_station.dto.StatusUpdateDTO;
import org.SS.service_station.exception.TicketNotFoundException;
import org.SS.service_station.exception.ValidationException;
import org.SS.service_station.kafka.KafkaProducerService;
import org.SS.service_station.model.Ticket;
import org.SS.service_station.model.TicketStatus;
import org.SS.service_station.repository.TicketRepository;
import org.SS.service_station.validation.StatusValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final KafkaProducerService kafkaProducerService;
    private final NotificationService notificationService;

    public TicketService(TicketRepository ticketRepository, KafkaProducerService kafkaProducerService, NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.notificationService = notificationService;
    }

    // Creating ticket
    public Ticket createTicket(String clientName, String reason) {
        Ticket ticket = new Ticket(clientName, reason);
        return ticketRepository.save(ticket);
    }

    // Getting all client tickets
    public List<Ticket> getAllTicketsByClient(String clientName) {
        if (clientName == null || clientName.trim().isEmpty()) {
            throw new IllegalArgumentException("Client name cannot be null or empty");
        }
        return ticketRepository.findByClientName(clientName);
    }

    // Getting all tickets in a certain status
    public List<Ticket> getAllTicketsByStatus(String status) {
        // Using the static method
        TicketStatus ticketStatus = StatusValidation.validateAndParseStatus(status);
        return ticketRepository.findByStatus(ticketStatus);
    }

    // Changing ticket status
    public void changeStatus(Long ticketId, StatusUpdateDTO request) {
        // Validating updated ticket status
        // StatusValidation.validateStatusUpdateDTO(request);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        // Determining who changed the status
        String changedBy = request.getChangedBy();

        // Validating new status and converting string to ticket status
        TicketStatus newStatus = StatusValidation.validateAndParseStatus(request.getNewStatus());

        // Checking that the new status is the following in the sequence
        if (!ticket.getStatus().isNextStatus(newStatus)) {
            throw new IllegalArgumentException("The new status must be the next status in the sequence.");
        }

        // Adding ticket changes to the history
        ticket.changeStatus(newStatus, changedBy, request.getReason());
        ticketRepository.save(ticket);

        // Sending a notification via Kafka
        try {
            kafkaProducerService.sendStatusUpdate(ticketId, request.getNewStatus(), request.getChangedBy(), request.getReason());
        } catch (Exception e) {
            logger.error("Failed to send status update to Kafka: {}", e.getMessage(), e);
        }

        // Notifying the client when the work is completed
        if (newStatus == TicketStatus.COMPLETED) {
            notificationService.notifyClient(ticket.getClientName(), "Your ticket has been completed.");
        }
    }

    // Method for updating the application status via Kafka
    public void updateTicketStatusFromKafka(Long ticketId, String newStatus, String changedBy, String reason) {
        // Map for validation errors' storing
        Map<String, String> errors = getStringStringMap(newStatus, changedBy, reason);

        // If there are errors, we throw the ValidationException exception.
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // New status validation
        TicketStatus status = StatusValidation.validateAndParseStatus(newStatus);

        // Updating status
        ticket.changeStatus(status, changedBy, reason);
        ticketRepository.save(ticket);

        logger.info("Updated ticket status via Kafka: ticketId={}, status={}, changed by={}, reason={}", ticketId, newStatus, changedBy, reason);
    }

    private static Map<String, String> getStringStringMap(String newStatus, String changedBy, String reason) {
        Map<String, String> errors = new HashMap<>();

        // Validating parameters
        if (newStatus == null || newStatus.trim().isEmpty()) {
            errors.put("newStatus", "New status cannot be null or empty");
        }
        if (changedBy == null || changedBy.trim().isEmpty()) {
            errors.put("changedBy", "Changed by cannot be null or empty");
        }
        if (reason == null || reason.trim().isEmpty()) {
            errors.put("reason", "Reason cannot be null or empty");
        }
        return errors;
    }

}
