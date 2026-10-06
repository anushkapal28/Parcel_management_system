package com.anushka.parcel.parcel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateParcelRequest(
        @NotBlank(message = "senderName is required") String senderName,
        @NotBlank(message = "receiverName is required") String receiverName,
        @NotBlank(message = "receiverAddress is required") String receiverAddress,
        @NotNull(message = "weightKg is required") @Positive(message = "weightKg must be greater than 0") Double weightKg) {
}
