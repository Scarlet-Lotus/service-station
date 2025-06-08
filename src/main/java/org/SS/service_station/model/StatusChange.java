package org.SS.service_station.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data // Generate getters, setters, toString, equals and hashCode
@NoArgsConstructor // Constructor without parameters for JPA (Hibernate)
@RequiredArgsConstructor // Constructor with required parameters
public class StatusChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NonNull
    private String changedBy; // Who changed status

    @Column(nullable = false)
    @NonNull
    private LocalDateTime changedAt; // When changed

    @Column(nullable = false)
    @NonNull
    private String reason; // Change reason

}
