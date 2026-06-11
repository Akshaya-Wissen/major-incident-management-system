package com.example.ims.dto;

import com.example.ims.entity.TimelineEventType;
import java.time.LocalDateTime;

public record TimelineResponse(
        Long id,
        TimelineEventType eventType,
        String actor,
        String message,
        LocalDateTime occurredAt
) {
}
