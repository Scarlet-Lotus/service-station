package org.SS.service_station.model;

public enum TicketStatus {

    NEW,               // New ticket
    ACCEPTED,          // Accepted ticket
    PROCESSING,        // ticket processing, defining the perimeter of the work
    IN_PROGRESS,       // Performing repairs
    COMPLETED;         // Completion of the work

    // Method for verifying that the new status is the next in the sequence
    public boolean isNextStatus(TicketStatus newStatus) {
        return this.ordinal() + 1 == newStatus.ordinal();
    }

    // Method for getting the following status (optional)
    public TicketStatus getNextStatus() {
        if (this.ordinal() + 1 < values().length) {
            return values()[this.ordinal() + 1];
        }
        throw new IllegalStateException("No next status available for " + this);
    }

}
