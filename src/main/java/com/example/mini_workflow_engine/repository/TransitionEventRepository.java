package com.example.mini_workflow_engine.repository;

// Imports our TransitionEvent entity
import com.example.mini_workflow_engine.model.TransitionEvent;

// Imports Spring Data's JpaRepository
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository for working with TransitionEvent objects (the system-wide
// event feed intended for a future webhook/notification dispatcher).
public interface TransitionEventRepository extends JpaRepository<TransitionEvent, Long> {

    // Returns every event belonging to one owner, oldest first — the
    // full activity feed across all of that owner's workflows/instances.
    List<TransitionEvent> findByOwnerIdOrderByOccurredAtAsc(String ownerId);
}
