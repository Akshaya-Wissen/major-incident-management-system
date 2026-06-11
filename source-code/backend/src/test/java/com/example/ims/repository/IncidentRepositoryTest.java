package com.example.ims.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ims.entity.IncidentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = {"/schema.sql", "/test-data.sql"})
class IncidentRepositoryTest {
    @Autowired
    private IncidentRepository incidentRepository;

    @Test
    void loadsSeededIncidentsByStatus() {
        assertThat(incidentRepository.count()).isEqualTo(4);
        assertThat(incidentRepository.findByStatusOrderByDetectedAtDesc(IncidentStatus.DELEGATED))
                .hasSize(1)
                .first()
                .extracting("impactedService")
                .isEqualTo("Checkout API");
    }
}
