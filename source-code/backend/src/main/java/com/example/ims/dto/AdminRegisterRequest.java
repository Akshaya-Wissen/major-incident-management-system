package com.example.ims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminRegisterRequest(
        @NotBlank @Size(max = 80) String username,
        @NotBlank @Size(max = 120) String displayName,
        @NotBlank @Size(min = 6, max = 72) String password,
        @NotBlank @Size(max = 40) String role
) {
}
