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
import java.time.LocalDateTime;

@Entity
@Table(name = "incident_assessment")
public class IncidentAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false, unique = true)
    private Incident incident;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String customerImpact;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String businessImpact;

    @Column(nullable = false)
    private Integer affectedUsers;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String currentHypothesis;

    @Column(nullable = false)
    private LocalDateTime assessedAt;

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

    public String getCustomerImpact() {
        return customerImpact;
    }

    public void setCustomerImpact(String customerImpact) {
        this.customerImpact = customerImpact;
    }

    public String getBusinessImpact() {
        return businessImpact;
    }

    public void setBusinessImpact(String businessImpact) {
        this.businessImpact = businessImpact;
    }

    public Integer getAffectedUsers() {
        return affectedUsers;
    }

    public void setAffectedUsers(Integer affectedUsers) {
        this.affectedUsers = affectedUsers;
    }

    public String getCurrentHypothesis() {
        return currentHypothesis;
    }

    public void setCurrentHypothesis(String currentHypothesis) {
        this.currentHypothesis = currentHypothesis;
    }

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(LocalDateTime assessedAt) {
        this.assessedAt = assessedAt;
    }
}
