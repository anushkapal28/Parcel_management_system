package com.anushka.parcel.tracking.dto;

import jakarta.validation.constraints.NotBlank;

public record TrackingEventRequest(
        @NotBlank(message = "trackingNumber is required") String trackingNumber,
        @NotBlank(message = "status is required") String status,
        String location,
        String note,
        String updatedBy) {
}
