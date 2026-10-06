package com.anushka.parcel.parcel.dto;

import com.anushka.parcel.parcel.model.ParcelStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "status is required") ParcelStatus status,
        String location,
        String note) {
}
