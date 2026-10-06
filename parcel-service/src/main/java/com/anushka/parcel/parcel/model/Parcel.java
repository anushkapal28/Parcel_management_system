package com.anushka.parcel.parcel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** Indexes speed up the queries we run most: filter by status, agent and creator. */
@Entity
@Table(name = "parcels", indexes = {
        @Index(name = "idx_parcel_status", columnList = "status"),
        @Index(name = "idx_parcel_agent", columnList = "assignedAgentEmail"),
        @Index(name = "idx_parcel_creator", columnList = "createdBy")
})
@Getter @Setter
public class Parcel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String trackingNumber;

    @Column(nullable = false) private String senderName;
    @Column(nullable = false) private String receiverName;
    @Column(nullable = false) private String receiverAddress;
    @Column(nullable = false) private Double weightKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ParcelStatus status;

    private String assignedAgentEmail;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
}
