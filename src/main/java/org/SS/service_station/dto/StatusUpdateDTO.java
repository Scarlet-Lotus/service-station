package org.SS.service_station.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateDTO {

    @NotBlank(message = "ChangedBy can not be null or empty")
    private String changedBy; // Who changed the status

    @NotBlank(message = "New status can not be null or empty")
    private String newStatus; // New status ("ACCEPTED", "PROCESSING", "IN_PROGRESS", "COMPLETED")

    @NotBlank(message = "Reason can not be null or empty")
    private String reason;    // Change status reason

    private LocalDateTime changedAt; // When changed (optional for output)

}
