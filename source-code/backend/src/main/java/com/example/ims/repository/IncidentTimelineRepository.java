package com.example.ims.repository;

import com.example.ims.entity.IncidentTimeline;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentTimelineRepository extends JpaRepository<IncidentTimeline, Long> {
    List<IncidentTimeline> findByIncidentIdOrderByOccurredAtAsc(Long incidentId);
}
