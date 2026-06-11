package com.example.ims.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = {"/schema.sql", "/test-data.sql"})
class KnowledgeBaseRepositoryTest {
    @Autowired
    private KnowledgeBaseRepository repository;

    @Test
    void searchesArticlesByTitleCategoryOrTags() {
        assertThat(repository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrTagsContainingIgnoreCase(
                "cache",
                "cache",
                "cache"
        )).extracting("title").contains("Identity gateway cache recovery");
    }
}
