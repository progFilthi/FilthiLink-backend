package com.product.filthilink.entities.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "processed_events")
@Getter
@Setter
@NoArgsConstructor
public class ProcessedEvent {

    @Id
    @Column(nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private Instant processedAt;

    @Version
    private Long version;

    public ProcessedEvent(String eventId, String eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.processedAt = Instant.now();
    }
}
