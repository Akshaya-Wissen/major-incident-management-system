package com.example.ims.dto;

import java.time.LocalDateTime;

public record KnowledgeBaseResponse(
        Long id,
        String title,
        String category,
        String summary,
        String content,
        String tags,
        LocalDateTime updatedAt
) {
}
