package com.example.ims.repository;

import com.example.ims.entity.KnowledgeBaseArticle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBaseArticle, Long> {
    List<KnowledgeBaseArticle> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrTagsContainingIgnoreCase(
            String title,
            String category,
            String tags
    );
}
