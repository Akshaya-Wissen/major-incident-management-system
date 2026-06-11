package com.example.ims.service;

import com.example.ims.dto.AssessmentRequest;
import com.example.ims.dto.CloseIncidentRequest;
import com.example.ims.dto.CreateIncidentRequest;
import com.example.ims.dto.DashboardResponse;
import com.example.ims.dto.DelegateRequest;
import com.example.ims.dto.IncidentDetailResponse;
import com.example.ims.dto.IncidentSummaryResponse;
import com.example.ims.dto.RcaRequest;
import com.example.ims.dto.ResolutionRequest;
import com.example.ims.dto.TimelineRequest;
import com.example.ims.dto.TimelineResponse;
import com.example.ims.entity.Incident;
import com.example.ims.entity.IncidentAssessment;
import com.example.ims.entity.IncidentRca;
import com.example.ims.entity.IncidentResolution;
import com.example.ims.entity.IncidentStatus;
import com.example.ims.entity.IncidentTimeline;
import com.example.ims.entity.TimelineEventType;
import com.example.ims.exception.InvalidIncidentTransitionException;
import com.example.ims.exception.ResourceNotFoundException;
import com.example.ims.repository.IncidentRepository;
import com.example.ims.repository.IncidentTimelineRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final IncidentTimelineRepository timelineRepository;
    private final IncidentMapper mapper;

    public IncidentService(
            IncidentRepository incidentRepository,
            IncidentTimelineRepository timelineRepository,
            IncidentMapper mapper
    ) {
        this.incidentRepository = incidentRepository;
        this.timelineRepository = timelineRepository;
        this.mapper = mapper;
    }

    public DashboardResponse dashboard() {
        List<IncidentSummaryResponse> recent = incidentRepository.findAllByOrderByDetectedAtDesc()
                .stream()
                .limit(5)
                .map(mapper::toSummary)
                .toList();

        Map<IncidentStatus, Long> counts = new EnumMap<>(IncidentStatus.class);
        Arrays.stream(IncidentStatus.values())
                .forEach(status -> counts.put(status, incidentRepository.countByStatus(status)));

        Map<String, Long> countsByName = counts.entrySet()
                .stream()
                .collect(java.util.stream.Collectors.toMap(entry -> entry.getKey().name(), Map.Entry::getValue));

        long total = incidentRepository.count();
        long closed = counts.get(IncidentStatus.CLOSED);
        long resolved = counts.get(IncidentStatus.RESOLVED) + counts.get(IncidentStatus.RCA);
        return new DashboardResponse(total, total - closed, resolved, closed, countsByName, recent);
    }

    public List<IncidentSummaryResponse> listIncidents(IncidentStatus status) {
        List<Incident> incidents = status == null
                ? incidentRepository.findAllByOrderByDetectedAtDesc()
                : incidentRepository.findByStatusOrderByDetectedAtDesc(status);
        return incidents.stream().map(mapper::toSummary).toList();
    }

    @Transactional
    public IncidentDetailResponse getIncident(Long id) {
        Incident incident = findIncident(id);
        return mapper.toDetail(incident, timelineRepository.findByIncidentIdOrderByOccurredAtAsc(id));
    }

    @Transactional
    public IncidentDetailResponse createIncident(CreateIncidentRequest request) {
        Incident incident = new Incident();
        incident.setTitle(request.title());
        incident.setDescription(request.description());
        incident.setSeverity(request.severity());
        incident.setStatus(IncidentStatus.DETECTED);
        incident.setImpactedService(request.impactedService());
        incident.setIncidentCommander(request.incidentCommander());
        incident.setCommunicationLead(request.communicationLead());
        incident.setTechnicalLead(request.technicalLead());
        incident.setReporter(request.reporter());
        incident.setTeamLead(request.teamLead());
        incident.setEscalationManager(request.escalationManager());
        incident.setSeniorManager(request.seniorManager());
        routeTo(incident, request.teamLead(), "Team Lead");
        incident.setDetectedAt(LocalDateTime.now());
        incident.setEtaDueAt(request.etaDueAt());
        Incident saved = incidentRepository.save(incident);
        addTimeline(saved, TimelineEventType.DETECTED, request.reporter(), "Incident raised with ETA " + request.etaDueAt() + " and routed to team lead " + request.teamLead() + ".");
        return getIncident(saved.getId());
    }

    @Transactional
    public IncidentDetailResponse communicate(Long id, TimelineRequest request) {
        Incident incident = findIncident(id);
        requireStatus(incident, IncidentStatus.DETECTED, IncidentStatus.COMMUNICATING);
        incident.setStatus(IncidentStatus.COMMUNICATING);
        addTimeline(incident, TimelineEventType.COMMUNICATION, request.actor(), request.message());
        return getIncident(id);
    }

    @Transactional
    public IncidentDetailResponse assess(Long id, AssessmentRequest request) {
        Incident incident = findIncident(id);
        requireStatus(incident, IncidentStatus.COMMUNICATING, IncidentStatus.ASSESSING);
        incident.setStatus(IncidentStatus.ASSESSING);

        IncidentAssessment assessment = incident.getAssessment() == null ? new IncidentAssessment() : incident.getAssessment();
        assessment.setIncident(incident);
        assessment.setCustomerImpact(request.customerImpact());
        assessment.setBusinessImpact(request.businessImpact());
        assessment.setAffectedUsers(request.affectedUsers());
        assessment.setCurrentHypothesis(request.currentHypothesis());
        assessment.setAssessedAt(LocalDateTime.now());
        incident.setAssessment(assessment);
        addTimeline(incident, TimelineEventType.ASSESSMENT, incident.getIncidentCommander(), "Impact assessed: " + request.currentHypothesis());
        return getIncident(id);
    }

    @Transactional
    public IncidentDetailResponse delegate(Long id, DelegateRequest request) {
        Incident incident = findIncident(id);
        requireStatus(incident, IncidentStatus.ASSESSING, IncidentStatus.DELEGATED);
        requireMissedEta(incident);
        incident.setIncidentCommander(request.incidentCommander());
        incident.setCommunicationLead(request.communicationLead());
        incident.setTechnicalLead(request.technicalLead());
        incident.setEscalationManager(request.escalationManager());
        incident.setSeniorManager(request.seniorManager());
        String previousOwnerRole = incident.getCurrentOwnerRole();
        if ("Team Lead".equals(previousOwnerRole)) {
            routeTo(incident, request.escalationManager(), "Escalation Manager");
        } else if ("Escalation Manager".equals(previousOwnerRole)) {
            routeTo(incident, request.seniorManager(), "Senior Manager");
        } else {
            throw new InvalidIncidentTransitionException("Incident is already at the highest approval level.");
        }
        incident.setStatus(IncidentStatus.DELEGATED);
        addTimeline(incident, TimelineEventType.DELEGATION, request.incidentCommander(), request.delegationNote() + " ETA missed; routed from " + previousOwnerRole + " to " + incident.getCurrentOwnerRole() + " " + incident.getCurrentOwner() + ".");
        return getIncident(id);
    }

    @Transactional
    public IncidentDetailResponse resolve(Long id, ResolutionRequest request) {
        Incident incident = findIncident(id);
        requireStatus(incident, IncidentStatus.DELEGATED, IncidentStatus.RESOLVED);
        if (!"Senior Manager".equals(incident.getCurrentOwnerRole())) {
            throw new InvalidIncidentTransitionException("Incident must be escalated to the senior manager before resolution.");
        }
        if (!request.resolvedBy().equals(incident.getSeniorManager())) {
            throw new InvalidIncidentTransitionException("Resolution must be submitted by senior manager " + incident.getSeniorManager() + ".");
        }
        LocalDateTime now = LocalDateTime.now();
        incident.setStatus(IncidentStatus.RESOLVED);
        incident.setResolvedAt(now);
        routeTo(incident, request.resolvedBy(), "Senior Manager");

        IncidentResolution resolution = incident.getResolution() == null ? new IncidentResolution() : incident.getResolution();
        resolution.setIncident(incident);
        resolution.setResolutionSummary(request.resolutionSummary());
        resolution.setMitigationSteps(request.mitigationSteps());
        resolution.setResolvedBy(request.resolvedBy());
        resolution.setResolvedAt(now);
        incident.setResolution(resolution);
        addTimeline(incident, TimelineEventType.RESOLUTION, request.resolvedBy(), request.resolutionSummary());
        return getIncident(id);
    }

    @Transactional
    public IncidentDetailResponse createRca(Long id, RcaRequest request) {
        Incident incident = findIncident(id);
        requireStatus(incident, IncidentStatus.RESOLVED, IncidentStatus.RCA);
        incident.setStatus(IncidentStatus.RCA);

        IncidentRca rca = incident.getRca() == null ? new IncidentRca() : incident.getRca();
        rca.setIncident(incident);
        rca.setRootCause(request.rootCause());
        rca.setContributingFactors(request.contributingFactors());
        rca.setCorrectiveActions(request.correctiveActions());
        rca.setPreventiveActions(request.preventiveActions());
        rca.setOwner(request.owner());
        rca.setDueDate(request.dueDate());
        rca.setApproved(request.approved());
        incident.setRca(rca);
        addTimeline(incident, TimelineEventType.RCA, request.owner(), "RCA drafted and approval state set to " + request.approved() + ".");
        return getIncident(id);
    }

    @Transactional
    public IncidentDetailResponse close(Long id, CloseIncidentRequest request) {
        Incident incident = findIncident(id);
        requireStatus(incident, IncidentStatus.RCA, IncidentStatus.CLOSED);
        if (incident.getRca() == null || !incident.getRca().isApproved()) {
            throw new InvalidIncidentTransitionException("Incident requires an approved RCA before closure.");
        }
        incident.setStatus(IncidentStatus.CLOSED);
        incident.setClosedAt(LocalDateTime.now());
        addTimeline(incident, TimelineEventType.CLOSURE, request.closedBy(), request.closureSummary());
        return getIncident(id);
    }

    @Transactional
    public TimelineResponse addNote(Long id, TimelineRequest request) {
        Incident incident = findIncident(id);
        return mapper.toTimeline(addTimeline(incident, request.eventType(), request.actor(), request.message()));
    }

    private Incident findIncident(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident " + id + " was not found."));
    }

    private void requireStatus(Incident incident, IncidentStatus expected, IncidentStatus target) {
        if (incident.getStatus() != expected && incident.getStatus() != target) {
            throw new InvalidIncidentTransitionException(
                    "Cannot move incident " + incident.getId() + " from " + incident.getStatus() + " to " + target + "."
            );
        }
    }

    private void requireMissedEta(Incident incident) {
        if (LocalDateTime.now().isBefore(incident.getEtaDueAt())) {
            throw new InvalidIncidentTransitionException(
                    "Escalation is allowed only after the ETA is missed. Current ETA: " + incident.getEtaDueAt() + "."
            );
        }
    }

    private IncidentTimeline addTimeline(Incident incident, TimelineEventType type, String actor, String message) {
        IncidentTimeline event = new IncidentTimeline();
        event.setIncident(incident);
        event.setEventType(type);
        event.setActor(actor);
        event.setMessage(message);
        event.setOccurredAt(LocalDateTime.now());
        return timelineRepository.save(event);
    }

    private void routeTo(Incident incident, String owner, String role) {
        incident.setCurrentOwner(owner);
        incident.setCurrentOwnerRole(role);
    }
}
