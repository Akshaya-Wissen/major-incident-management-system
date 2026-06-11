package com.example.ims.dto;

import com.example.ims.entity.IncidentStatus;
import com.example.ims.entity.Severity;
import java.time.LocalDateTime;

public record IncidentSummaryResponse(
        Long id,
        String title,
        Severity severity,
        IncidentStatus status,
        String impactedService,
        String incidentCommander,
        String communicationLead,
        String technicalLead,
        String teamLead,
        String escalationManager,
        String seniorManager,
        String currentOwner,
        String currentOwnerRole,
        LocalDateTime detectedAt,
        LocalDateTime etaDueAt,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt
) {
}
