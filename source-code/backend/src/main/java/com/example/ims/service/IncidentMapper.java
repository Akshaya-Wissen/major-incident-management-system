package com.example.ims.service;

import com.example.ims.dto.IncidentDetailResponse;
import com.example.ims.dto.IncidentSummaryResponse;
import com.example.ims.dto.TimelineResponse;
import com.example.ims.entity.Incident;
import com.example.ims.entity.IncidentAssessment;
import com.example.ims.entity.IncidentRca;
import com.example.ims.entity.IncidentResolution;
import com.example.ims.entity.IncidentTimeline;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {
    public IncidentSummaryResponse toSummary(Incident incident) {
        return new IncidentSummaryResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getImpactedService(),
                incident.getIncidentCommander(),
                incident.getCommunicationLead(),
                incident.getTechnicalLead(),
                incident.getTeamLead(),
                incident.getEscalationManager(),
                incident.getSeniorManager(),
                incident.getCurrentOwner(),
                incident.getCurrentOwnerRole(),
                incident.getDetectedAt(),
                incident.getEtaDueAt(),
                incident.getResolvedAt(),
                incident.getClosedAt()
        );
    }

    public IncidentDetailResponse toDetail(Incident incident, List<IncidentTimeline> timeline) {
        return new IncidentDetailResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getImpactedService(),
                incident.getIncidentCommander(),
                incident.getCommunicationLead(),
                incident.getTechnicalLead(),
                incident.getReporter(),
                incident.getTeamLead(),
                incident.getEscalationManager(),
                incident.getSeniorManager(),
                incident.getCurrentOwner(),
                incident.getCurrentOwnerRole(),
                incident.getDetectedAt(),
                incident.getEtaDueAt(),
                incident.getResolvedAt(),
                incident.getClosedAt(),
                toAssessment(incident.getAssessment()),
                toResolution(incident.getResolution()),
                toRca(incident.getRca()),
                timeline.stream().map(this::toTimeline).toList()
        );
    }

    public TimelineResponse toTimeline(IncidentTimeline event) {
        return new TimelineResponse(
                event.getId(),
                event.getEventType(),
                event.getActor(),
                event.getMessage(),
                event.getOccurredAt()
        );
    }

    private IncidentDetailResponse.AssessmentView toAssessment(IncidentAssessment assessment) {
        if (assessment == null) {
            return null;
        }
        return new IncidentDetailResponse.AssessmentView(
                assessment.getCustomerImpact(),
                assessment.getBusinessImpact(),
                assessment.getAffectedUsers(),
                assessment.getCurrentHypothesis(),
                assessment.getAssessedAt()
        );
    }

    private IncidentDetailResponse.ResolutionView toResolution(IncidentResolution resolution) {
        if (resolution == null) {
            return null;
        }
        return new IncidentDetailResponse.ResolutionView(
                resolution.getResolutionSummary(),
                resolution.getMitigationSteps(),
                resolution.getResolvedBy(),
                resolution.getResolvedAt()
        );
    }

    private IncidentDetailResponse.RcaView toRca(IncidentRca rca) {
        if (rca == null) {
            return null;
        }
        return new IncidentDetailResponse.RcaView(
                rca.getRootCause(),
                rca.getContributingFactors(),
                rca.getCorrectiveActions(),
                rca.getPreventiveActions(),
                rca.getOwner(),
                rca.getDueDate(),
                rca.isApproved()
        );
    }
}
