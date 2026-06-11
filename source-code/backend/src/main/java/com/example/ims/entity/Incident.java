package com.example.ims.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "incidents")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private IncidentStatus status;

    @Column(nullable = false, length = 120)
    private String impactedService;

    @Column(nullable = false, length = 120)
    private String incidentCommander;

    @Column(nullable = false, length = 120)
    private String communicationLead;

    @Column(nullable = false, length = 120)
    private String technicalLead;

    @Column(nullable = false, length = 120)
    private String reporter;

    @Column(nullable = false, length = 120)
    private String teamLead;

    @Column(nullable = false, length = 120)
    private String escalationManager;

    @Column(nullable = false, length = 120)
    private String seniorManager;

    @Column(nullable = false, length = 120)
    private String currentOwner;

    @Column(nullable = false, length = 80)
    private String currentOwnerRole;

    @Column(nullable = false)
    private LocalDateTime detectedAt;

    @Column(nullable = false)
    private LocalDateTime etaDueAt;

    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    @OneToOne(mappedBy = "incident", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private IncidentAssessment assessment;

    @OneToOne(mappedBy = "incident", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private IncidentResolution resolution;

    @OneToOne(mappedBy = "incident", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private IncidentRca rca;

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentTimeline> timeline = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public String getImpactedService() {
        return impactedService;
    }

    public void setImpactedService(String impactedService) {
        this.impactedService = impactedService;
    }

    public String getIncidentCommander() {
        return incidentCommander;
    }

    public void setIncidentCommander(String incidentCommander) {
        this.incidentCommander = incidentCommander;
    }

    public String getCommunicationLead() {
        return communicationLead;
    }

    public void setCommunicationLead(String communicationLead) {
        this.communicationLead = communicationLead;
    }

    public String getTechnicalLead() {
        return technicalLead;
    }

    public void setTechnicalLead(String technicalLead) {
        this.technicalLead = technicalLead;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getTeamLead() {
        return teamLead;
    }

    public void setTeamLead(String teamLead) {
        this.teamLead = teamLead;
    }

    public String getEscalationManager() {
        return escalationManager;
    }

    public void setEscalationManager(String escalationManager) {
        this.escalationManager = escalationManager;
    }

    public String getSeniorManager() {
        return seniorManager;
    }

    public void setSeniorManager(String seniorManager) {
        this.seniorManager = seniorManager;
    }

    public String getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(String currentOwner) {
        this.currentOwner = currentOwner;
    }

    public String getCurrentOwnerRole() {
        return currentOwnerRole;
    }

    public void setCurrentOwnerRole(String currentOwnerRole) {
        this.currentOwnerRole = currentOwnerRole;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public LocalDateTime getEtaDueAt() {
        return etaDueAt;
    }

    public void setEtaDueAt(LocalDateTime etaDueAt) {
        this.etaDueAt = etaDueAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public IncidentAssessment getAssessment() {
        return assessment;
    }

    public void setAssessment(IncidentAssessment assessment) {
        this.assessment = assessment;
    }

    public IncidentResolution getResolution() {
        return resolution;
    }

    public void setResolution(IncidentResolution resolution) {
        this.resolution = resolution;
    }

    public IncidentRca getRca() {
        return rca;
    }

    public void setRca(IncidentRca rca) {
        this.rca = rca;
    }

    public List<IncidentTimeline> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<IncidentTimeline> timeline) {
        this.timeline = timeline;
    }
}
