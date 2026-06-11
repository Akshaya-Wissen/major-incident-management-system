package com.example.ims.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ims.dto.KnowledgeBaseRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = {"/schema.sql", "/test-data.sql"})
class KnowledgeBaseServiceTest {
    @Autowired
    private KnowledgeBaseService service;

    @Test
    void createsAndSearchesArticle() {
        service.create(new KnowledgeBaseRequest(
                "Status publisher recovery",
                "Runbook",
                "Recover delayed public status page updates.",
                "Drain stuck publisher jobs, restart workers, and verify status update latency.",
                "status,page,publisher"
        ));

        assertThat(service.search("publisher"))
                .extracting("title")
                .contains("Status publisher recovery");
    }
}
