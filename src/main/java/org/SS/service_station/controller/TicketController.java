package org.SS.service_station.controller;

import org.SS.service_station.dto.StatusUpdateRequestDTO;
import org.SS.service_station.dto.TicketRequestDTO;
import org.SS.service_station.dto.TicketResponseDTO;
import org.SS.service_station.mapper.TicketMapper;
import org.SS.service_station.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Creating ticket
    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketRequestDTO request) {
        return ResponseEntity.ok(
                TicketMapper.toResponse(ticketService.createTicket(request.getClientName()))
        );
    }

    // Getting all client tickets
    @GetMapping("/client/{clientName}")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByClient(@PathVariable String clientName) {
        List<TicketResponseDTO> tickets = ticketService.getAllTicketsByClient(clientName)
                .stream()
                .map(TicketMapper::toResponse) // Using the static mapper method
                .toList();
        return ResponseEntity.ok(tickets);
    }

    // Getting all tickets in a certain status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByStatus(@PathVariable String status) {
        List<TicketResponseDTO> tickets = ticketService.getAllTicketsByStatus(status)
                .stream()
                .map(TicketMapper::toResponse) // Using the static mapper method
                .toList();
        return ResponseEntity.ok(tickets);
    }

    // Changing ticket status
    @PutMapping("/{ticketId}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long ticketId, @RequestBody StatusUpdateRequestDTO request) {
        ticketService.changeStatus(ticketId, request);
        return ResponseEntity.ok().build();
    }

}
