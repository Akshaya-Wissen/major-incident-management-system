package com.example.ims.repository;

import com.example.ims.entity.Incident;
import com.example.ims.entity.IncidentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatusOrderByDetectedAtDesc(IncidentStatus status);

    List<Incident> findAllByOrderByDetectedAtDesc();

    long countByStatus(IncidentStatus status);
}
