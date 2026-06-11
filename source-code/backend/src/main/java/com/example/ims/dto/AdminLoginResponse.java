package com.example.ims.dto;

public record AdminLoginResponse(
        String username,
        String displayName,
        String role
) {
}
