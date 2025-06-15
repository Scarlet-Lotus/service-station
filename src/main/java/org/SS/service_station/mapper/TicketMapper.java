package org.SS.service_station.mapper;

import lombok.experimental.UtilityClass;
import org.SS.service_station.dto.StatusHistoryDTO;
import org.SS.service_station.dto.StatusUpdateDTO;
import org.SS.service_station.dto.TicketResponseDTO;
import org.SS.service_station.model.Ticket;

@UtilityClass
public class TicketMapper {

    // Static method for converting Ticket entity to TicketResponseDTO
    public TicketResponseDTO toResponse(Ticket ticket) {
        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getClientName(),
                ticket.getStatus().name(),
                ticket.getCreatedAt(),
                ticket.getHistory().stream()
                        .map(history -> new StatusHistoryDTO(
                                history.getChangedBy(),
                                history.getChangedAt(),
                                history.getReason()
                        ))
                        .toList()
        );
    }

}
