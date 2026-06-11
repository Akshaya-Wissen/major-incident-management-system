package com.example.ims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResolutionRequest(
        @NotBlank String resolutionSummary,
        @NotBlank String mitigationSteps,
        @NotBlank @Size(max = 120) String resolvedBy
) {
}
