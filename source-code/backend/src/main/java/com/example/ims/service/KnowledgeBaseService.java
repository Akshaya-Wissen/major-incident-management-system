package com.example.ims.service;

import com.example.ims.dto.KnowledgeBaseRequest;
import com.example.ims.dto.KnowledgeBaseResponse;
import com.example.ims.entity.KnowledgeBaseArticle;
import com.example.ims.exception.ResourceNotFoundException;
import com.example.ims.repository.KnowledgeBaseRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeBaseService {
    private final KnowledgeBaseRepository repository;

    public KnowledgeBaseService(KnowledgeBaseRepository repository) {
        this.repository = repository;
    }

    public List<KnowledgeBaseResponse> search(String query) {
        List<KnowledgeBaseArticle> articles = query == null || query.isBlank()
                ? repository.findAll()
                : repository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrTagsContainingIgnoreCase(query, query, query);
        return articles.stream().map(this::toResponse).toList();
    }

    public KnowledgeBaseResponse get(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge base article " + id + " was not found."));
    }

    @Transactional
    public KnowledgeBaseResponse create(KnowledgeBaseRequest request) {
        KnowledgeBaseArticle article = new KnowledgeBaseArticle();
        apply(request, article);
        return toResponse(repository.save(article));
    }

    @Transactional
    public KnowledgeBaseResponse update(Long id, KnowledgeBaseRequest request) {
        KnowledgeBaseArticle article = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge base article " + id + " was not found."));
        apply(request, article);
        return toResponse(article);
    }

    private void apply(KnowledgeBaseRequest request, KnowledgeBaseArticle article) {
        article.setTitle(request.title());
        article.setCategory(request.category());
        article.setSummary(request.summary());
        article.setContent(request.content());
        article.setTags(request.tags());
        article.setUpdatedAt(LocalDateTime.now());
    }

    private KnowledgeBaseResponse toResponse(KnowledgeBaseArticle article) {
        return new KnowledgeBaseResponse(
                article.getId(),
                article.getTitle(),
                article.getCategory(),
                article.getSummary(),
                article.getContent(),
                article.getTags(),
                article.getUpdatedAt()
        );
    }
}
