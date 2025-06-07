package org.SS.service_station.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDTO {

    private Long id; // Ticket identification
    private String clientName; // Client name
    private String status; // Current ticket Status
    private LocalDateTime createdAt; // Ticket creation date
    private List<StatusChangeDTO> history; // Change status history

}
