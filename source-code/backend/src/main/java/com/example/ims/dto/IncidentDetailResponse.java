package com.example.ims.dto;

import com.example.ims.entity.IncidentStatus;
import com.example.ims.entity.Severity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record IncidentDetailResponse(
        Long id,
        String title,
        String description,
        Severity severity,
        IncidentStatus status,
        String impactedService,
        String incidentCommander,
        String communicationLead,
        String technicalLead,
        String reporter,
        String teamLead,
        String escalationManager,
        String seniorManager,
        String currentOwner,
        String currentOwnerRole,
        LocalDateTime detectedAt,
        LocalDateTime etaDueAt,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt,
        AssessmentView assessment,
        ResolutionView resolution,
        RcaView rca,
        List<TimelineResponse> timeline
) {
    public record AssessmentView(
            String customerImpact,
            String businessImpact,
            Integer affectedUsers,
            String currentHypothesis,
            LocalDateTime assessedAt
    ) {
    }

    public record ResolutionView(
            String resolutionSummary,
            String mitigationSteps,
            String resolvedBy,
            LocalDateTime resolvedAt
    ) {
    }

    public record RcaView(
            String rootCause,
            String contributingFactors,
            String correctiveActions,
            String preventiveActions,
            String owner,
            LocalDate dueDate,
            boolean approved
    ) {
    }
}
