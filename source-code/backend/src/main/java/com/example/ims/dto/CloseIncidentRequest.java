package com.example.ims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CloseIncidentRequest(
        @NotBlank @Size(max = 120) String closedBy,
        @NotBlank String closureSummary
) {
}
