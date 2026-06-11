package com.example.ims.dto;

import com.example.ims.entity.TimelineEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TimelineRequest(
        @NotNull TimelineEventType eventType,
        @NotBlank @Size(max = 140) String actor,
        @NotBlank String message
) {
}
