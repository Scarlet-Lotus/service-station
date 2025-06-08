package org.SS.service_station.mapper;

import org.SS.service_station.dto.StatusChangeDTO;
import org.SS.service_station.dto.TicketResponseDTO;
import org.SS.service_station.model.Ticket;

public class TicketMapper {

    // Static method for converting Ticket entity to TicketResponseDTO
    public static TicketResponseDTO toResponse(Ticket ticket) {
        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getClientName(),
                ticket.getStatus().name(),
                ticket.getCreatedAt(),
                ticket.getHistory().stream()
                        .map(history -> new StatusChangeDTO(
                                history.getChangedBy(),
                                history.getChangedAt(),
                                history.getReason()
                        ))
                        .toList()
        );
    }

}
