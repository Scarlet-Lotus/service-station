package org.SS.service_station.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_status_history")
@Data // Generate getters, setters, toString, equals and hashCode
@NoArgsConstructor // Constructor without parameters for JPA (Hibernate)
public class StatusUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String changedBy; // Who changed status

    @Column(nullable = false)
    private LocalDateTime changedAt; // When changed

    @Column(nullable = false)
    private String reason; // Change reason

    public StatusUpdate(String changedBy, LocalDateTime changedAt, String reason) {
        this.changedBy = changedBy;
        this.changedAt = changedAt;
        this.reason = reason;
    }

}
