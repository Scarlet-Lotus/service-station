package org.SS.service_station.model;

public enum TicketStatus {
    ACCEPTED,          // Accepted ticket
    PROCESSING,        // ticket processing, defining the perimeter of the work
    IN_PROGRESS,       // Performing repairs
    COMPLETED          // Completion of the work
}
