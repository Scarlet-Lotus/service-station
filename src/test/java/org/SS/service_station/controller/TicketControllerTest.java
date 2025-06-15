package org.SS.service_station.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.SS.service_station.dto.StatusUpdateDTO;
import org.SS.service_station.dto.TicketRequestDTO;
import org.SS.service_station.dto.TicketResponseDTO;
import org.SS.service_station.model.Ticket;
import org.SS.service_station.model.TicketStatus;
import org.SS.service_station.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTicket_SuccessfulCase() {
        // Arrange
        TicketRequestDTO request = new TicketRequestDTO();
        request.setClientName("John Doe");
        request.setReason("Car repair");

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setClientName("John Doe");
        ticket.setStatus(TicketStatus.NEW);
        ticket.setCreatedAt(LocalDateTime.now());

        when(ticketService.createTicket("John Doe", "Car repair")).thenReturn(ticket);

        // Act
        ResponseEntity<TicketResponseDTO> response = ticketController.createTicket(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getClientName());
        verify(ticketService, times(1)).createTicket("John Doe", "Car repair");
    }

    @Test
    void createTicket_InvalidClientName() {
        // Arrange
        TicketRequestDTO request = new TicketRequestDTO();
        request.setClientName(""); // Недопустимое значение
        request.setReason("Car repair");

        // Act
        Set<ConstraintViolation<TicketRequestDTO>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<TicketRequestDTO> violation = violations.iterator().next();
        assertEquals("Client name can not be null or empty", violation.getMessage());

        // Проверяем, что метод сервиса не вызывался
        verify(ticketService, never()).createTicket(anyString(), anyString());
    }

    @Test
    void getTicketsByClient_SuccessfulCase() {
        // Arrange
        String clientName = "John Doe";
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setClientName(clientName);
        ticket.setStatus(TicketStatus.NEW);
        ticket.setCreatedAt(LocalDateTime.now());

        when(ticketService.getAllTicketsByClient(clientName)).thenReturn(Collections.singletonList(ticket));

        // Act
        ResponseEntity<List<TicketResponseDTO>> response = ticketController.getTicketsByClient(clientName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(clientName, response.getBody().getFirst().getClientName());
        verify(ticketService, times(1)).getAllTicketsByClient(clientName);
    }

    @Test
    void getTicketsByClient_ClientNotFound() {
        // Arrange
        String clientName = "UnknownClient";
        when(ticketService.getAllTicketsByClient(clientName)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<TicketResponseDTO>> response = ticketController.getTicketsByClient(clientName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(ticketService, times(1)).getAllTicketsByClient(clientName);
    }

    @Test
    void getTicketsByStatus_SuccessfulCase() {
        // Arrange
        String status = "NEW";
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setClientName("John Doe");
        ticket.setStatus(TicketStatus.NEW);
        ticket.setCreatedAt(LocalDateTime.now());

        when(ticketService.getAllTicketsByStatus(status)).thenReturn(Collections.singletonList(ticket));

        // Act
        ResponseEntity<List<TicketResponseDTO>> response = ticketController.getTicketsByStatus(status);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals("NEW", response.getBody().getFirst().getStatus());
        verify(ticketService, times(1)).getAllTicketsByStatus(status);
    }

    @Test
    void getTicketsByStatus_InvalidStatus() {
        // Arrange
        String invalidStatus = "INVALID_STATUS";

        when(ticketService.getAllTicketsByStatus(invalidStatus))
                .thenThrow(new IllegalArgumentException("Invalid status: " + invalidStatus));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ticketController.getTicketsByStatus(invalidStatus));

        assertTrue(exception.getMessage().contains("Invalid status: " + invalidStatus));
        verify(ticketService, times(1)).getAllTicketsByStatus(invalidStatus);
    }

    @Test
    void changeStatus_SuccessfulCase() {
        // Arrange
        Long ticketId = 1L;
        StatusUpdateDTO request = new StatusUpdateDTO();
        request.setChangedBy("Admin");
        request.setNewStatus("IN_PROGRESS");
        request.setReason("Moving to next step");

        doNothing().when(ticketService).changeStatus(ticketId, request);

        // Act
        ResponseEntity<Void> response = ticketController.changeStatus(ticketId, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ticketService, times(1)).changeStatus(ticketId, request);
    }

    @Test
    void changeStatus_InvalidStatusTransition() {
        // Arrange
        Long ticketId = 1L;
        StatusUpdateDTO request = new StatusUpdateDTO();
        request.setChangedBy("Admin");
        request.setNewStatus("COMPLETED");
        request.setReason("Trying to skip steps");

        doThrow(new IllegalArgumentException("The new status must be the next status in the sequence."))
                .when(ticketService).changeStatus(ticketId, request);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ticketController.changeStatus(ticketId, request));

        assertTrue(exception.getMessage().contains("The new status must be the next status in the sequence."));
        verify(ticketService, times(1)).changeStatus(ticketId, request);
    }

}