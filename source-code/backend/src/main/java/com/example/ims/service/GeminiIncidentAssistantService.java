package com.example.ims.service;

import com.example.ims.dto.AiChatRequest;
import com.example.ims.dto.AiChatResponse;
import com.example.ims.dto.IncidentDetailResponse;
import com.example.ims.dto.TimelineResponse;
import com.example.ims.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeminiIncidentAssistantService {
    private static final Pattern INCIDENT_ID_PATTERN = Pattern.compile(
            "(?i)(?:inc[-\\s#]*|incident\\s*(?:id)?\\s*#?|ticket\\s*(?:id)?\\s*#?|id\\s*#?)(\\d+)"
    );
    private static final Pattern ONLY_ID_PATTERN = Pattern.compile("^\\s*#?(\\d+)\\s*$");

    private final IncidentService incidentService;
    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiIncidentAssistantService(
            IncidentService incidentService,
            @Value("${app.gemini.api-key:}") String apiKey,
            @Value("${app.gemini.model:gemini-3.1-flash-lite}") String model
    ) {
        this.incidentService = incidentService;
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public AiChatResponse chat(AiChatRequest request) {
        Long incidentId = resolveIncidentId(request);
        if (incidentId != null && isDirectDetailsQuestion(request.message())) {
            return getDirectIncidentDetails(incidentId);
        }

        if (apiKey == null || apiKey.isBlank()) {
            return new AiChatResponse("AI assistant is not configured. Set GEMINI_API_KEY in the backend environment and restart Spring Boot.");
        }

        String prompt = buildPrompt(request);
        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", systemInstruction()))
                ),
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of(
                        "temperature", 0.25,
                        "maxOutputTokens", 800
                )
        );

        Map<?, ?> response = restClient.post()
                .uri("/v1beta/models/{model}:generateContent", model)
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-goog-api-key", apiKey)
                .body(body)
                .retrieve()
                .body(Map.class);

        return new AiChatResponse(extractText(response));
    }

    private String buildPrompt(AiChatRequest request) {
        Long incidentId = resolveIncidentId(request);

        if (incidentId == null) {
            return "User question: " + request.message();
        }

        IncidentDetailResponse incident;
        try {
            incident = incidentService.getIncident(incidentId);
        } catch (ResourceNotFoundException exception) {
            return """
                    User question: %s

                    Incident lookup:
                    The user referenced incident ID %s, but no incident with that ID exists in the system.
                    Tell the user that the ID was not found and ask them to check the incident ID shown on the board.
                    """.formatted(request.message(), incidentId);
        }

        return """
                IMPORTANT:
                The application already fetched the incident record from its database.
                Do not ask the user to paste incident details.
                Do not say you lack access to the incident context.

                User question: %s

                Database incident context:
                ID: INC-%s
                Title: %s
                Description: %s
                Severity: %s
                Status: %s
                Service: %s
                Reporter: %s
                Team Lead: %s
                Escalation Manager: %s
                Senior Manager: %s
                Current Owner: %s (%s)
                Detected At: %s
                ETA: %s
                Resolved At: %s
                Closed At: %s
                Customer Impact: %s
                Business Impact: %s
                Affected Users: %s
                Current Hypothesis: %s
                Assessed At: %s
                Resolution: %s
                Mitigation Steps: %s
                Resolved By: %s
                Root Cause: %s
                Contributing Factors: %s
                Corrective Actions: %s
                Preventive Actions: %s
                RCA Owner: %s
                RCA Due Date: %s
                RCA Approved: %s
                Timeline:
                %s
                """.formatted(
                request.message(),
                incident.id(),
                incident.title(),
                incident.description(),
                incident.severity(),
                incident.status(),
                incident.impactedService(),
                incident.reporter(),
                incident.teamLead(),
                incident.escalationManager(),
                incident.seniorManager(),
                incident.currentOwner(),
                incident.currentOwnerRole(),
                incident.detectedAt(),
                incident.etaDueAt(),
                incident.resolvedAt(),
                incident.closedAt(),
                incident.assessment() == null ? "Pending" : incident.assessment().customerImpact(),
                incident.assessment() == null ? "Pending" : incident.assessment().businessImpact(),
                incident.assessment() == null ? "Pending" : incident.assessment().affectedUsers(),
                incident.assessment() == null ? "Pending" : incident.assessment().currentHypothesis(),
                incident.assessment() == null ? "Pending" : incident.assessment().assessedAt(),
                incident.resolution() == null ? "Pending" : incident.resolution().resolutionSummary(),
                incident.resolution() == null ? "Pending" : incident.resolution().mitigationSteps(),
                incident.resolution() == null ? "Pending" : incident.resolution().resolvedBy(),
                incident.rca() == null ? "Pending" : incident.rca().rootCause(),
                incident.rca() == null ? "Pending" : incident.rca().contributingFactors(),
                incident.rca() == null ? "Pending" : incident.rca().correctiveActions(),
                incident.rca() == null ? "Pending" : incident.rca().preventiveActions(),
                incident.rca() == null ? "Pending" : incident.rca().owner(),
                incident.rca() == null ? "Pending" : incident.rca().dueDate(),
                incident.rca() == null ? "Pending" : incident.rca().approved(),
                formatTimeline(incident.timeline())
        );
    }

    private Long resolveIncidentId(AiChatRequest request) {
        if (request.incidentId() != null) {
            return request.incidentId();
        }
        return extractIncidentId(request.message()).orElse(null);
    }

    private AiChatResponse getDirectIncidentDetails(Long incidentId) {
        try {
            IncidentDetailResponse incident = incidentService.getIncident(incidentId);
            return new AiChatResponse(formatDirectIncidentDetails(incident));
        } catch (ResourceNotFoundException exception) {
            return new AiChatResponse("I could not find INC-" + incidentId + ". Please check the incident ID shown on the board.");
        }
    }

    private boolean isDirectDetailsQuestion(String message) {
        if (message == null) {
            return false;
        }
        String normalized = message.toLowerCase();
        return normalized.contains("detail")
                || normalized.contains("summary")
                || normalized.contains("status")
                || normalized.contains("owner")
                || normalized.contains("eta")
                || normalized.contains("timeline")
                || normalized.contains("what happened")
                || normalized.contains("about");
    }

    private String formatDirectIncidentDetails(IncidentDetailResponse incident) {
        return """
                INC-%s: %s
                - Status: %s
                - Severity: %s
                - Service: %s
                - Current owner: %s (%s)
                - Raised by: %s
                - Team lead: %s
                - Escalation manager: %s
                - Senior manager: %s
                - Detected at: %s
                - ETA: %s
                - Resolved at: %s
                - Customer impact: %s
                - Business impact: %s
                - Resolution: %s
                - RCA: %s
                - Latest timeline: %s
                """.formatted(
                incident.id(),
                incident.title(),
                incident.status(),
                incident.severity(),
                incident.impactedService(),
                incident.currentOwner(),
                incident.currentOwnerRole(),
                incident.reporter(),
                incident.teamLead(),
                incident.escalationManager(),
                incident.seniorManager(),
                incident.detectedAt(),
                incident.etaDueAt(),
                incident.resolvedAt() == null ? "Pending" : incident.resolvedAt(),
                incident.assessment() == null ? "Pending" : incident.assessment().customerImpact(),
                incident.assessment() == null ? "Pending" : incident.assessment().businessImpact(),
                incident.resolution() == null ? "Pending" : incident.resolution().resolutionSummary(),
                incident.rca() == null ? "Pending" : incident.rca().rootCause(),
                latestTimeline(incident.timeline())
        );
    }

    private String latestTimeline(List<TimelineResponse> timeline) {
        if (timeline == null || timeline.isEmpty()) {
            return "No timeline events recorded.";
        }
        TimelineResponse event = timeline.get(timeline.size() - 1);
        return "%s | %s | %s: %s".formatted(event.occurredAt(), event.eventType(), event.actor(), event.message());
    }

    private Optional<Long> extractIncidentId(String message) {
        if (message == null || message.isBlank()) {
            return Optional.empty();
        }
        Matcher explicitMatch = INCIDENT_ID_PATTERN.matcher(message);
        if (explicitMatch.find()) {
            return parseLong(explicitMatch.group(1));
        }
        Matcher onlyIdMatch = ONLY_ID_PATTERN.matcher(message);
        if (onlyIdMatch.matches()) {
            return parseLong(onlyIdMatch.group(1));
        }
        return Optional.empty();
    }

    private Optional<Long> parseLong(String value) {
        try {
            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private String formatTimeline(List<TimelineResponse> timeline) {
        if (timeline == null || timeline.isEmpty()) {
            return "No timeline events recorded.";
        }
        return timeline.stream()
                .map(event -> "- %s | %s | %s: %s".formatted(
                        event.occurredAt(),
                        event.eventType(),
                        event.actor(),
                        event.message()
                ))
                .reduce((left, right) -> left + System.lineSeparator() + right)
                .orElse("No timeline events recorded.");
    }

    private String systemInstruction() {
        return """
                You are an AI incident management assistant embedded inside a Jira-like major incident tool.
                Help with triage, SLA risk, stakeholder updates, escalation reasoning, RCA drafting, and concise next actions.
                Users can reference incidents by visible IDs like INC-12, incident 12, ticket 12, or just 12.
                Use only the provided incident context. If data is missing, say what is missing instead of asking for the full incident details again.
                Keep responses operational, specific, and short. Prefer bullets for action plans.
                Do not claim that you performed actions in the system.
                """;
    }

    private String extractText(Map<?, ?> response) {
        if (response == null) {
            return "Gemini returned an empty response.";
        }
        Object candidates = response.get("candidates");
        if (!(candidates instanceof List<?> candidateList) || candidateList.isEmpty()) {
            return "Gemini did not return a candidate response.";
        }
        Object first = candidateList.get(0);
        if (!(first instanceof Map<?, ?> candidate)) {
            return "Gemini returned an unexpected response format.";
        }
        Object content = candidate.get("content");
        if (!(content instanceof Map<?, ?> contentMap)) {
            return "Gemini returned no content.";
        }
        Object parts = contentMap.get("parts");
        if (!(parts instanceof List<?> partList)) {
            return "Gemini returned no text parts.";
        }
        return partList.stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(part -> part.get("text"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .reduce("", String::concat)
                .trim();
    }
}
