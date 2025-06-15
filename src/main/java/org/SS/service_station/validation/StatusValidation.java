package org.SS.service_station.validation;

import lombok.experimental.UtilityClass;
import org.SS.service_station.model.TicketStatus;

@UtilityClass
public class StatusValidation {

    // A method for validating the correctness of the status string
    public TicketStatus validateAndParseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }

        try {
            return TicketStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

}
