package com.anushka.parcel.tracking.exception;

import java.time.Instant;
import java.util.Map;

/** Standard JSON error body returned by every service. */
public record ApiError(Instant timestamp, int status, String error, String message, Map<String, String> fieldErrors) {
    public static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now(), status, error, message, null);
    }
}
