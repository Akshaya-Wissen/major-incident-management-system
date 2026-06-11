package com.example.ims.repository;

import com.example.ims.entity.IncidentAssessment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentAssessmentRepository extends JpaRepository<IncidentAssessment, Long> {
    Optional<IncidentAssessment> findByIncidentId(Long incidentId);
}
