package com.example.ims.dto;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalIncidents,
        long openIncidents,
        long resolvedIncidents,
        long closedIncidents,
        Map<String, Long> incidentsByStatus,
        List<IncidentSummaryResponse> recentIncidents
) {
}
