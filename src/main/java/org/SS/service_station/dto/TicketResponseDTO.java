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

    private Long id; // Идентификатор заявки
    private String clientName; // Имя клиента
    private String status; // Текущий статус заявки
    private LocalDateTime createdAt; // Дата создания заявки
    private List<StatusChangeDTO> history; // История изменений статусов

}
