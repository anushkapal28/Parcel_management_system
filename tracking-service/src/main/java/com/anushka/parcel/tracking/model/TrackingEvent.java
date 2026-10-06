package com.anushka.parcel.tracking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tracking_events", indexes = @Index(name = "idx_tracking_number", columnList = "trackingNumber"))
@Getter @Setter
public class TrackingEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String trackingNumber;

    @Column(nullable = false, length = 30)
    private String status;

    private String location;
    private String note;
    private String updatedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }
}
