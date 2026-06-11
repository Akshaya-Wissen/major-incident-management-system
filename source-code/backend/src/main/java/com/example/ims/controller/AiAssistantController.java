package com.example.ims.controller;

import com.example.ims.dto.AiChatRequest;
import com.example.ims.dto.AiChatResponse;
import com.example.ims.service.GeminiIncidentAssistantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {
    private final GeminiIncidentAssistantService assistantService;

    public AiAssistantController(GeminiIncidentAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/chat")
    public AiChatResponse chat(@Valid @RequestBody AiChatRequest request) {
        return assistantService.chat(request);
    }
}
