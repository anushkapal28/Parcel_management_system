package com.anushka.parcel.parcel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AssignRequest(
        @NotBlank(message = "agentEmail is required") @Email(message = "agentEmail is not valid") String agentEmail) {
}
