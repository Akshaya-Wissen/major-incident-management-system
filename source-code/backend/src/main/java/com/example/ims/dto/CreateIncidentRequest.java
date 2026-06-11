package com.example.ims.dto;

import com.example.ims.entity.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateIncidentRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank String description,
        @NotNull Severity severity,
        @NotBlank @Size(max = 120) String impactedService,
        @NotBlank @Size(max = 120) String incidentCommander,
        @NotBlank @Size(max = 120) String communicationLead,
        @NotBlank @Size(max = 120) String technicalLead,
        @NotBlank @Size(max = 120) String reporter,
        @NotBlank @Size(max = 120) String teamLead,
        @NotBlank @Size(max = 120) String escalationManager,
        @NotBlank @Size(max = 120) String seniorManager,
        @NotNull LocalDateTime etaDueAt
) {
}
