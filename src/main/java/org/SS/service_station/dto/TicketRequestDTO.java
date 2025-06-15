package org.SS.service_station.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDTO {

    @NotBlank(message = "Client name can not be null or empty")
    private String clientName; // Client Name

    @NotBlank(message = "Reason can not be null or empty")
    private String reason; // Change status reason

}
