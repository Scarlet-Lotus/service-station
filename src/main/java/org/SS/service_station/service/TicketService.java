package org.SS.service_station.service;

import org.SS.service_station.dto.StatusUpdateRequestDTO;
import org.SS.service_station.kafka.KafkaProducerService;
import org.SS.service_station.model.Ticket;
import org.SS.service_station.model.TicketStatus;
import org.SS.service_station.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final KafkaProducerService kafkaProducerService;

    public TicketService(TicketRepository ticketRepository, KafkaProducerService kafkaProducerService) {
        this.ticketRepository = ticketRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    // Creating ticket
    public Ticket createTicket(String clientName) {
        Ticket ticket = new Ticket(clientName);
        return ticketRepository.save(ticket);
    }

    // Getting all client tickets
    public List<Ticket> getAllTicketsByClient(String clientName) {
        return ticketRepository.findByClientName(clientName);
    }

    // Getting all tickets in a certain status
    public List<Ticket> getAllTicketsByStatus(String status) {
        try {
            TicketStatus ticketStatus = TicketStatus.valueOf(status.toUpperCase());
            return ticketRepository.findByStatus(ticketStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    // Changing ticket status
    public void changeStatus(Long ticketId, StatusUpdateRequestDTO request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Validating updated ticket status
        TicketStatus newStatus;
        try {
            newStatus = TicketStatus.valueOf(request.getNewStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + request.getNewStatus());
        }

        // Adding ticket changes to the revision history
        ticket.changeStatus(newStatus, "System", request.getReason());
        ticketRepository.save(ticket);

        // Sending a notification via Kafka
        kafkaProducerService.sendStatusUpdate(ticketId, request.getNewStatus(), request.getReason());
    }

}
