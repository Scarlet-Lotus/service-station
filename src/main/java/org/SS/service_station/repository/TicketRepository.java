package org.SS.service_station.repository;

import org.SS.service_station.model.Ticket;
import org.SS.service_station.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Method for getting all tickets by the client's name
    List<Ticket> findByClientName(String clientName);

    // Method for getting all tickets in the certain status
    List<Ticket> findByStatus(TicketStatus status);

}
