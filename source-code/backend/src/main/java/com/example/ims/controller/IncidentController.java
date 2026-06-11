package com.example.ims.controller;

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
import com.example.ims.entity.IncidentStatus;
import com.example.ims.service.IncidentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class IncidentController {
    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return incidentService.dashboard();
    }

    @GetMapping("/incidents")
    public List<IncidentSummaryResponse> list(@RequestParam(required = false) IncidentStatus status) {
        return incidentService.listIncidents(status);
    }

    @GetMapping("/incidents/{id}")
    public IncidentDetailResponse get(@PathVariable Long id) {
        return incidentService.getIncident(id);
    }

    @PostMapping("/incidents")
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentDetailResponse create(@Valid @RequestBody CreateIncidentRequest request) {
        return incidentService.createIncident(request);
    }

    @PatchMapping("/incidents/{id}/communicate")
    public IncidentDetailResponse communicate(@PathVariable Long id, @Valid @RequestBody TimelineRequest request) {
        return incidentService.communicate(id, request);
    }

    @PatchMapping("/incidents/{id}/assess")
    public IncidentDetailResponse assess(@PathVariable Long id, @Valid @RequestBody AssessmentRequest request) {
        return incidentService.assess(id, request);
    }

    @PatchMapping("/incidents/{id}/delegate")
    public IncidentDetailResponse delegate(@PathVariable Long id, @Valid @RequestBody DelegateRequest request) {
        return incidentService.delegate(id, request);
    }

    @PatchMapping("/incidents/{id}/resolve")
    public IncidentDetailResponse resolve(@PathVariable Long id, @Valid @RequestBody ResolutionRequest request) {
        return incidentService.resolve(id, request);
    }

    @PatchMapping("/incidents/{id}/rca")
    public IncidentDetailResponse rca(@PathVariable Long id, @Valid @RequestBody RcaRequest request) {
        return incidentService.createRca(id, request);
    }

    @PatchMapping("/incidents/{id}/close")
    public IncidentDetailResponse close(@PathVariable Long id, @Valid @RequestBody CloseIncidentRequest request) {
        return incidentService.close(id, request);
    }

    @PostMapping("/incidents/{id}/timeline")
    @ResponseStatus(HttpStatus.CREATED)
    public TimelineResponse addTimeline(@PathVariable Long id, @Valid @RequestBody TimelineRequest request) {
        return incidentService.addNote(id, request);
    }
}
