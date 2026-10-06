package com.anushka.parcel.auth.dto;

public record AuthResponse(String token, String tokenType, String name, String email, String role) {
}
