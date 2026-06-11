package com.example.ims.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.ims.dto.AssessmentRequest;
import com.example.ims.dto.CloseIncidentRequest;
import com.example.ims.dto.CreateIncidentRequest;
import com.example.ims.dto.DelegateRequest;
import com.example.ims.dto.RcaRequest;
import com.example.ims.dto.ResolutionRequest;
import com.example.ims.dto.TimelineRequest;
import com.example.ims.entity.IncidentStatus;
import com.example.ims.entity.Severity;
import com.example.ims.entity.TimelineEventType;
import com.example.ims.exception.InvalidIncidentTransitionException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = {"/schema.sql", "/test-data.sql"})
class IncidentServiceTest {
    @Autowired
    private IncidentService incidentService;

    @Test
    void createsIncidentInDetectedStateWithTimeline() {
        var created = incidentService.createIncident(new CreateIncidentRequest(
                "Search outage",
                "Search results are failing for all users.",
                Severity.SEV1,
                "Search API",
                "Priya Menon",
                "Arjun Das",
                "Fatima Ali",
                "NOC Analyst",
                "Priya Menon",
                "Rahul Bose",
                "Sana Khan",
                LocalDateTime.now().plusHours(2)
        ));

        assertThat(created.status()).isEqualTo(IncidentStatus.DETECTED);
        assertThat(created.etaDueAt()).isAfter(LocalDateTime.now());
        assertThat(created.reporter()).isEqualTo("NOC Analyst");
        assertThat(created.currentOwner()).isEqualTo("Priya Menon");
        assertThat(created.currentOwnerRole()).isEqualTo("Team Lead");
        assertThat(created.timeline()).hasSize(1);
        assertThat(created.timeline().get(0).eventType()).isEqualTo(TimelineEventType.DETECTED);
    }

    @Test
    void rejectsEscalationBeforeEtaIsMissed() {
        var created = incidentService.createIncident(new CreateIncidentRequest(
                "Profile API errors",
                "Profile updates are intermittently failing.",
                Severity.SEV2,
                "Profile API",
                "Maya Singh",
                "Maya Singh",
                "Anika Rao",
                "Service Desk",
                "Maya Singh",
                "Anika Rao",
                "Nikhil Batra",
                LocalDateTime.now().plusHours(4)
        ));

        incidentService.communicate(created.id(), new TimelineRequest(
                TimelineEventType.COMMUNICATION,
                "Maya Singh",
                "Stakeholder update posted."
        ));
        incidentService.assess(created.id(), new AssessmentRequest(
                "Customers may see failed profile updates.",
                "Support tickets may increase.",
                250,
                "Profile write dependency is intermittently timing out."
        ));

        assertThatThrownBy(() -> incidentService.delegate(created.id(), new DelegateRequest(
                "Maya Singh",
                "Maya Singh",
                "Anika Rao",
                "Anika Rao",
                "Nikhil Batra",
                "Escalating before ETA."
        ))).isInstanceOf(InvalidIncidentTransitionException.class);
    }

    @Test
    void supportsAssessmentAfterCommunication() {
        var communicated = incidentService.communicate(4L, new TimelineRequest(
                TimelineEventType.COMMUNICATION,
                "Om Prakash",
                "Second stakeholder update posted."
        ));
        assertThat(communicated.status()).isEqualTo(IncidentStatus.COMMUNICATING);

        var assessed = incidentService.assess(4L, new AssessmentRequest(
                "Customers may see delayed status updates.",
                "Support teams need manual communications.",
                1200,
                "Status publisher queue worker is lagging."
        ));

        assertThat(assessed.status()).isEqualTo(IncidentStatus.ASSESSING);
        assertThat(assessed.assessment().affectedUsers()).isEqualTo(1200);
    }

    @Test
    void resolvesCreatesRcaAndClosesLifecycle() {
        incidentService.delegate(1L, new DelegateRequest(
                "Aarav Mehta",
                "Neha Rao",
                "Vikram Shah",
                "Meera Joshi",
                "Rehan Kapur",
                "ETA missed at escalation manager level."
        ));
        incidentService.resolve(1L, new ResolutionRequest(
                "Connection pool size rolled back and errors normalized.",
                "Rolled back adapter config and drained stuck workers.",
                "Rehan Kapur"
        ));
        var rca = incidentService.createRca(1L, new RcaRequest(
                "Adapter pool size was reduced below peak checkout concurrency.",
                "Load test profile did not include flash sale traffic.",
                "Restore production pool limits and add checkout concurrency tests.",
                "Add canary guardrail for pool saturation before full rollout.",
                "Aarav Mehta",
                LocalDate.now().plusDays(7),
                true
        ));
        var closed = incidentService.close(1L, new CloseIncidentRequest(
                "Aarav Mehta",
                "Closed after approved RCA and preventive actions were assigned."
        ));

        assertThat(rca.status()).isEqualTo(IncidentStatus.RCA);
        assertThat(closed.status()).isEqualTo(IncidentStatus.CLOSED);
        assertThat(closed.closedAt()).isNotNull();
    }

    @Test
    void rejectsResolutionFromRoleBelowSeniorManager() {
        assertThatThrownBy(() -> incidentService.resolve(1L, new ResolutionRequest(
                "Connection pool size rolled back and errors normalized.",
                "Rolled back adapter config and drained stuck workers.",
                "Vikram Shah"
        ))).isInstanceOf(InvalidIncidentTransitionException.class);
    }

    @Test
    void rejectsClosureWithoutApprovedRca() {
        assertThatThrownBy(() -> incidentService.close(2L, new CloseIncidentRequest(
                "Maya Singh",
                "Attempting to close too early."
        ))).isInstanceOf(InvalidIncidentTransitionException.class);
    }
}
