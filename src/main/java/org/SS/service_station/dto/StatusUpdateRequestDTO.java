package org.SS.service_station.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequestDTO {

    private String newStatus; // New status ("ACCEPTED", "PROCESSING", "IN_PROGRESS", "COMPLETED")
    private String reason;    // Change status reason

}
