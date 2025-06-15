package org.SS.service_station.service;

import org.SS.service_station.dto.StatusUpdateDTO;
import org.SS.service_station.exception.TicketNotFoundException;
import org.SS.service_station.exception.ValidationException;
import org.SS.service_station.kafka.KafkaProducerService;
import org.SS.service_station.model.Ticket;
import org.SS.service_station.model.TicketStatus;
import org.SS.service_station.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private NotificationService notificationService;

    private Ticket ticket;

    @BeforeEach
    public void setUp() {
        ticket = new Ticket("John Doe", "Initial reason");
        ticket.setId(1L);
    }

    // Test for creating a ticket
    @Test
    public void testCreateTicket() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket createdTicket = ticketService.createTicket("John Doe", "Initial reason");

        assertNotNull(createdTicket);
        assertEquals("John Doe", createdTicket.getClientName());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    // Test for getting all tickets by client
    @Test
    public void testGetAllTicketsByClient() {
        when(ticketRepository.findByClientName("John Doe")).thenReturn(List.of(ticket));

        List<Ticket> tickets = ticketService.getAllTicketsByClient("John Doe");

        assertFalse(tickets.isEmpty());
        assertEquals(1, tickets.size());
        assertEquals("John Doe", tickets.getFirst().getClientName());
        verify(ticketRepository, times(1)).findByClientName("John Doe");
    }

    // Test for invalid client name in getAllTicketsByClient
    @Test
    public void testGetAllTicketsByClient_InvalidClientName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ticketService.getAllTicketsByClient(null));

        assertEquals("Client name cannot be null or empty", exception.getMessage());
        verify(ticketRepository, never()).findByClientName(anyString());
    }

    // Test for getting all tickets by status
    @Test
    public void testGetAllTicketsByStatus() {
        when(ticketRepository.findByStatus(TicketStatus.NEW)).thenReturn(List.of(ticket));

        List<Ticket> tickets = ticketService.getAllTicketsByStatus("NEW");

        assertFalse(tickets.isEmpty());
        assertEquals(1, tickets.size());
        assertEquals(TicketStatus.NEW, tickets.getFirst().getStatus());
        verify(ticketRepository, times(1)).findByStatus(TicketStatus.NEW);
    }

    // Test for invalid status in getAllTicketsByStatus
    @Test
    public void testGetAllTicketsByStatus_InvalidStatus() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ticketService.getAllTicketsByStatus("INVALID_STATUS"));

        assertTrue(exception.getMessage().contains("Invalid status"));
        verify(ticketRepository, never()).findByStatus(any(TicketStatus.class));
    }

    // Test for changing ticket status
    @Test
    public void testChangeStatus() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        StatusUpdateDTO request = new StatusUpdateDTO();
        request.setChangedBy("Admin");
        request.setNewStatus("ACCEPTED");
        request.setReason("Accepting the ticket");

        ticketService.changeStatus(1L, request);

        assertEquals(TicketStatus.ACCEPTED, ticket.getStatus());
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(kafkaProducerService, times(1)).sendStatusUpdate(1L, "ACCEPTED", "Admin", "Accepting the ticket");
    }

    // Test for invalid ticket ID in changeStatus
    @Test
    public void testChangeStatus_TicketNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TicketNotFoundException.class, () -> ticketService.changeStatus(1L, new StatusUpdateDTO()));

        assertEquals("Ticket not found", exception.getMessage());
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    // Test for invalid status transition in changeStatus
    @Test
    public void testChangeStatus_InvalidStatusTransition() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        StatusUpdateDTO request = new StatusUpdateDTO();
        request.setChangedBy("Admin");
        request.setNewStatus("COMPLETED");
        request.setReason("Trying to skip steps");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> ticketService.changeStatus(1L, request));

        assertEquals("The new status must be the next status in the sequence.", exception.getMessage());
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    // Test for updating ticket status via Kafka
    @Test
    public void testUpdateTicketStatusFromKafka() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        ticketService.updateTicketStatusFromKafka(1L, "ACCEPTED", "System", "Updating via Kafka");

        assertEquals(TicketStatus.ACCEPTED, ticket.getStatus());
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    // Test for invalid parameters in updateTicketStatusFromKafka
    @Test
    public void testUpdateTicketStatusFromKafka_InvalidParameters() {
        ValidationException exception = assertThrows(ValidationException.class, () -> ticketService.updateTicketStatusFromKafka(1L, "", null, ""));

        Map<String, String> errors = exception.getErrors();

        assertNotNull(errors);
        assertEquals(3, errors.size());
        assertTrue(errors.containsKey("newStatus"));
        assertTrue(errors.containsKey("changedBy"));
        assertTrue(errors.containsKey("reason"));
        verify(ticketRepository, never()).findById(anyLong());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

}