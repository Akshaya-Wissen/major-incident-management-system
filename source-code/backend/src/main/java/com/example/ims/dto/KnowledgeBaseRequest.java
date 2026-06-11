package com.example.ims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record KnowledgeBaseRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 80) String category,
        @NotBlank String summary,
        @NotBlank String content,
        @NotBlank @Size(max = 200) String tags
) {
}
