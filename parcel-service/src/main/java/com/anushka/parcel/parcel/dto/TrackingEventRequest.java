package com.anushka.parcel.parcel.dto;

public record TrackingEventRequest(String trackingNumber, String status, String location, String note, String updatedBy) {
}
