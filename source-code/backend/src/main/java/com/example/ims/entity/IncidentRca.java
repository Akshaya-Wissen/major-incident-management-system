package com.example.ims.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "incident_rca")
public class IncidentRca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false, unique = true)
    private Incident incident;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String rootCause;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contributingFactors;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String correctiveActions;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String preventiveActions;

    @Column(nullable = false, length = 120)
    private String owner;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private boolean approved;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getContributingFactors() {
        return contributingFactors;
    }

    public void setContributingFactors(String contributingFactors) {
        this.contributingFactors = contributingFactors;
    }

    public String getCorrectiveActions() {
        return correctiveActions;
    }

    public void setCorrectiveActions(String correctiveActions) {
        this.correctiveActions = correctiveActions;
    }

    public String getPreventiveActions() {
        return preventiveActions;
    }

    public void setPreventiveActions(String preventiveActions) {
        this.preventiveActions = preventiveActions;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
