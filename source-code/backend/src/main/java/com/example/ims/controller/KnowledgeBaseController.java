package com.example.ims.controller;

import com.example.ims.dto.KnowledgeBaseRequest;
import com.example.ims.dto.KnowledgeBaseResponse;
import com.example.ims.service.KnowledgeBaseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge-base")
public class KnowledgeBaseController {
    private final KnowledgeBaseService service;

    public KnowledgeBaseController(KnowledgeBaseService service) {
        this.service = service;
    }

    @GetMapping
    public List<KnowledgeBaseResponse> list(@RequestParam(required = false) String q) {
        return service.search(q);
    }

    @GetMapping("/{id}")
    public KnowledgeBaseResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KnowledgeBaseResponse create(@Valid @RequestBody KnowledgeBaseRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public KnowledgeBaseResponse update(@PathVariable Long id, @Valid @RequestBody KnowledgeBaseRequest request) {
        return service.update(id, request);
    }
}
