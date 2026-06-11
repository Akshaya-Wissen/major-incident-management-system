package com.example.ims.repository;

import com.example.ims.entity.IncidentRca;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRcaRepository extends JpaRepository<IncidentRca, Long> {
    Optional<IncidentRca> findByIncidentId(Long incidentId);
}
