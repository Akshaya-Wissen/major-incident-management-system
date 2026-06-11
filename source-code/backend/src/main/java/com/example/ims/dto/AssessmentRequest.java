package com.example.ims.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssessmentRequest(
        @NotBlank String customerImpact,
        @NotBlank String businessImpact,
        @NotNull @Min(0) Integer affectedUsers,
        @NotBlank String currentHypothesis
) {
}
