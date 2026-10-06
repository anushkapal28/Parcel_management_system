package com.anushka.parcel.tracking.dto;

import com.anushka.parcel.tracking.model.TrackingEvent;

import java.time.Instant;

public record TrackingEventResponse(String trackingNumber, String status, String location, String note,
                                    String updatedBy, Instant timestamp) {
    public static TrackingEventResponse from(TrackingEvent e) {
        return new TrackingEventResponse(e.getTrackingNumber(), e.getStatus(), e.getLocation(),
                e.getNote(), e.getUpdatedBy(), e.getCreatedAt());
    }
}
