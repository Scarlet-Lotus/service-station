package org.SS.service_station.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistoryDTO {

    private String changedBy; // Who changed status
    private LocalDateTime changedAt; // When was changed
    private String reason; // Change reason

}
