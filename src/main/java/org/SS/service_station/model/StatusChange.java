package org.SS.service_station.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String changedBy; // Кто изменил статус

    @Column(nullable = false)
    private LocalDateTime changedAt; // Когда изменили

    @Column(nullable = false)
    private String reason; // Причина изменения
}
