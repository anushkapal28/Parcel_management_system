package com.anushka.parcel.parcel.dto;

import com.anushka.parcel.parcel.model.Parcel;
import com.anushka.parcel.parcel.model.ParcelStatus;

import java.time.Instant;

public record ParcelResponse(String trackingNumber, String senderName, String receiverName, String receiverAddress,
                             Double weightKg, ParcelStatus status, String assignedAgentEmail, String createdBy,
                             Instant createdAt, Instant updatedAt) {
    public static ParcelResponse from(Parcel p) {
        return new ParcelResponse(p.getTrackingNumber(), p.getSenderName(), p.getReceiverName(), p.getReceiverAddress(),
                p.getWeightKg(), p.getStatus(), p.getAssignedAgentEmail(), p.getCreatedBy(),
                p.getCreatedAt(), p.getUpdatedAt());
    }
}
