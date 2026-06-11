package com.example.ims.repository;

import com.example.ims.entity.IncidentResolution;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentResolutionRepository extends JpaRepository<IncidentResolution, Long> {
    Optional<IncidentResolution> findByIncidentId(Long incidentId);
}
