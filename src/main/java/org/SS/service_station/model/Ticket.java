package org.SS.service_station.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tickets")
@Data // Generate getters, setters, toString, equals and hashCode
@NoArgsConstructor // Constructor without parameters for JPA (Hibernate)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientName; // Client name

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status; // Current ticket status

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Ticket creation date

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ticket_id")
    private List<StatusUpdate> history = new ArrayList<>(); // Status change history

    public Ticket(String clientName, String reason) {
        this.clientName = clientName;
        this.status = TicketStatus.NEW; // Initial status - "new ticket"
        this.createdAt = LocalDateTime.now();
        this.history.add(new StatusUpdate(clientName, LocalDateTime.now(), reason));
    }

    // Method for changing the ticket status
    public void changeStatus(TicketStatus newStatus, String changedBy, String reason) {
        this.status = newStatus;
        this.history.add(new StatusUpdate(changedBy, LocalDateTime.now(), reason));
    }

}
